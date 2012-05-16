package de.unimainz.imbei.mzid;


/**
 * Generates phonetic code following the "Cologne phonetics" algorithm.
 */
public class ColognePhoneticTransformer implements FieldTransformer<PlainTextField, PlainTextField>{

	// TODO
	// replacement rules, patterns encoded as regular expressions
	private static String rules[][] = {
			{ "[AEIJOUY]", "0"},
			{ "H", ""},
			{ "B", "1"},
			{ "P[^H]", "1"},
			{ "[DT][^CSZ]", "2"},
			{ "FVW", "3"},
			{ "PH", "3"},
	}; 
			
			
	
	public PlainTextField transform(PlainTextField input)
	{
		return null;
	}

}
