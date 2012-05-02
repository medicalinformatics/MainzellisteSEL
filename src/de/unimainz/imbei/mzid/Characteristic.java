package de.unimainz.imbei.mzid;

import java.util.BitSet;

import de.unimainz.imbei.mzid.Config.CharacteristicType;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;

/**
 * A characteristic describing a person, e.g. name, date of birth, ...
 * This abstraction allows for different matching of plaintext and hashed characteristics.
 * 
 * @author Martin Lablans
 *
 */
public abstract class Characteristic<T> {
	public abstract T getValue();
	public abstract void setValue(T value);
	
	public Characteristic(T s) {
		setValue(s);
	}
	
	@Override
	public String toString() {
		return getValue().toString();
	}
	
	public static Characteristic<?> build(String charKey, Object o){
		return build(Config.instance.getCharacteristicType(charKey), o);
	}
	
	public static Characteristic<?> build(CharacteristicType t, Object o){
		switch(t){
			case PLAINTEXT:
			case PLAINTEXT_NORMALIZED:
				assert o instanceof String;
				return new PlainTextCharacteristic((String)o);
			case HASHED:
			case HASHED_NORMALIZED:
				assert o instanceof BitSet;
				return new HashedCharacteristic((BitSet)o);
			default:
				throw new NotImplementedException();
		}
	}
}
