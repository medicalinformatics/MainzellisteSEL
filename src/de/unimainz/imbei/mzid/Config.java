package de.unimainz.imbei.mzid;

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

public enum Config {
	instance;
	
	enum FieldType {
		PLAINTEXT,
		PLAINTEXT_NORMALIZED,
		HASHED, // Bloomfilter without prior normalization
		HASHED_NORMALIZED; // Bloomfilter with prior normalization
	}
	
	private EntityManagerFactory emf;
	private final Map<String,FieldType> FieldTypes;
	private final PIDGenerator pidgen;
	private final Map<String, Session> sessions;
	private Properties props;
	
	Config() {
		//TODO: Das alles irgendwoher laden.
		emf = Persistence.createEntityManagerFactory("mzid");
		Map<String, FieldType> temp = new HashMap<String, FieldType>();
		temp.put("vorname", FieldType.PLAINTEXT);
		temp.put("nachname", FieldType.PLAINTEXT);
		temp.put("geburtsname", FieldType.PLAINTEXT);
		temp.put("geburtsdatum", FieldType.PLAINTEXT);
		FieldTypes = Collections.unmodifiableMap(temp);
		pidgen = PIDGenerator.init(1, 2, 3, 0);
		sessions = new HashMap<String, Session>();
		props = new Properties();
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
	
	public Patient getPatient(PID pid){
		EntityManager em = emf.createEntityManager();
		Patient p = em.find(Patient.class, pid);
		em.close();
		return p;
	}
	
	public List<Patient> getPatients(){ //TODO: Filtern
		EntityManager em = emf.createEntityManager();
		List<Patient> pl = em.createQuery("select p from Patient p", Patient.class).getResultList();
		em.close(); // causes all entities to be detached
		return pl;
	}

	public void addPatient(Patient p){
		EntityManager em = emf.createEntityManager();
		em.persist(p); //TODO: Fehlerbehandlung, falls PID schon existiert.
		em.close();
	}
	
	public void updatePatient(Patient p){
		EntityManager em = emf.createEntityManager();
		
		//1. fetch existing patient -- avoid reuse of existing functions to retain persistence context
		Patient exPat = em.find(Patient.class, p.getId());
		
		//2. update persisted instance
		exPat.setFields(p.getFields());
		
		//3. close //TODO: Commit needed?
		em.close();
	}
}