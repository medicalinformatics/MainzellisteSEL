package de.unimainz.imbei.mzid;

import java.util.Map;

public class BinaryFieldComparator extends FieldComparator {

	public BinaryFieldComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}
	
	@Override
	public Object compare(Person personLeft, Person personRight) {
		// TODO: Fall, dass geforderte Charakteristiken nicht vorhanden sind  
		Map<String, Characteristic<?>> cLeft = personLeft.getCharacteristics();
		Map<String, Characteristic<?>> cRight = personRight.getCharacteristics();
		
		return cLeft.get(this.fieldLeft).equals(cRight.get(this.fieldLeft));
		
	}

}
