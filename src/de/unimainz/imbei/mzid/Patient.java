package de.unimainz.imbei.mzid;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Patient { 
	@Id
	private PID id;
	private Map<String, Field<?>> fields;
	
	public Patient() {}
	
	public Patient(PID id, Map<String, Field<?>> c) {
		this.id = id;
		this.fields = c;
	}

	public PID getId() {
		return id;
	}
	
	public void setId(PID id) {
		this.id = id;
	}
	
	public Map<String, Field<?>> getFields() {
		return fields;
	}
	
	public void setFields(Map<String, Field<?>> Fields) {
		this.fields = Fields;
	}
	
	@Override
	public String toString() {
		return id.toString() + fields.toString();
	}
}