package de.unimainz.imbei.mzid.matcher;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.Field;
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
public abstract class FieldComparator<F extends Field<?>> {

	protected String fieldLeft;
	protected String fieldRight;
	protected double missingWeight = 0.0;
	
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
	public double compare (Patient patientLeft, Patient patientRight)
	{
		Field<?> cLeft = patientLeft.getFields().get(this.fieldLeft);
		Field<?> cRight = patientRight.getFields().get(this.fieldRight);
		if(cLeft instanceof CompoundField && cRight instanceof CompoundField)
			return compare((CompoundField<F>) cLeft, (CompoundField<F>) cRight);
		else
			return compare((F) cLeft, (F) cRight);
	}

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
	
	public abstract double compare(F fieldLeft, F fieldRight);

	/**
	 * Default method for comparison of CompoundField. An implementatino of the 
	 * algorithm for array comparisons used by Automatch and its successor
	 * QualityStage. See: Ascential QualityStage. Mathing Concepts and Reference Guide.
	 * Version 7.5, 5/19-5/20.
 	 *
	 * @param fieldLeft
	 * @param fieldRight
	 * @return
	 */
	public double compare(CompoundField<F> fieldLeft, CompoundField<F> fieldRight)
	{
		
		int nLeft = fieldLeft.getSize();
		int nRight = fieldRight.getSize();
		int nNonEmptyLeft = fieldLeft.getSize() - fieldLeft.nEmptyFields();
		int nNonEmptyRight = fieldLeft.getSize() - fieldRight.nEmptyFields();

		// let fieldsA be the array with less non-missing fields
		List<F> fieldsA;
		List<F> fieldsB;
		
		if (nNonEmptyLeft <= nNonEmptyRight)
		{
			fieldsA = fieldLeft.clone().getValue();
			fieldsB = fieldRight.clone().getValue();
		} else {
			fieldsB = fieldLeft.clone().getValue();
			fieldsA = fieldRight.clone().getValue();
			
		}
		double highestWeight;
		double numerator = 0.0;
		double denominator = Math.min(nNonEmptyLeft, nNonEmptyRight);
		F fieldWithMaxWeight = null;
		for (F oneFieldA : fieldsA)
		{
			if (oneFieldA.isEmpty()) continue;
			highestWeight = this.missingWeight;
			Iterator<F> fieldBIt = fieldsB.iterator();
			while (fieldBIt.hasNext())
			{
				F oneFieldB = fieldBIt.next();
				// do not consider empty fields
				if (oneFieldB.isEmpty()) 
				{
					fieldBIt.remove();
					continue;
				}
				double thisWeight = this.compare(oneFieldA, oneFieldB);
				if (thisWeight > highestWeight)
				{
					highestWeight = thisWeight;
					fieldWithMaxWeight = oneFieldB;					
				}
			}
			if (highestWeight > this.missingWeight)
			{
				numerator += highestWeight;
				fieldsB.remove(fieldWithMaxWeight);
			}
		}		
		return numerator / denominator;
	}
}
