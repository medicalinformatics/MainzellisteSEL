package de.unimainz.imbei.mzid;

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
	public static char delimiters[] = {' ', '.', ':', ',', ';', '-', '\''};
	
	/**
	 * Normalize a PlainTextField. Normalization includes:
	 * <ul>
	 * 	<li> removal of leading and trailing spaces,
	 * 	<li> conversion to upper case,
	 *  <li> conversion of Umlauts.
	 * <ul>
	 * @param input
	 * @return
	 */
	public static PlainTextField normalizeString(PlainTextField input)
	{
		String resultString = new String();
		// TODO: Implementieren
		PlainTextField output = new PlainTextField(resultString);
		return output;
		
	}
	
	/**
	 * Decompose surname field into components.
	 * The field is decomposed into tokens, seperated by the delimiters defined
	 * by DELIMITERS.
	 * @param input
	 * @return
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
	 * @param input
	 * @return
	 */
	public static CompoundField<PlainTextField> decomposeFirstName(PlainTextField input)
	{
		CompoundField<PlainTextField> output = new CompoundField<PlainTextField>(3);
		
		// TODO: Implementieren
		return output;
	}
	
	/**
	 * Generates phonetic code following the "Cologne phonetics" algorithm.
	 * @param input
	 * @return
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
	 * @param input
	 * @return
	 */
	public static PlainTextField hannoverPhonetic(PlainTextField input)
	{
		String resultString = new String();
		// TODO: Implementieren
		PlainTextField output = new PlainTextField(resultString);
		return output;
	}
	
}
