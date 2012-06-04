package de.unimainz.imbei.mzid.matcher;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.PlainTextField;


/**
 * Decomposition of first name into components (3 by default)
 * @author borg
 *
 */
public class FirstNameDecomposer implements FieldTransformer<PlainTextField, CompoundField<PlainTextField>> {

	/** Delimiters to recognize when decomposing Names as regular expression.
	 * 
	 */
	private String delimiters = "[ \\.:,;\\-']+";

	private int nComponents = 3;
	
	public CompoundField<PlainTextField> transform(PlainTextField input)
	{
		CompoundField<PlainTextField> output = new CompoundField<PlainTextField>(nComponents);
		String substrings[] = input.getValue().split(delimiters, nComponents);
		int i;
		for (i = 0; i < substrings.length; i++)
			output.setValueAt(i, new PlainTextField(substrings[i]));
		// fill remaining fields with empty Strings
		for (;i < nComponents; i++)
			output.setValueAt(i, new PlainTextField(""));
		return output;
	}
	
	public Class<PlainTextField> getInputClass()
	{
		return PlainTextField.class;
	}
	
	public Class<CompoundField<PlainTextField>> getOutputClass()
	{
		return (Class<CompoundField<PlainTextField>>) new CompoundField<PlainTextField>(3).getClass();
	}
	
	public static void main(String args[])
	{
		FirstNameDecomposer dec = new FirstNameDecomposer();
		System.out.println(dec.getOutputClass());
		
		BloomFilterTransformer bf = new BloomFilterTransformer();
		System.out.println(bf.getOutputClass());
		
	}
}
