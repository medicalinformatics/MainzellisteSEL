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
		Map<String, Field<?>> cLeft = patientLeft.getFields();
		Map<String, Field<?>> cRight = patientRight.getFields();
		
		return cLeft.get(this.fieldLeft).equals(cRight.get(this.fieldLeft));
		
	}

}
