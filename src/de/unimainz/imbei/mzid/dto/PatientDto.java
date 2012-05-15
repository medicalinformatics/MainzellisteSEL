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
//@Entity
public class PatientDto {
//	@Id
//	@GeneratedValue
	private String intPatId;

//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
//	@JoinColumn(name="intPatId")
	private Set<IDDto> ids;
	
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
//	@JoinColumn(name="intPatId") //TODO: Wieso läuft das nicht?
	private Map<String, Field<?>> fields;

	public String getIntPatId() {
		return intPatId;
	}
	
	void setIntPatId(String intPatId) {
		this.intPatId = intPatId;
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
