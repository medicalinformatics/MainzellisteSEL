package de.unimainz.imbei.mzid.matcher;

import java.util.BitSet;

import de.unimainz.imbei.mzid.HashedField;

/**
 * Compares two fields that were encrypted as bloom filters.
 * See {@link BloomFilterTransformer} for details. 
 * @author borg
 *
 */
public class DiceFieldComparator extends FieldComparator<HashedField> {

	public DiceFieldComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}
	
	@Override
	public double compareBackend(HashedField fieldLeft, HashedField fieldRight)
	{

		assert (fieldLeft instanceof HashedField);
		assert (fieldRight instanceof HashedField);
		
		HashedField hLeft = fieldLeft;
		HashedField hRight = fieldRight;
		BitSet bLeft = hLeft.getValue();
		BitSet bRight = hRight.getValue();
		
		int nLeft = bLeft.cardinality();
		int nRight = bRight.cardinality();
		bLeft.and(bRight);
		return (2.0 * bLeft.cardinality() / (nLeft + nRight));
	}
}

