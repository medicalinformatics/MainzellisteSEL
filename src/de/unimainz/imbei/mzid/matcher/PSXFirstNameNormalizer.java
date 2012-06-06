package de.unimainz.imbei.mzid.matcher;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.PlainTextField;


/**
 * Wrapper for transformations conducted by PSX:
 * <ul>
 * 	<li> Removal of leading and trailing delimiters.
 * 	<li> Conversion of umlauts.
 * 	<li> Decomposition into three components.
 * @author borg
 *
 */
public class PSXFirstNameNormalizer extends FieldTransformer<PlainTextField, CompoundField<PlainTextField>> {

	private StringNormalizer stringNormalizer = new StringNormalizer();
	private FirstNameDecomposer firstNameDecomposer = new FirstNameDecomposer();
	
	@Override
	public CompoundField<PlainTextField> transform(PlainTextField input) {
		CompoundField<PlainTextField> output = firstNameDecomposer.transform(stringNormalizer.transform(input)); 
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


}
