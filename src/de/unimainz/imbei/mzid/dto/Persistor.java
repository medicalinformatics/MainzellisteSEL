package de.unimainz.imbei.mzid.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
//import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.ID;
import de.unimainz.imbei.mzid.IDGeneratorMemory;
import de.unimainz.imbei.mzid.IDRequest;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.exceptions.InternalErrorException;

/**
 * Handles reading and writing from and to the database.
 * 
 * @author Martin Lablans
 */
public enum Persistor {
	instance;
	
//	private List<Patient> cache = null;
	
	private EntityManagerFactory emf;
	
	private EntityManager em;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private Persistor() {
		
	
		HashMap<String, String> persistenceOptions = new HashMap<String, String>();
		
		// Settings from mzid config
		persistenceOptions.put("javax.persistence.jdbc.driver", Config.instance.getProperty("db.driver"));
		persistenceOptions.put("javax.persistence.jdbc.url", Config.instance.getProperty("db.url"));
		persistenceOptions.put("javax.persistence.jdbc.user", Config.instance.getProperty("db.username"));
		persistenceOptions.put("javax.persistence.jdbc.password", Config.instance.getProperty("db.password"));
		
		// Other settings
		persistenceOptions.put("openjpa.jdbc.SynchronizeMappings", "buildSchema");
		persistenceOptions.put("openjpa.jdbc.DriverDataSource", "dbcp");
		persistenceOptions.put("openjpa.ConnectionProperties", "testOnBorrow=true, validationQuery=SELECT 1");
		
		emf = Persistence.createEntityManagerFactory("mzid", persistenceOptions);
		em = emf.createEntityManager();
		
		new org.apache.openjpa.jdbc.schema.DBCPDriverDataSource();
		// Check database connection
		getPatients();
		
		Logger.getLogger(Persistor.class).info("Persistence has initialized successfully.");
	}
	
	/**
	 * Get a patient by id.
	 * @param pid
	 * @return
	 */
	public Patient getPatient(ID pid){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Patient> q = em.createQuery("SELECT p FROM Patient p JOIN p.ids id WHERE id.idString = :idString AND id.type = :idType", Patient.class);
		q.setParameter("idString", pid.getIdString());
		q.setParameter("idType", pid.getType());
		List<Patient> result = q.getResultList();
		if (result.size() > 1) {
			logger.fatal("Found more than one patient with ID: " + pid.toString());
			throw new InternalErrorException("Found more than one patient with ID: " + pid.toString());
		}
		Patient p = result.get(0);
		// Fetch lazy loaded IDs
		p.getIds();
		em.close();
		if (result.size() == 0)
			return null;
		else
			return result.get(0);
	}
	
	/**
	 * Returns all patients currently persisted in the patient list. This is not a copy!
	 * Caller MUST NOT perform write operations on the return value or its linked objects.
	 * 
	 * @return All persisted patients.
	 */
	public synchronized List<Patient> getPatients() { //TODO: Filtern
		// Entities are not detached, because the IDs are lazy-loaded
		List<Patient> pl;
		pl = this.em.createQuery("select p from Patient p", Patient.class).getResultList();
		return pl;
	}

	public synchronized List<Patient> getPatientsBlocking(Patient p) { //TODO: Filtern
		// Entities are not detached, because the IDs are lazy-loaded
		List<Patient> pl;
//		if (cache == null) cache = new LinkedList(this.em.createQuery("select p from Patient p", Patient.class).getResultList());
//		return cache;
		String geburtstag = p.getFields().get("geburtstag").getValue().toString();
		String geburtsmonat = p.getFields().get("geburtsmonat").getValue().toString();
		String geburtsjahr= p.getFields().get("geburtsjahr").getValue().toString();
		
		pl = this.em.createQuery("select p from Patient p JOIN p.blockingFields bf WHERE " +
				"KEY(bf) = 'geburtsmonat' AND VALUE(bf) = '" + geburtsmonat + "' OR " +
				"KEY(bf) = 'geburtsjahr' AND VALUE(bf) = '" + geburtsjahr + "' OR " +
				"KEY(bf) = 'geburtstag' AND VALUE(bf) = '" + geburtstag + "'", Patient.class).getResultList();
		logger.debug("Geblockte Patienten:" + pl.size());
		return pl;
	}

	/**
	 * Returns a detached list of the IDs of all patients.
	 * @return A list where every item represents the IDs of one patient.
	 */
	public synchronized List<Set<ID>> getAllIds()
	{
		List<Patient> patients = this.getPatients();
		List<Set<ID>> ret = new LinkedList<Set<ID>>();
		for (Patient p : patients) {
			Set<ID> thisPatientIds = p.getIds();
			this.em.detach(thisPatientIds);
			ret.add(thisPatientIds);
		}
		return ret;
	}
	/**
	 * Add an ID request to the database. In cases where a new ID is created, a
	 * new Patient object is persisted.
	 * @param req
	 */
	public synchronized void addIdRequest(IDRequest req){
		em.getTransaction().begin();
//		if (!em.contains(req.getAssignedPatient())) cache.add(req.getAssignedPatient());
		em.persist(req); //TODO: Fehlerbehandlung, falls PID schon existiert.		
		em.getTransaction().commit();
	}
	
	/**
	 * Update the persisted properties of an ID generator (e.g. the counter from which PIDs 
	 * are generated).
	 * @param mem
	 */
	public synchronized void updateIDGeneratorMemory(IDGeneratorMemory mem)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(mem);
		em.getTransaction().commit();
		em.close();
	}
	
	/**
	 * Mark the patient with ID idOfDuplicate as a duplicate of idOfOriginal.
	 * @see de.unimainz.imbei.mzid.Patient#isDuplicate()
	 * @see de.unimainz.imbei.mzid.Patient#getOriginal()
	 * @see de.unimainz.imbei.mzid.Patient#setOriginal(Patient)
	 * @param idOfDuplicate
	 * @param idOfOriginal
	 */
	public synchronized void markAsDuplicate(ID idOfDuplicate, ID idOfOriginal)
	{
		Patient pDuplicate = getPatient(idOfDuplicate);
		Patient pOriginal = getPatient(idOfOriginal);
		pDuplicate.setOriginal(pOriginal);
		updatePatient(pDuplicate);
//		this.cache = null;
	}
	
	/**
	 * Load the persisted properties for an ID generator.
	 * @param idString Identifier of the ID generator.
	 * 
	 */
	public IDGeneratorMemory getIDGeneratorMemory(String idString)
	{
		EntityManager em = emf.createEntityManager();
		TypedQuery<IDGeneratorMemory> q = em.createQuery("SELECT m FROM IDGeneratorMemory m WHERE m.idString = :idString", IDGeneratorMemory.class);
		q.setParameter("idString", idString);
		List<IDGeneratorMemory> result = q.getResultList();
		em.close();
		if (result.size() == 0)
			return null;
		else
			return result.get(0);
	}
	
	/**
	 * Persist changes made to a patient.
	 * @param p
	 */
	public synchronized void updatePatient(Patient p){
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(p);
		em.getTransaction().commit();
		em.close();
//		this.cache = null;
	}
	
	public synchronized void deleteAllPatients() {
//		this.cache = null;
		this.em.getTransaction().begin();
		this.em.createQuery("delete from Patient", Patient.class).executeUpdate();
		this.em.getTransaction().commit();
	}
}
