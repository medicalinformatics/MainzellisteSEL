package de.unimainz.imbei.mzid;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.unimainz.imbei.mzid.exceptions.InternalErrorException;

/**
 * CompoundField represents a field that is composed of several subfields. For example, 
 * a name with several components can be modeled as CompoundField<PlainTextField>.
 *   
 * @author borg
 *
 * @param <T> The class of the components (a subclass of Field<?>).
 */
@Entity
public class CompoundField<T extends Field<?>> extends Field<List<T>> {
	

	@OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, 
				fetch=FetchType.EAGER, targetEntity = Field.class)
	private List<T> value;
	
	/**
	 * Get the number of components. This is the number of Fields this CompoundField can hold, some
	 * of which can be empty at a given time. To get the number of non-empty fields,
	 * call getSize() - nEmptyFields.
	 * 
	 */
	public int getSize() {
		return value.size();
	}

	/**
	 * Construct a CompoundField from a list of fields.
	 * @param value
	 */
	public CompoundField(List<T> value)
	{
		super(value);
	}

	/** Construct a CompoundField with size components.
	 * 
	 * @param size
	 */
	public CompoundField(int size)
	{		
		super(new Vector<T>(size));
		for (int i = 0; i < size; i++)
		{
			value.add(null);
		}
	}
	
	@Override
	public List<T> getValue()
	{
		return this.value;
	}

	/**
	 * Get the i-th component.
	 * @param i
	 */
	public T getValueAt(int i)
	{
		return this.value.get(i);
	}

	@Override
	public void setValue(List<T> value)	
	{
		this.value = value;
	}
	
	@Override
	public void setValue(String s) {
		try {
//			JSONObject obj = new JSONObject(s);
//			this.value = new LinkedList<T>();
//			Class parameterClass = Class.forName(obj.getString("parameterClass"));
//			// One field in obj is the class of the base fields, the other are values 
//			for (int fieldInd = 0; fieldInd < obj.length() -  1; fieldInd++) {
//				this.value.add((T) new PlainTextField((String)obj.get("field" + fieldInd)));
//			}
			JSONArray arr = new JSONArray(s);
			this.value = new LinkedList<T>();
			for (int fieldInd = 0; fieldInd < arr.length(); fieldInd++) {
				JSONObject obj = arr.getJSONObject(fieldInd);
				T thisField = (T) Class.forName(obj.getString("class")).newInstance();
				thisField.setValue(obj.getString("value"));
				this.value.add(thisField);
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error("Exception:", e);
			throw new InternalErrorException();
		}
	}
	
	/**
	 * Set the i-th component.
	 * @param i
	 * @param value
	 */
	public void setValueAt(int i, T value)
	{
		this.value.set(i,  value);	
	}
	
	/**
	 * Get the number of currently empty components. If a component is empty is
	 * determined by calling its isEmpty() method.
	 */
	public int nEmptyFields()
	{
		int result = 0;
		for (T thisField : this.value)
		{
			if (thisField.isEmpty()) result++;
		}
		return result;
	}
	
	@Override
	/**
	 * A CompoundField is empty if all of its components are empty. 
	 */
	public boolean isEmpty()
	{
		if (this.nEmptyFields() == this.getSize())
			return true;
		else
			return false;
	}
	
	@Override
	/**
	 * Creates a copy of this CompoundField. The components are copied by calling
	 * their clone() methods.
	 */
	public CompoundField<T> clone()
	{
		List<Field<?>> copies = new Vector<Field<?>>(3);
		for (T field : this.value)
		{
			copies.add(field.clone());
		}
		return new CompoundField<T>((List<T>) copies);
	}
	
	@Override
	public JSONArray getValueJSON() throws JSONException {
		JSONArray obj = new JSONArray();
		for (T field : this.value) {
			obj.put(field.toJSON());
		}
		return obj;
	}
	

	
}
