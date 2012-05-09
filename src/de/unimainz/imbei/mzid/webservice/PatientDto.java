package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Patient;

/**
 * TODO: Serialisierer für Session schreiben
 * @author Martin
 *
 */
public class PatientDto {
	private PID id;
	private Map<String, Field<?>> fields;
	
	public PatientDto() {}
	
	public PatientDto(Patient arg0) {
		id = arg0.getId();
		fields = new HashMap<String, Field<?>>(arg0.getFields());
	}

	@XmlAttribute
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
}
