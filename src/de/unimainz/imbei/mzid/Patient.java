package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

@XmlRootElement
@Entity
public class Patient {
	/**
	 * Internal ID. Set by JPA when on first persistance.
	 */
	@Id
	@GeneratedValue
	@JsonIgnore
	private int patientJpaId; // JPA
	
	@OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	private Set<ID> ids;
	
	@OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	private Map<String, Field<?>> fields;
	
	public Patient() {}
	
	public Patient(Set<ID> ids, Map<String, Field<?>> c) {
		this.ids = ids;
		this.fields = c;
	}
	
	public Set<ID> getIds(){
		return Collections.unmodifiableSet(ids);
	}
	
	public ID getId(String type)
	{
		for (ID thisId : ids)
		{
			if (thisId.getType().equals(type))
				return thisId;
		}
		return null;
	}
	
	public void setIds(Set<ID> ids) {
		this.ids = ids;
	}
	
	public Map<String, Field<?>> getFields() {
		return fields;
	}
	
	public void setFields(Map<String, Field<?>> Fields) {
		this.fields = Fields;
	}
	
	@Override
	public String toString() {
		return fields.toString();
	}
}