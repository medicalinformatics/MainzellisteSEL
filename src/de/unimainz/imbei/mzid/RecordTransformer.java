package de.unimainz.imbei.mzid;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import de.unimainz.imbei.mzid.exceptions.InternalErrorException;
import de.unimainz.imbei.mzid.matcher.*;

import de.unimainz.imbei.mzid.matcher.FieldComparator;

/**
 * Stores the field transformers for a set of fields, implemented as a HashMap.
 * Keys are the field names, values the corresponding FieldTransformer objects.
 * @author borg
 *
 */
public class RecordTransformer {
	
	private Map<String, FieldTransformerChain> fieldTransformers;
	
	public RecordTransformer(Properties props) throws InternalErrorException
	{
		fieldTransformers = new HashMap<String, FieldTransformerChain>();

		// Get names of fields from config vars.*
		Pattern p = Pattern.compile("^field\\.(\\w+)\\.type");
		java.util.regex.Matcher m;

		// Build map of comparators and map of frequencies from Properties
		for (Object key : props.keySet())
		{
			m = p.matcher((String) key);
			if (m.find()){
				String fieldName = m.group(1);
				String transformerProp = props.getProperty("field." + fieldName + ".transformers");
				if (transformerProp != null)
				{
					String transformers[] = transformerProp.split(",");
					FieldTransformerChain thisChain = new FieldTransformerChain();
					for (String thisTrans : transformers)
					{
						thisTrans = thisTrans.trim();
						try{
							FieldTransformer<Field<?>, Field<?>> tr = (FieldTransformer<Field<?>, Field<?>>) Class.forName("de.unimainz.imbei.mzid.matcher." + thisTrans).newInstance();
							thisChain.add(tr);						
						} catch (Exception e)
						{
							System.err.println(e.getMessage());
							throw new InternalErrorException();
						}
					}
					this.fieldTransformers.put(fieldName, thisChain);
				}
			}
		}			
	}
	
	
	/** Transforms a patient by transforming all of its fields. Fields
	 * for which no transformer is found (i.e. the field name is not in
	 * .keySet()) are passed unchanged. */
	public Patient transform(Patient input)
	{
		Map<String, Field<?>> inFields = input.getFields();
		Patient output = new Patient();
		HashMap<String, Field<?>> outFields = new HashMap<String, Field<?>>();
		/* iterate over input fields and transform each */
		for (String fieldName : inFields.keySet())
		{
			if (this.fieldTransformers.containsKey(fieldName))
				outFields.put(fieldName, this.fieldTransformers.get(fieldName).transform(inFields.get(fieldName)));
			else
				outFields.put(fieldName, inFields.get(fieldName).clone());
		}
		output.setFields(outFields);
		return output;
	}
	
}
