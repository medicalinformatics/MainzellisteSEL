package de.unimainz.imbei.mzid;

/**
 * A person's identificator. Once created, the PID is guaranteed to be valid. Immutable.
 * 
 * @see PIDGenerator to generate PIDs.
 * @author Martin Lablans
 */
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
			String c = correct(PIDString);
			if(c==null)
				throw new InvalidPIDException();
			PIDString = c;
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
	 * @param pid zu prüfender String
	 */
	public static boolean verify(String pid){
		return PIDGenerator.isCorrectPID(pid);
	}

	/**
	 * Tries to correct the given PIDString.
	 * 
	 * @return correct PIDString or null if impossible to correct
	 */
	public static String correct(String PIDString)  {
		return PIDGenerator.correctPID(PIDString);
	}
		
}
