package de.unimainz.imbei.mzid;

public class PlainTextField extends Field<String> {
	private String value;

	public PlainTextField(String value) {
		super(value);
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

}
