package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

import de.unimainz.imbei.mzid.Characteristic;
import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Person;

/**
 * TODO: Serialisierer für Session schreiben
 * @author Martin
 *
 */
public class PatientDto {
	private PID id;
	private Map<String, Characteristic<?>> characteristics;
	
	public PatientDto() {}
	
	public PatientDto(Person arg0) {
		id = arg0.getId();
		characteristics = new HashMap<String, Characteristic<?>>(arg0.getCharacteristics());
	}

	@XmlAttribute
	public PID getId() {
		return id;
	}
	
	public void setId(PID id) {
		this.id = id;
	}
	
	public Map<String, Characteristic<?>> getCharacteristics() {
		return characteristics;
	}
	
	public void setCharacteristics(Map<String, Characteristic<?>> characteristics) {
		this.characteristics = characteristics;
	}
}
