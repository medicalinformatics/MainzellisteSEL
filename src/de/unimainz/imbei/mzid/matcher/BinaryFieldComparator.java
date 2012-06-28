package de.unimainz.imbei.mzid.matcher;

import java.util.Map;

import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.Patient;


public class BinaryFieldComparator extends FieldComparator<Field<?>> {

	public BinaryFieldComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}
	
	@Override
	public double compare(Patient patientLeft, Patient patientRight) {
		// TODO: Fall, dass geforderte Charakteristiken nicht vorhanden sind  
		Map<String, Field<?>> cLeft = patientLeft.getFields();
		Map<String, Field<?>> cRight = patientRight.getFields();
		return compare(cLeft.get(this.fieldLeft), cRight.get(this.fieldRight));
	}

	public double compareBackend(Field<?> fieldLeft, Field<?> fieldRight)
	{
		if (fieldLeft.equals(fieldRight))
			return 1.0;
		else
			return 0.0;		
		
	}
}
