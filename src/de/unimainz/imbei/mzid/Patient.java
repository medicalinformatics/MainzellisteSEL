package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
	private String intPatId;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="intPatId")
	private Set<ID> ids;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
//	@JoinColumn(name="intPatId")
	private Map<String, Field<?>> fields;
	
	public Patient() {}
	
	public Patient(String plid, Set<ID> ids, Map<String, Field<?>> c) {
		this.intPatId = plid;
		this.ids = ids;
		this.fields = c;
	}
	
	public String getIntPatId() {
		return intPatId;
	}
	
	protected void setIntPatId(String intPatId) {
		this.intPatId = intPatId;
	}
	
	public Set<ID> getIds(){
		return Collections.unmodifiableSet(ids);
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
		return intPatId.toString() + fields.toString();
	}
}