package de.unimainz.imbei.mzid;

public class HashedCharacteristic extends Characteristic{
	private String value;
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public void setValue(String hash) {
		this.value = hash;
	}
}
