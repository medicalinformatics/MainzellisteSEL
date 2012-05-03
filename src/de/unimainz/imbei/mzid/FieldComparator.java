package de.unimainz.imbei.mzid;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;

import sun.security.util.BitArray;

/**
 * Represents a comparison between two input fields (characteristics)
 * in a Person. Comparison methods, such as string comparison or
 * binary comparison (equal / not equal) are implemented as
 * subclasses of this class. Every concrete comparison (for example:
 * compare first names of input by JaroWinkler string metric) is
 * represented by an object of this class  
 * @author borg
 *
 */
public abstract class FieldComparator {

	protected String fieldLeft;
	protected String fieldRight;
	
	
	/** Default constructor. Usually the parametrized constructor
	 * should be used, but the default constructor makes sense
	 * for array comparisons, where the the comparison fields are
	 * changed in order to avoid the overhead of instantiating many
	 * FieldComparator objects.
	 * 
	 */
	public FieldComparator()
	{
	}
	
	/**
	 * Instantiate comparison between two
	 * specified fields. The field definitions correspond to
	 * indices in the characteristics map of the persons (objects of
	 * class Person) which are compared.
	 * 
	 * In many cases, subclasses will define constructors with
	 * additional arguments for setting comparator-specific
	 * parameters.
	 * 
	 * @param fieldLeft
	 * @param fieldRight
	 */
	public FieldComparator(String fieldLeft, String fieldRight)
	{
		this.fieldLeft = fieldLeft;
		this.fieldRight = fieldRight;
	}
	
	/**
	 * This is the workhorse of the comparator. Implementations
	 * should implement or interface their comparison logic (e.g.
	 * a string comparison algorithm) into this method. The parameters
	 * specify the Person objects which to compare.
	 * 
	 * @param personLeft 
	 * @param personRight
	 * @return An object containing the comparison result, for example
	 * Double for distance metrics or Boolean for equal / unequal comparisons.
	 */
	public abstract Object compare (Person personLeft, Person personRight);

	public String getFieldLeft() {
		return fieldLeft;
	}

	public void setFieldLeft(String fieldLeft) {
		this.fieldLeft = fieldLeft;
	}

	public String getFieldRight() {
		return fieldRight;
	}

	public void setFieldRight(String fieldRight) {
		this.fieldRight = fieldRight;
	}
}
