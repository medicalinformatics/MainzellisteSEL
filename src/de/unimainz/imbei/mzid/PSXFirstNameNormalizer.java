package de.unimainz.imbei.mzid;


/**
 * Wrapper for transformations conducted by PSX:
 * <ul>
 * 	<li> Removal of leading and trailing delimiters.
 * 	<li> Conversion of umlauts.
 * 	<li> Decomposition into three components.
 * @author borg
 *
 */
public class PSXFirstNameNormalizer implements FieldTransformer<PlainTextField, CompoundField<PlainTextField>> {

	private StringNormalizer stringNormalizer = new StringNormalizer();
	private FirstNameDecomposer firstNameDecomposer = new FirstNameDecomposer();
	
	@Override
	public CompoundField<PlainTextField> transform(PlainTextField input) {
		CompoundField<PlainTextField> output = firstNameDecomposer.transform(stringNormalizer.transform(input)); 
		return output;
	}
	

}
