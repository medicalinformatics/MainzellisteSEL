package de.unimainz.imbei.mzid;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ElementCollection;
import javax.persistence.MapKeyClass;
import de.unimainz.imbei.mzid.dto.Persistor;

@Entity
public class IDGeneratorMemory {
	
	@Id
	@GeneratedValue
	protected int fieldJpaId;
	
	@ElementCollection(targetClass = String.class, fetch=FetchType.EAGER)
	@MapKeyClass(String.class)
	protected Map<String, String> mem = new HashMap<String, String>();
	
	protected String idString;
	
	public IDGeneratorMemory(String idString)
	{
		this.idString = idString;
	}
	synchronized void set(String key, String value){
		mem.put(key, value);
	}
	
	synchronized String get(String key){
		return mem.get(key);
	}
	
	synchronized void commit(){
		Persistor.instance.updateIDGeneratorMemory(this);
	}
}
