package de.unimainz.imbei.mzid;

import javax.persistence.Entity;

import org.codehaus.jettison.json.JSONObject;

@Entity
public class PlainTextField extends Field<String> {
	private String value;

	public PlainTextField(String value) {
		super(value);
	}
	
	public String getValue() {
		return this.value;
	}
	
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
