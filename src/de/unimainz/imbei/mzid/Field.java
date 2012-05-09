package de.unimainz.imbei.mzid;

import java.util.BitSet;

import de.unimainz.imbei.mzid.Config.FieldType;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;

/**
 * A Field describing a person, e.g. name, date of birth, ...
 * This abstraction allows for different matching of plaintext and hashed Fields.
 * 
 * @author Martin Lablans
 *
 */
public abstract class Field<T> {
	public abstract T getValue();
	public abstract void setValue(T value);
	
	public Field(T s) {
		setValue(s);
	}
	
	@Override
	public String toString() {
		return getValue().toString();
	}
	
	public static Field<?> build(String charKey, Object o){
		return build(Config.instance.getFieldType(charKey), o);
	}
	
	public static Field<?> build(FieldType t, Object o){
		switch(t){
			case PLAINTEXT:
			case PLAINTEXT_NORMALIZED:
				assert o instanceof String;
				return new PlainTextField((String)o);
			case HASHED:
			case HASHED_NORMALIZED:
				assert o instanceof BitSet;
				return new HashedField((BitSet)o);
			default:
				throw new NotImplementedException();
		}
	}
}
