package com.shaurya.back.OCR;

import java.awt.image.BufferedImage;

import com.shaurya.back.Exceptions.OCRException;


public interface OCRModel {

		public String getResult(BufferedImage img) throws OCRException;
}
