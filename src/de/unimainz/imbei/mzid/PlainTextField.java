package de.unimainz.imbei.mzid;

import javax.persistence.Entity;

@Entity
public class PlainTextField extends Field<String> {
	private String value;

	public PlainTextField(String value) {
		super(value);
	}
	
	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String getValueJSON() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean isEmpty()
	{
		return (this.value == null || this.value.length() == 0);
	}
	
	@Override
	public PlainTextField clone()
	{
		return new PlainTextField(new String(this.value));
	}

}
