package de.unimainz.imbei.mzid;

import javax.persistence.Entity;

@Entity
public class IntegerField extends Field<Integer> {
	
	private int value;
	
	public IntegerField(Integer value)
	{
		this.value = value;
	}
	
	public IntegerField(String value)
	{
		this.value = Integer.parseInt(value);
	}
	
	public Integer getValue()
	{
		return this.value;
	}
	
	public void setValue(Integer value)
	{
		this.value = value;
	}
	
	public IntegerField clone()
	{
		return new IntegerField(this.value);
	}

}
