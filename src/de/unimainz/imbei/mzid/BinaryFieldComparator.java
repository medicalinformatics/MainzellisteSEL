package de.unimainz.imbei.mzid;

import java.util.Map;


public class BinaryFieldComparator extends FieldComparator {

	public BinaryFieldComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}
	
	@Override
	public double compare(Patient patientLeft, Patient patientRight) {
		// TODO: Fall, dass geforderte Charakteristiken nicht vorhanden sind  
		Map<String, Field<?>> cLeft = patientLeft.getFields();
		Map<String, Field<?>> cRight = patientRight.getFields();
		
		if (cLeft.get(this.fieldLeft).equals(cRight.get(this.fieldLeft)))
			return 1.0;
		else
			return 0.0;		
	}

}
