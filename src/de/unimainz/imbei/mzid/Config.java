package de.unimainz.imbei.mzid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum Config {
	instance;
	
	enum CharacteristicType {
		PLAINTEXT,
		PLAINTEXT_NORMALIZED,
		HASHED, // Bloomfilter without prior normalization
		HASHED_NORMALIZED; // Bloomfilter with prior normalization
	}
	
	private final Map<String,CharacteristicType> characteristicTypes;
	private final PIDGenerator pidgen;
	private final Map<String, Session> sessions;
	
	Config() {
		//TODO: Das alles irgendwoher laden.
		Map<String, CharacteristicType> temp = new HashMap<String, CharacteristicType>();
		temp.put("vorname", CharacteristicType.PLAINTEXT);
		temp.put("nachname", CharacteristicType.PLAINTEXT);
		temp.put("geburtsname", CharacteristicType.PLAINTEXT);
		temp.put("geburtsdatum", CharacteristicType.PLAINTEXT);
		characteristicTypes = Collections.unmodifiableMap(temp);
		pidgen = PIDGenerator.init(1, 2, 3, 0);
		sessions = new HashMap<String, Session>();
	}
	
	public Set<String> getCharacteristicKeys(){
		return characteristicTypes.keySet();
	}
	
	public CharacteristicType getCharacteristicType(String characteristicKey){
		assert characteristicTypes.keySet().contains(characteristicKey);
		return characteristicTypes.get(characteristicKey);
	}
	
	public PIDGenerator getPidgen() {
		return pidgen;
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