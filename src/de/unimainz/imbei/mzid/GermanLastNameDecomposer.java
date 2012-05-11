package de.unimainz.imbei.mzid;

/**
 * Decomposition of last name into components (3 by default),
 * with recognition of German components such as "von", "Freiherr" etc.
 * 
 * @author borg
 *
 */
public class GermanLastNameDecomposer implements FieldTransformer<PlainTextField, CompoundField<PlainTextField>>{
	
	private int nCcomponents = 3;

	/** Delimiters to recognize when decomposing Names.
	 * 
	 */
	private static char delimiterChars[] = {' ', '.', ':', ',', ';', '-', '\''};

	
	public CompoundField<PlainTextField> transform(PlainTextField input)
	{
		// TODO möglichst unabhängig von upper / lower case machen
		return null;
		
	}
	
}
