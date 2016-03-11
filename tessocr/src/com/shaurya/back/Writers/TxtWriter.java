package com.shaurya.back.Writers;

import java.io.FileWriter;
import java.io.IOException;

import com.shaurya.back.Types.FormatText;

public class TxtWriter implements WriterModel {
	
	/**
	 * @uml.property  name="file"
	 */
	FileWriter file;
	@Override
	public void openfile(String s) throws IOException {
		file = new FileWriter(s);
		
	}

	@Override
	public void write(FormatText s) throws IOException {
		file.append(s.getText());
		file.append("\n");
	}

	@Override
	public void close() throws IOException {
		file.close();		
	}

}
