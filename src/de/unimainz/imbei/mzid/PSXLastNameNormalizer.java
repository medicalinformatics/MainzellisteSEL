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
public class PSXLastNameNormalizer implements FieldTransformer<PlainTextField, CompoundField<PlainTextField>> {

	private StringNormalizer stringNormalizer = new StringNormalizer();
	private GermanLastNameDecomposer lastNameDecomposer = new GermanLastNameDecomposer();
	
	@Override
	public CompoundField<PlainTextField> transform(PlainTextField input) {
		return lastNameDecomposer.transform(stringNormalizer.transform(input)); 
	}
	

}
