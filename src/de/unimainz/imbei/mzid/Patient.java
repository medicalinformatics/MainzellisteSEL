package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Patient { 
	/**
	 * Internal ID. Set by JPA when on first persistance.
	 */
	private String plid;
	
	private Set<ID> ids;
	private Map<String, Field<?>> fields;
	
	public Patient() {}
	
	public Patient(String plid, Set<ID> ids, Map<String, Field<?>> c) {
		this.plid = plid;
		this.ids = ids;
		this.fields = c;
	}
	
	public String getPlid() {
		return plid;
	}
	
	private void setPlid(String plid) {
		this.plid = plid;
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
		return plid.toString() + fields.toString();
	}
}