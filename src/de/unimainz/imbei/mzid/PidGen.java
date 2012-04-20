/**
 * 
 */
package de.unimainz.imbei.mzid;

/**
 * @author borg
 *
 */
@Deprecated
public class PidGen {
	
	
	private int key1;
	private int key2;
	private int key3;
	
	/**
	 * Initializes the PID generator
	 * @param key1 First encryption key.
	 * @param key2 Second encryption key.
	 * @param key3 Third encryption key.
	 */
	public PidGen(int key1, int key2, int key3)
	{
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
	}
	
	/**
	 * Generate PID for a given counter
	 * @param counter The internal counter which is encrypted to form the PID.
	 * @return
	 */
	public String generatePID(int counter)
	{
		String newPid = new String();
		return (newPid);
	}

	
	/* Kann man die folgenden Methoden zusammenfassen, um die Berechnungen nicht
	 * zweimal machen zu müssen? (Prüfen und Korrigieren sind ein Rechenschritt, oder?)
	 */
	
	/**
	 * Check if the given PID is correct.
	 * @param pid The PID to check.
	 * @return true if pid is a valid PID, false otherwise
	 */
	public boolean checkPID(String pid)
	{
		//TODO: implementieren
		return true;
	}
	
	/**
	 * Correct an invalid PID.
	 * @param pid The PID to correct.
	 * @return The corrected PID
	 * 
	 * If a valid PID is provided, 
	 */
	public String correctPID(String pid)
	{
		String correctedPID = new String();
		return (correctedPID);
	}
}
