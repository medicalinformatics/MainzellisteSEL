package de.unimainz.imbei.mzid;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;

import de.unimainz.imbei.mzid.dto.PatientAdapter;
import de.unimainz.imbei.mzid.dto.PatientDto;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;

public enum Config {
	instance;
	
	enum FieldType {
		PLAINTEXT,
		PLAINTEXT_NORMALIZED,
		HASHED, // Bloomfilter without prior normalization
		HASHED_NORMALIZED; // Bloomfilter with prior normalization
	}
	
	private String configPath = "Web-Content/WEB-INF/mzid.conf";
	
	private final Map<String,FieldType> FieldTypes;
	private final Map<String, Session> sessions;
	private Properties props;
	
	Config() {
		//TODO: Das alles irgendwoher laden.
		props = new Properties();
		try {
			FileReader r = new FileReader(new File(configPath));
			props.load(r);
			r.close();
			
		} catch (IOException e)
		{
			// TODO
		}
		
	
		sessions = new HashMap<String, Session>();

		Map<String, FieldType> temp = new HashMap<String, FieldType>();
		temp.put("vorname", FieldType.PLAINTEXT);
		temp.put("nachname", FieldType.PLAINTEXT);
		temp.put("geburtsname", FieldType.PLAINTEXT);
		temp.put("geburtsdatum", FieldType.PLAINTEXT);
		FieldTypes = Collections.unmodifiableMap(temp);
	}
	
	public String getProperty(String propKey){
		return props.getProperty(propKey);
	}
	
	public Set<String> getFieldKeys(){
		return FieldTypes.keySet();
	}
	
	public FieldType getFieldType(String FieldKey){
		assert FieldTypes.keySet().contains(FieldKey);
		return FieldTypes.get(FieldKey);
	}
	
	public Session newSession(){
		String sid = UUID.randomUUID().toString();
		Session s = new Session(sid);
		synchronized (sessions) {
			sessions.put(sid, s);
		}
		return s;
	}
	
	/**
	 * Returns Session with sid (or null if unknown)
	 * Caller MUST ensure proper synchronization on the session.
	 * 
	 * @return
	 */
	public Session getSession(String sid) {
		synchronized (sessions) {
			return sessions.get(sid);
		}
	}
	
	/**
	 * Returns all known session ids.
	 * 
	 * @return
	 */
	public Set<String> getSessionIds(){
		return Collections.unmodifiableSet(new HashSet<String>(sessions.keySet()));
	}
	
	public void deleteSession(String sid){
		synchronized (sessions) {
			sessions.remove(sid);
		}
	}
}