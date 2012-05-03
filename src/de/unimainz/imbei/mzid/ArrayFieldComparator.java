package de.unimainz.imbei.mzid;

import java.util.Hashtable;
import java.util.Map;

/**
 * Implements an array comparison between sets of fields.
 * I.e., a set of fields in the data of one patient is compared 
 * to a set of fields in another patient's data. This allows 
 * taking transposition (such as swapping of name components) into
 * account.
 * 
 * 
 * @author borg
 *
 */
public class ArrayFieldComparator {

	private String fieldListLeft[]; /** The names of fields to compare in the first patient's data */
	private String fieldListRight[]; /** The names of fields to compare in the second patient's data */
	private FieldComparator comparator;
	
	/**
	 * Instantiates an ArrayFieldComparator. 
	 * @param fieldListLeft The names of fields to compare in the first patient's data. 
	 * @param fieldListRight The names of fields to compare in the second patient's data.
	 * @param comparator The comparator to use.
	 */
	public ArrayFieldComparator(String fieldListLeft[], String fieldListRight[],
			FieldComparator comparator)
	{
		super();
		this.fieldListLeft = fieldListLeft;
		this.fieldListRight = fieldListRight;
		this.comparator = comparator;
	}
	
	
	public Map<StringPair, Object> compare(Person personLeft, Person personRight) 
	{
		Hashtable<StringPair, Object> result = new Hashtable<StringPair, Object>();
		for (String fieldLeft : fieldListLeft)
		{
			comparator.setFieldLeft(fieldLeft);
			for (String fieldRight : fieldListRight)
			{	
				comparator.setFieldRight(fieldRight);
				Object value = comparator.compare(personLeft, personRight);
				result.put(new StringPair(fieldLeft, fieldRight), value);
			}
		}
		
		return result;
	}

}
