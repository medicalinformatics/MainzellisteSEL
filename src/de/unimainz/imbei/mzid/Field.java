package de.unimainz.imbei.mzid;

import java.lang.reflect.Constructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.codehaus.jackson.annotate.JsonIgnore;

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
	protected int fieldJpaId;
	 
	public abstract T getValue();
	public abstract void setValue(T value);
	@Override
	public abstract Field<T> clone();
	
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

	public static Field<?> build(Class<? extends Field<?>> t, Object o){
		try {
			Constructor<? extends Field<?>> c = t.getConstructor(o.getClass());
			return c.newInstance(o);
		} catch (Exception e) {
				throw new NotImplementedException();
		}
	}
}
