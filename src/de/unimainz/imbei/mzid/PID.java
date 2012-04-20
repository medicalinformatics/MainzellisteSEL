package de.unimainz.imbei.mzid;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unimainz.imbei.mzid.exceptions.InvalidPIDException;
import de.unimainz.imbei.mzid.webservice.PIDAdapter;

/**
 * A person's identificator. Once created, the PID is guaranteed to be valid. Immutable.
 * 
 * @see PIDGenerator to generate PIDs.
 * @author Martin Lablans
 */
@XmlJavaTypeAdapter(PIDAdapter.class)
public class PID {
	String PIDString;
	
	/**
	 * Creates a PID from a given PIDString.
	 * 
	 * @param PIDString String containing a valid PID.
	 * @throws InvalidPIDException The given PIDString is invalid and could not be corrected.
	 */
	public PID(String PIDString) throws InvalidPIDException {
		if(!verify(PIDString)){
			throw new InvalidPIDException();
		}
		this.PIDString = PIDString;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if(!(arg0 instanceof PID))
			return false;
		
		PID other = (PID)arg0;
		return other.PIDString.equals(PIDString);
	}
	
	@Override
	public int hashCode() {
		return PIDString.hashCode();
	}
	
	@Override
	public String toString() {
		return PIDString;
	}

	/**
	 * Checks whether a given String is a valid PID.
	 * 
	 * @param pid The PID which to check.
	 * @return true if pid is a correct PID, false otherwise.
	 */
	public static boolean verify(String pid){
		return PIDGenerator.isCorrectPID(pid);
	}

	/**
	 * Tries to correct the given PIDString.
	 * Up to two errors are recognized, errors with one changed
	 * character or a transposition of adjacent characters can
	 * be corrected.
	 * @return correct PIDString or null if impossible to correct
	 */
	public static String correct(String PIDString)  {
		return PIDGenerator.correctPID(PIDString);
	}
		
}
