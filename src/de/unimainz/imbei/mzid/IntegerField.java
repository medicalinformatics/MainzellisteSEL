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
	
	@Override
	public Integer getValue()
	{
		return this.value;
	}
	
	@Override
	public void setValue(Integer value)
	{
		this.value = value;
	}
	
	@Override
	public void setValue(String s) {
		this.value = Integer.parseInt(s);
	}
	
	@Override
	public Integer getValueJSON() {
		return this.value;
	}
	
	@Override
	public IntegerField clone()
	{
		return new IntegerField(this.value);
	}	
}
