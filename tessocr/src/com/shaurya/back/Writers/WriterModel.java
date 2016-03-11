package com.shaurya.back.Writers;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.shaurya.back.Types.FormatText;


public interface WriterModel {
	
	public void openfile(String s) throws FileNotFoundException, IOException;
	
	public void write(FormatText s) throws IOException;
	
	public void close() throws IOException;

}
