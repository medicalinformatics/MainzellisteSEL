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
		int codeWord[], sum6, sum7, test6, test7;
		
		codeWord = PIDBackend.PID2c(pid);
		sum6 = PIDBackend.wsum1(codeWord); // checksum 1
		sum7 = PIDBackend.wsum2(codeWord); // checksum 2		
		test6 = sum6 ^ codeWord[6];
		test7 = sum7 ^ codeWord[7];
		if (test6 == 0 && test7 == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tries to correct the given PIDString.
	 * 
	 * @return correct PIDString or null if impossible to correct
	 */
	public static String correct(String PIDString){
		return PIDString;
	}
}
