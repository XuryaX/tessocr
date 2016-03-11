package com.shaurya.back.Types;

import java.awt.image.BufferedImage;

public class RegionImage {
	
	private BufferedImage image;
	
	private double density;
	
	public RegionImage(BufferedImage img,double d){
		image=img;
		density=d;
	}
	
	public BufferedImage image(){
		return image;
	}
	
	public int height(){
		return image.getHeight();
	}
	
	public int width(){
		return image.getWidth();
	}
	
	public double density(){
		return density;
	}
}
