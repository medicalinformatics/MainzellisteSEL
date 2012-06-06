package de.unimainz.imbei.mzid;

import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * Klasse für zusammengesetzte Attribute, z.B. Namen aus mehreren Komponenten.
 * @author borg
 *
 * @param <T> Die Art des Felds
 */
@Entity
public class CompoundField<T extends Field<?>> extends Field<List<T>> {
	
	private int size;
	
	@OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, 
				fetch=FetchType.EAGER, targetEntity = Field.class)
	private List<T> value;
	
	public int getSize() {
		return size;
	}

	public CompoundField(List<T> value)
	{
		super(value);
	}

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

	public T getValueAt(int i)
	{
		return this.value.get(i);
	}

	@Override
	public void setValue(List<T> value)	
	{
		this.value = value;
		this.size = value.size();
	}
	
	public void setValueAt(int i, T value)
	{
		this.value.set(i,  value);	
	}
	
	@Override
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
