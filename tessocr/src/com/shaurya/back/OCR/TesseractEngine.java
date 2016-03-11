package com.shaurya.back.OCR;

import java.awt.image.BufferedImage;



import com.shaurya.back.Exceptions.OCRException;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TesseractEngine implements OCRModel {

	private Tesseract instance;
	
	public TesseractEngine(){
		instance = Tesseract.getInstance();
		instance.setDatapath("lib//tessdata");
		instance.setPageSegMode(6);
		
	}
	
	@Override
	public String getResult(BufferedImage img) throws OCRException{
		String str;
		try {
			str = instance.doOCR(img);
		} catch (TesseractException e) {
			throw new OCRException(e.getMessage());
		}
		
		return str.trim();
	}

}
