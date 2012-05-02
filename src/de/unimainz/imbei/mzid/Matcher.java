package de.unimainz.imbei.mzid;

/**
 * This class is reponsible for comparing a given patient to those present in the local database.
 * 
 * @author Martin
 *
 */
public enum Matcher {
	instance;
	
	/**
	 * Checks if a given Patient is already present in the database.
	 * 
	 * @return match's PID; null if none found.
	 */
	public PID match(Person a){
		//TODO: implementieren
		return null;
	}
}
