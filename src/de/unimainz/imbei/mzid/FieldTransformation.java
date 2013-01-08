package de.unimainz.imbei.mzid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Deprecated

/**
 * This class provides functions for preprocessing input fields, such as conversion of umlauts, decomposition of names,
 * generation of phonetic codes.
 * @author borg
 *
 */
public class FieldTransformation {

	/** Delimiters to recognize when decomposing Names.
	 * 
	 */
	private static char delimiterChars[] = {' ', '.', ':', ',', ';', '-', '\''};
	
	/** Characters which to replace (umlauts) 
	 * {�, �, �, �, �, �, �}
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
	
	public FieldTransformation()
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
	 * @param Input field.
	 * @return The transformed field.
	 */
	public PlainTextField normalizeString(PlainTextField input)
	{
		// TODO: ung�ltige Zeichen filtern
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
	
	/**
	 * Decompose surname field into components.
	 * The field is decomposed into tokens, seperated by the delimiters defined
	 * by DELIMITERS.
	 * @param Input fields.
	 * @return The decomposed field.
	 */
	public static CompoundField<PlainTextField> decomposeSurname(PlainTextField input)
	{
		CompoundField<PlainTextField> output = new CompoundField<PlainTextField>(3);
		
		// TODO: Implementieren
		return output;
	}
	
	/**
	 * Decompose first name field into components.
	 * TODO: Genaue Beschreibung
	 * @param Input field.
	 * @return The decomposed field.
	 */
	public static CompoundField<PlainTextField> decomposeFirstName(PlainTextField input)
	{
		CompoundField<PlainTextField> output = new CompoundField<PlainTextField>(3);
		
		// TODO: Implementieren
		return output;
	}
	
	/**
	 * Generates phonetic code following the "Cologne phonetics" algorithm.
	 * @param input Input field.
	 * @return The transformed field.
	 */
	public static PlainTextField colognePhonetic(PlainTextField input)
	{
		String resultString = new String();
		// TODO: Implementieren
		PlainTextField output = new PlainTextField(resultString);
		return output;
	}

	/**
	 * Generates phonetic code following the "Hannover phonetics" algorithm.
	 * @param input The input field.
	 * @return The transformed field.
	 */
	public static PlainTextField hannoverPhonetic(PlainTextField input)
	{
		String resultString = new String();
		// TODO: Implementieren
		PlainTextField output = new PlainTextField(resultString);
		return output;
	}
	
}
