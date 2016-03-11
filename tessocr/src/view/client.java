package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.shaurya.back.ImageToDocument;
import com.shaurya.back.Exceptions.OCRException;
import com.shaurya.back.Exceptions.SpellCheckException;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class client {
	
	public static void main(String args[]){
	       FileInputStream in;
		try {
			File file = new File("F:\\asd.jpg");
			in = new FileInputStream(file);
			
		       JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder( in );

		       BufferedImage image = decoder.decodeAsBufferedImage();

		       in.close();
		       
			   ImageToDocument change = new ImageToDocument(image);
			   
			   change.transform(1,file.getParent()+"\\");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImageFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OCRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SpellCheckException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		
	}

}
