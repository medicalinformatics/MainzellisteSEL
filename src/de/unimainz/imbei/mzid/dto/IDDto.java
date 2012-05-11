package de.unimainz.imbei.mzid.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * DTO for IDs. Can be converted from and into any type of ID.
 * 
 * @author Martin Lablans
 */
@Entity
public class IDDto {
	@Id
	@GeneratedValue
	private String idid;
	
	private String idString;
	private String type;
	
	public IDDto() {}
	
	IDDto(String idString, String type) {
		this.idString = idString;
		this.type = type;
	}

	@XmlAttribute
	public String getId() {
		return idString;
	}
	
	private void setId(String id) {
		this.idString = id;
	}
	
	public String getType() {
		return type;
	}
	
	private void setType(String type) {
		this.type = type;
	}
}
