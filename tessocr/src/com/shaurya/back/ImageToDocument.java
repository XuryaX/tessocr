package com.shaurya.back;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.shaurya.back.Exceptions.OCRException;
import com.shaurya.back.Exceptions.SpellCheckException;
import com.shaurya.back.OCR.OCRModel;
import com.shaurya.back.OCR.TesseractEngine;
import com.shaurya.back.Spelling.Jaspell;
import com.shaurya.back.Spelling.SpellCheck;
import com.shaurya.back.Types.FormatText;
import com.shaurya.back.Types.RegionImage;
import com.shaurya.back.Writers.DocWriter;
import com.shaurya.back.Writers.TxtWriter;
import com.shaurya.back.Writers.WriterModel;


public class ImageToDocument {
	
	private ArrayList<RegionImage> textimage;
	
	double average_density;
	int mad;
	int median;
	
	public ImageToDocument(ArrayList<RegionImage> textimage){
			this.textimage = textimage;
			
			median = Statistical.median(textimage);
			mad = Statistical.mad(textimage, median)+2;
			
			avdens();
	}
	
	public ImageToDocument(BufferedImage image)	{
		GetImageText textget = new GetImageText( image );
		
		this.textimage = textget.getRegionImage();
		
		median = Statistical.median(textimage);
		mad = Statistical.mad(textimage, median)+1;
		
		avdens();
	}
	
	public void transform(int optype) throws OCRException, FileNotFoundException, IOException, SpellCheckException	{
		ArrayList<FormatText> content = identify();
		draft(content,optype,"");
	}
	
	public void transform(int optype,String path) throws OCRException, FileNotFoundException, IOException, SpellCheckException	{
		ArrayList<FormatText> content = identify();
		draft(content,optype,path);
	}
	
	private void avdens()	{
		average_density=0;
		for(int i=0;i<textimage.size();i++){
			RegionImage box = (RegionImage) textimage.get(i);
			average_density +=box.density();
		}
		average_density /=textimage.size();
		
	}

	private int classify(RegionImage textregion)
	{
		int height,diff,choice;
		height = textregion.height();
		diff = height - median;
		choice = (int) Math.floor(diff/mad);
		
		switch(choice){
		case -3:
			return 6;
		case -2:
			return 8;
		case -1:
			return 10;
		case 0:
			return 11;
		case 1:
			return 14;
		case 2:
			return 18;
		case 3:
			return 21;
		default:
			if(choice>0)
				return 28;
			else
				return 3;
		
		}
	}
	
	private ArrayList<FormatText> identify() throws OCRException{
		
		OCRModel engine = new TesseractEngine();
		
		ArrayList<FormatText> content = new ArrayList<FormatText>();
		RegionImage region;
		
		for(int i=0;i<textimage.size();i++)
		{
			FormatText s = new FormatText();
			region = textimage.get(i);
			
			if(region.density()>average_density)
				s.setBold(true);
			else
				s.setBold(false);
			s.setSize(classify(region));
			s.setText(engine.getResult(region.image()));
			content.add(s);
		}
			return content;
	}

	private void draft(ArrayList<FormatText> content,int optype,String path) throws FileNotFoundException, IOException, SpellCheckException {
		FormatText line;
		WriterModel write;
		SpellCheck checker = new Jaspell();
		
		switch(optype)
		{
		case 1:
			write  =  new DocWriter();
			write.openfile(path+"result.doc");
			break;
		case 2:
			write  =  new TxtWriter();
			write.openfile(path+"result.txt");
			break;
		default:
			write  =  new TxtWriter();
			write.openfile(path+"result.txt");
		}
		
		for(int i=0;i<content.size();i++)
		{
			line = checker.correct(content.get(i));
			write.write(line);
		}
		write.close();
	}


}
