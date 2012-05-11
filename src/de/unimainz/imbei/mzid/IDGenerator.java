package de.unimainz.imbei.mzid;

public interface IDGenerator<I> {
	void init(IDGeneratorMemory mem);
	I getNext();
	
	/**
	 * Checks whether a given String is a valid PID.
	 * TODO: Verschieben
	 * 
	 * @param pid The PID which to check.
	 * @return true if pid is a correct PID, false otherwise.
	 */
	public boolean verify(String id);

	/**
	 * Tries to correct the given PIDString.
	 * Up to two errors are recognized, errors with one changed
	 * character or a transposition of adjacent characters can
	 * be corrected.
	 * 
	 * TODO: Verschieben
	 * 
	 * @return correct PIDString or null if impossible to correct
	 */
	public String correct(String PIDString);
	
	public I buildId(String id, String type);
}
