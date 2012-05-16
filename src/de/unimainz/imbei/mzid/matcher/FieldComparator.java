package de.unimainz.imbei.mzid.matcher;

import de.unimainz.imbei.mzid.Patient;


/**
 * Represents a comparison between two input fields (Fields)
 * in a Patient. Comparison methods, such as string comparison or
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
	 * indices in the Fields map of the persons (objects of
	 * class Patient) which are compared.
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
	 * specify the Patient objects which to compare.
	 * 
	 * @param patientLeft 
	 * @param patientRight
	 * @return The comparison result as a real number in the interval [0,1],
	 * where 1 denotes equality and 0 maximal disagreement.
	 */
	public abstract double compare (Patient patientLeft, Patient patientRight);

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
