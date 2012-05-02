package de.unimainz.imbei.mzid;

public class PlainTextCharacteristic extends Characteristic<String> {
	private String value;

	public PlainTextCharacteristic(String value) {
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
