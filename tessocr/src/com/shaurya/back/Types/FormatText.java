package com.shaurya.back.Types;

public class FormatText {
	
	
	private String text;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private int Size;
	// Setter & Getter for Text

	public void setText(String str)
	{
		text = str;
	}

	public String getText()
	{
		return text;
	}
	
	// Setter & Getter for Bold

	public void setBold(boolean B)
	{
		bold = B;
	}
	
	public boolean getBold()
	{
		return bold;
	}
	
	// Setter & Getter for Italic

	public void setItalic(boolean B)
	{
		italic = B;
	}
	
	public boolean getItalic()
	{
		return italic;
	}
	
	// Setter & Getter for Underline

	public void setUnderline(boolean B)
	{
		underline = B;
	}
	
	public boolean getUnderline()
	{
		return underline;
	}
	
	// Setter & Getter for Size

	public void setSize(int S)
	{
		Size = S;
	}
	public int getSize()
	{
		return Size;
	}

}
