package de.unimainz.imbei.mzid;

/**
 * Decomposition of first name into components (3 by default)
 * @author borg
 *
 */
public class FirstNameDecomposer implements FieldTransformer<PlainTextField, CompoundField<PlainTextField>> {

	/** Delimiters to recognize when decomposing Names.
	 * 
	 */
	private static char delimiterChars[] = {' ', '.', ':', ',', ';', '-', '\''};

	private int nComponents = 3;
	
	public CompoundField<PlainTextField> transform(PlainTextField input)
	{
		// TODO
		return null;
		
	}
}
