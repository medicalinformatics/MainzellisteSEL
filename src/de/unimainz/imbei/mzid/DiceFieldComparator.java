package de.unimainz.imbei.mzid;

import java.util.BitSet;
import java.util.Map;

public class DiceFieldComparator extends FieldComparator {

	public DiceFieldComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}
	
	@Override
	public Object compare(Patient patientLeft, Patient patientRight)
	{

		Characteristic<?> cLeft = patientLeft.getCharacteristics().get(fieldLeft);
		Characteristic<?> cRight = patientRight.getCharacteristics().get(fieldRight);
		assert (cLeft instanceof HashedCharacteristic);
		assert (cRight instanceof HashedCharacteristic);
		
		HashedCharacteristic hLeft = (HashedCharacteristic) cLeft;
		HashedCharacteristic hRight = (HashedCharacteristic) cRight;
		BitSet bLeft = hLeft.getValue();
		BitSet bRight = hRight.getValue();
		
		int nLeft = bLeft.cardinality();
		int nRight = bRight.cardinality();
		bLeft.and(bRight);
		return (2 * bLeft.cardinality() / (nLeft + nRight));
	}
}

