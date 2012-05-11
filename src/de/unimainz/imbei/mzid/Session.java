package de.unimainz.imbei.mzid;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unimainz.imbei.mzid.dto.SessionAdapter;

@XmlJavaTypeAdapter(SessionAdapter.class)
public class Session extends ConcurrentHashMap<String, String>{
	private String sessionId;
	
	public Session(String s) {
		sessionId = s;
	}
	
	public String getId(){
		return sessionId;
	}
}
