package de.unimainz.imbei.mzid;

public class PlainTextCharacteristic extends Characteristic {
	private String value;

	public PlainTextCharacteristic(String value) {
		this.value = value;
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
