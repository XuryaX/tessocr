package com.shaurya.back.Spelling;

import com.shaurya.back.Exceptions.SpellCheckException;
import com.shaurya.back.Types.FormatText;

import pt.tumba.spell.SpellChecker;

public class Jaspell implements SpellCheck	{

	/**
	 * @uml.property  name="dict"
	 */
	private String dict;
	/**
	 * @uml.property  name="check"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private SpellChecker check;
	
	public Jaspell() throws SpellCheckException
	{
		this("");
	}
	public Jaspell(String str) throws SpellCheckException
	{
		str="lib//english.txt";
		dict = str;
		check = new SpellChecker();
		try {
			check.initialize(dict);
		} catch (Exception e) {
			throw new SpellCheckException("Jaspell Problem - Initializing Dict  : "+e.getMessage());
		}
	}

	
	private String replace(String word,String prefix,String suffix)
	{
		return word.replace(prefix, "").replace(suffix," ");
	}
	

	public FormatText correct(FormatText str)
	{
		String wordres,strres;
		strres="";
		for(String word:str.getText().split(" "))
		{
			wordres=check.spellCheckWord(word);
			if(wordres.contains("<suggestion>"))
			{
				strres+=replace(wordres,"<suggestion>","</suggestion>");
			}
			else if(wordres.contains("<plain>"))
			{
				strres+=replace(wordres,"<plain>","</plain>");
			}
			else
			{
				strres+=replace(wordres,"<misspell>","</misspell>");
			}
		}
		str.setText(strres);
		return str;
	}
}