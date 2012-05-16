package de.unimainz.imbei.mzid;

import java.util.BitSet;

public class DiceFieldComparator extends FieldComparator {

	public DiceFieldComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}
	
	@Override
	public double compare(Patient patientLeft, Patient patientRight)
	{

		Field<?> cLeft = patientLeft.getFields().get(fieldLeft);
		Field<?> cRight = patientRight.getFields().get(fieldRight);
		assert (cLeft instanceof HashedField);
		assert (cRight instanceof HashedField);
		
		HashedField hLeft = (HashedField) cLeft;
		HashedField hRight = (HashedField) cRight;
		BitSet bLeft = hLeft.getValue();
		BitSet bRight = hRight.getValue();
		
		int nLeft = bLeft.cardinality();
		int nRight = bRight.cardinality();
		bLeft.and(bRight);
		return (2 * bLeft.cardinality() / (nLeft + nRight));
	}
}

