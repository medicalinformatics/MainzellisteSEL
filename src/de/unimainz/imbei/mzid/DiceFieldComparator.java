package de.unimainz.imbei.mzid;

import java.util.BitSet;
import java.util.Map;

public class DiceFieldComparator extends FieldComparator {

	public DiceFieldComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}
	
	@Override
	public Object compare(Person personLeft, Person personRight)
	{
		// TODO: Typ-Check
		// TODO: Fall, dass geforderte Charakteristiken nicht vorhanden sind  
		Map<String, Characteristic<?>> cLeft = personLeft.getCharacteristics();
		Map<String, Characteristic<?>> cRight = personRight.getCharacteristics();
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

