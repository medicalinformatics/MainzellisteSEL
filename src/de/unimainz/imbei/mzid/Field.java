package de.unimainz.imbei.mzid;

import java.lang.reflect.Constructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;

/**
 * A Field describing a person, e.g. name, date of birth, ...
 * This abstraction allows for different matching of plaintext and hashed Fields.
 * 
 * @author Martin Lablans
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
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
	public Field()
	{		
	}
	public Field(T s) {
		setValue(s);
	}
	
	
	public abstract void setValue(String s);
	
	public JSONObject toJSON() throws JSONException {
		JSONObject ret = new JSONObject();
		ret.put("class", this.getClass().getName());
		ret.put("value", this.getValueJSON());
		return ret;
	}
	
	/** Must return one of the types recognized by JSONObject.put:
	 * Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONObject.NULL object. 
	 * 
	 * Does not return a JSON-String! Call toJSON to get a JSON representation of a field.
	 * @return
	 */
	public abstract Object getValueJSON() throws JSONException;

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

	@Override
	public String toString() {
		return this.getValue().toString();
	}
	
	public boolean isEmpty()
	{
		return this.getValue() == null;
	}
	
	public static Field<?> build(Class<? extends Field<?>> t, Object o){
		try {
			Constructor<? extends Field<?>> c = t.getConstructor(o.getClass());
			return c.newInstance(o);
		} catch (Exception e) {
				e.printStackTrace();
				throw new NotImplementedException();
		}
	}
}
