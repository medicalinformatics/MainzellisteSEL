package de.unimainz.imbei.mzid.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.openjpa.persistence.Persistent;

import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.ID;
import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Patient;

/**
 * DTO für Patient
 * 
 * @author Martin
 */
@Entity
public class PatientDto {
	@Id
	@GeneratedValue
	private String plid;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="plid")
	private Set<IDDto> ids;
	private Map<String, Field<?>> fields;
	
	PatientDto() {}

	public String getPlid() {
		return plid;
	}
	
	void setPlid(String plid) {
		this.plid = plid;
	}
	
	public Set<IDDto> getIds() {
		return ids;
	}
	
	void setIds(Set<IDDto> ids) {
		this.ids = ids;
	}
	
	public Map<String, Field<?>> getFields() {
		return fields;
	}
	
	void setFields(Map<String, Field<?>> fields) {
		this.fields = fields;
	}
}
