package de.unimainz.imbei.mzid;

import java.util.List;
import java.util.Vector;

/**
 * Klasse für zusammengesetzte Attribute, z.B. Namen aus mehreren Komponenten.
 * @author borg
 *
 * @param <T> Die Art des Felds
 */
public class CompoundField<T extends Field> extends Field<List<T>> {
	
	private int size;
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
	}
	
	@Override
	public List<T> getValue()
	{
		return this.value;
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
}
