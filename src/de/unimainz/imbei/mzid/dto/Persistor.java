package de.unimainz.imbei.mzid.dto;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

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
	
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("mzid");
	
	private Persistor() {
		// Check database connection
		getPatients();
	}
	
	public Patient getPatient(ID pid){
		EntityManager em = emf.createEntityManager();
		TypedQuery<Patient> q = em.createQuery("SELECT p FROM Patient p JOIN p.ids id WHERE id.idString = :idString", Patient.class);
		q.setParameter("idString", pid.getIdString());
		List<Patient> result = q.getResultList();
		em.close();
		return result.get(0);
	}
	
	public List<Patient> getPatients() { //TODO: Filtern
		EntityManager em = emf.createEntityManager();
		List<Patient> pl = em.createQuery("select p from Patient p", Patient.class).getResultList();
		em.close(); // causes all entities to be detached
		return pl;
	}

	public synchronized void addIdRequest(IDRequest req){
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
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
	
//	public List<IDGeneratorMemory> getIDGeneratorMemories()
//	{
//		EntityManager em = emf.createEntityManager();
//		TypedQuery<IDGeneratorMemory> q = em.createQuery("SELECT m FROM IDGeneratorMemory m WHERE m.idString = :idString", IDGeneratorMemory.class);
//		q.setParameter("idString", idString);
//		List<IDGeneratorMemory> result = q.getResultList();
//		em.close();
//	}
	
	public synchronized void updatePatient(Patient p){
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(p);
		em.getTransaction().commit();
		em.close();
	}
}
