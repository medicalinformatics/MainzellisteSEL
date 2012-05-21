package de.unimainz.imbei.mzid;

import java.util.HashMap;

import de.unimainz.imbei.mzid.matcher.FieldComparator;

/**
 * Stores the field transformers for a set of fields, implemented as a HashMap.
 * Keys are the field names, values the corresponding FieldTransformer objects.
 * @author borg
 *
 */
public class RecordTransformer extends HashMap<String, FieldComparator> {

	/** Transforms a patient by transforming all of its fields. Fields
	 * for which no transformer is found (i.e. the field name is not in
	 * .keySet()) are passed unchanged. */
	public Patient transform(Patient input)
	{
		Patient output = new Patient();
		return output;
	}
	
}
