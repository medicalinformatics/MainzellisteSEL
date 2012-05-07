package de.unimainz.imbei.mzid;

import java.util.Map;

public class BinaryFieldComparator extends FieldComparator {

	public BinaryFieldComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}
	
	@Override
	public Object compare(Patient patientLeft, Patient patientRight) {
		// TODO: Fall, dass geforderte Charakteristiken nicht vorhanden sind  
		Map<String, Characteristic<?>> cLeft = patientLeft.getCharacteristics();
		Map<String, Characteristic<?>> cRight = patientRight.getCharacteristics();
		
		return cLeft.get(this.fieldLeft).equals(cRight.get(this.fieldLeft));
		
	}

}
