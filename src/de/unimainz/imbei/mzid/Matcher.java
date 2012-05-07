package de.unimainz.imbei.mzid;

import java.util.Iterator;
import java.util.Vector;

/**
 * This class is reponsible for comparing a given patient to those present in the local database.
 * 
 * @author Martin
 *
 */
public enum Matcher {
	instance;
	

	/** The list of Persons in the database */
	private Iterable<Patient> patientList;
	
	/** PID-Generator instance */
	private PIDGenerator pidGen;
	
	/**
	 * Checks if a given Patient is already present in the database.
	 * 
	 * @return match's PID; null if none found.
	 */
	public PID match(Patient a){

		Vector<Patient> directMatches = new Vector<Patient>();

		for (Patient b : patientList)
		{
			// assert that the persons have the same Characteristics 
			assert (a.getCharacteristics().keySet().equals(b.getCharacteristics().keySet()));
			
			// TODO: Eleganter lösen über Datenbankabfrage
			// get exact matches (i.e. all Characteristics agree)
			if (a.getCharacteristics().equals(b.getCharacteristics()))
				directMatches.add(b);
			
			// TODO: Gewichte berechnen, Klassifikation etc.
		}
		
		// Two or more exact matches are not possible (errornous database)
		assert (directMatches.size() < 2);
		
		if (directMatches.size() == 1)
			return directMatches.elementAt(0).getId();

		// ansonsten: Ausgefeilteres Matching
		
		// Wenn nichts gefunden: Neuen PID zurückgeben
		return new PID(pidGen.getNextPIDString());
		// TODO: Zähler in der Datenbank hochzählen
	
	}
}
