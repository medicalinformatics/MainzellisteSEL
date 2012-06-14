package de.unimainz.imbei.mzid;

import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

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
	 * @return
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
	 * @return
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
	 * @return
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
}
