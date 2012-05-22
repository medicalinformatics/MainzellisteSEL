package de.unimainz.imbei.mzid;

import java.util.BitSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.unimainz.imbei.mzid.Config.FieldType;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;

/**
 * A Field describing a person, e.g. name, date of birth, ...
 * This abstraction allows for different matching of plaintext and hashed Fields.
 * 
 * @author Martin Lablans
 *
 */
@Entity
public abstract class Field<T> {
	@Id
	@GeneratedValue
	@JsonIgnore
	protected int jpaId;
	
	public abstract T getValue();
	public abstract void setValue(T value);
	
	/** Empty constructor. Used by subclasses. */ 
	protected Field()
	{		
	}
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Field<?>)
				return this.getValue().equals(((Field<?>) obj).getValue());
		else
			return false;
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
