package de.unimainz.imbei.mzid.matcher;

import java.util.Vector;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.Field;

public abstract class FieldTransformer<IN extends Field<?>, OUT extends Field<?>>{

	public abstract OUT transform(IN input);	
	public abstract Class<IN> getInputClass();
	public abstract Class<OUT> getOutputClass();
	
	/** Default handling for compound fields: Element-wise transformation
	 * of the components.
	 * @param A CompoundField.
	 * @return A CompoundField where component i is the result of 
	 * this.transform(input.getValueAt(i)).
	 */
	public CompoundField<OUT> transform(CompoundField<IN> input)
	{
		Vector<OUT> outFields = new Vector<OUT>(input.getSize());
		for (IN thisField : input.getValue())
		{
			outFields.add(this.transform(thisField));
		}
		CompoundField<OUT> result = new CompoundField<OUT>(outFields);
		return result;
	}
}
