package de.unimainz.imbei.mzid.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;

/**
 * Handles reading and writing from and to the database.
 * 
 * @author Martin Lablans
 */
public enum Persistor {
	instance;
	
	private EntityManagerFactory emf;
	
	/** Caches patient list */
	private List<Patient> cache = null;
	
	private Persistor() {
		HashMap<String, String> persistenceOptions = new HashMap<String, String>();
		
		// Settings from mzid config
		persistenceOptions.put("javax.persistence.jdbc.driver", Config.instance.getProperty("db.driver"));
		persistenceOptions.put("javax.persistence.jdbc.url", Config.instance.getProperty("db.url"));
		persistenceOptions.put("javax.persistence.jdbc.user", Config.instance.getProperty("db.username"));
		persistenceOptions.put("javax.persistence.jdbc.password", Config.instance.getProperty("db.password"));
		
		// Other settings
		persistenceOptions.put("openjpa.jdbc.SynchronizeMappings", "buildSchema");
		
		emf = Persistence.createEntityManagerFactory("mzid", persistenceOptions);
		
		// Check database connection
		getPatients();
		
		Logger.getLogger(Persistor.class).info("Persistence has initialized successfully.");
	}
	
	public Patient getPatient(ID pid){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Patient> q = em.createQuery("SELECT p FROM Patient p JOIN p.ids id WHERE id.idString = :idString", Patient.class);
		q.setParameter("idString", pid.getIdString());
		List<Patient> result = q.getResultList();
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
		if (cache == null){
			EntityManager em = emf.createEntityManager();
			List<Patient> pl = em.createQuery("select p from Patient p", Patient.class).getResultList();
			em.close(); // causes all entities to be detached
			cache = new LinkedList<Patient>(pl);
		}
			
		return Collections.unmodifiableList(cache);
	}

	public synchronized void addIdRequest(IDRequest req){
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		// add assigned patient to cache if not yet persisted
		if (!em.contains(req.getAssignedPatient())  && this.cache != null)
			this.cache.add(req.getAssignedPatient());
		em.persist(req); //TODO: Fehlerbehandlung, falls PID schon existiert.
		em.getTransaction().commit();
		em.close();
	}
	
	public synchronized void updateIDGeneratorMemory(IDGeneratorMemory mem)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(mem);
		em.getTransaction().commit();
		em.close();
	}
	
	public synchronized void markAsDuplicate(ID idOfDuplicate, ID idOfOriginal)
	{
		Patient pDuplicate = getPatient(idOfDuplicate);
		Patient pOriginal = getPatient(idOfOriginal);
		pDuplicate.setOriginal(pOriginal);
		updatePatient(pDuplicate);
		cache = null;
	}
	
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
	
	public synchronized void updatePatient(Patient p){
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(p);
		em.getTransaction().commit();
		em.close();
		cache = null;
	}
}
