package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

import de.unimainz.imbei.mzid.Session;

/**
 * TODO: Serialisierer für Session schreiben
 * @author Martin
 *
 */
public class SessionDto {
	private String sessionId;
	private HashMap<String, String> data = new HashMap<String, String>();
	
	public SessionDto() {}
	
	public SessionDto(Session arg0) {
		sessionId = arg0.getId();
		data = new HashMap<String, String>(arg0);
	}

	@XmlAttribute
	public String getId(){
		return sessionId;
	}
	
	public void setId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@XmlAttribute
	public Map<String, String> getData() {
		return data;
	}
	
	public void setData(Map<String, String> map) {
		data = new HashMap<String, String>(map);
	}
}
