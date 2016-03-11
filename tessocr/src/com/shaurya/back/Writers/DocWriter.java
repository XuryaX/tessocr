package com.shaurya.back.Writers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.shaurya.back.Types.FormatText;

import org.apache.poi.xwpf.usermodel.XWPFDocument;   
import org.apache.poi.xwpf.usermodel.XWPFParagraph;   
import org.apache.poi.xwpf.usermodel.XWPFRun;   


public class DocWriter implements WriterModel {


	XWPFDocument document;
	XWPFParagraph paragraph;
	FileOutputStream fos;
	String fontFamily;
	
	public DocWriter()
	{
		document = new XWPFDocument();
		paragraph = document.createParagraph();
		fontFamily="Arial";
	}
	@Override
	public void openfile(String s) throws FileNotFoundException {
		fos = new FileOutputStream(s);
	}

	@Override
	public void write(FormatText s) {
		
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setBold(false);
        paragraphRun.setItalic(false);
        paragraphRun.setFontSize(s.getSize());
        paragraphRun.setFontFamily(fontFamily);
        
        paragraphRun.setText(s.getText());
        paragraphRun.addBreak();
	}


	@Override
	public void close() throws IOException {
			document.write(fos);
			fos.close();
	}

}
