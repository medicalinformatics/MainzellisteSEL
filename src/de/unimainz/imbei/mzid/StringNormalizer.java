package de.unimainz.imbei.mzid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class StringNormalizer implements FieldTransformer<PlainTextField, PlainTextField> {

	/** Delimiters to remove from start and end.
	 * 
	 */
	private static char delimiterChars[] = {' ', '.', ':', ',', ';', '-', '\''};

	/** Characters which to replace (umlauts) 
	 * {ä, Ä, ö, Ö, ü, Ü, ß}
	 * */
	private static char umlauts[] = {'\u00e4', '\u00c4', '\u00f6', '\u00d6', '\u00fc', '\u00dc', '\u00df'};

	/** Replacement for umlauts */
	private static String umlautReplacement[] = {"ae", "AE", "oe", "OE", "ue", "UE", "ss"};  
	
	/** Delimiters to recognize when decomposing Names as Set.
	 * Used internally for efficient access to delimiters.
	 */
	private Set<Character> delimiters;
	
	/** Mapping between umlauts and their replacement */
	private Map<Character, String> umlautReplacementMap;


	public StringNormalizer()
	{
		int i;
		this.delimiters = new HashSet<Character>();
		for (i = 0; i < delimiterChars.length; i++)
		{
			delimiters.add(new Character(delimiterChars[i]));
		}
		
		this.umlautReplacementMap = new HashMap<Character, String>();
		for (i = 0; i < umlauts.length; i++ )
		{
			umlautReplacementMap.put(new Character(umlauts[i]), umlautReplacement[i]);
		}
	}
	

	/**
	 * Normalize a PlainTextField. Normalization includes:
	 * <ul>
	 * 	<li> removal of leading and trailing delimiters,
	 *  <li> conversion of Umlauts.
	 * 	<li> conversion to upper case,
	 * <ul>
	 * @param input
	 * @return
	 */
	public PlainTextField transform(PlainTextField input)
	{
		// TODO: ungültige Zeichen filtern
		String inputStr = input.toString();
		StringBuffer resultString;

		// Copy into new String, omitting leading and trainling delimiters
		int start, end;
		for (start = 0; delimiters.contains(inputStr.charAt(start)); start++);
		for (end = inputStr.length() - 1; end >= start && delimiters.contains(inputStr.charAt(end)); end--);
		
		resultString = new StringBuffer(inputStr.substring(start,  end + 1));
		
		// if resultString is empty, nothing more to do 
		if (resultString.length() == 0) return new PlainTextField("");

		// convert umlauts
		Character thisChar;
		for (int pos = 0; pos < resultString.length(); pos++)
		{
			thisChar = new Character(resultString.charAt(pos));
			if (umlautReplacementMap.containsKey(thisChar))
			{
				resultString.replace(pos,  pos + 1, umlautReplacementMap.get(thisChar));
			}
		}
		
		// convert to uppercase
		PlainTextField output = new PlainTextField(resultString.toString().toUpperCase());
		return output;
		
	}

}
