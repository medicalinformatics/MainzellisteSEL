package de.unimainz.imbei.mzid;

/**
 * A characteristic describing a person, e.g. name, date of birth, ...
 * This abstraction allows for different matching of plaintext and hashed characteristics.
 * 
 * @author Martin Lablans
 *
 */
public abstract class Characteristic {
	public abstract String getValue();
	public abstract void setValue(String value);
	
	@Override
	public String toString() {
		return getValue();
	}
}
