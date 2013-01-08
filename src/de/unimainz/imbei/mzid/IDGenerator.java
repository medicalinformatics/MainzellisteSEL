package de.unimainz.imbei.mzid;

/**
 * Generator for a given type of ID (e.g. PID, SIC, LabID, ...)
 * 
 * Each type of ID needs an own generator - even if based on the same algorithm, in which case
 * there would be several instances of the same generator implementation.
 * 
 * @author Martin Lablans
 *
 * @param <I>
 */
public interface IDGenerator<I extends ID> {
	/**
	 * Called by the IDGeneratorFactory.
	 * 
	 * @param mem This allows the generator to "memorize" values, e.g. sequence counters.
	 * @param type "name" of the generated IDs, e.g. "gpohid"
	 */
	void init(IDGeneratorMemory mem, String idType);
	
	/**
	 * This is the method to call to generate a new unique (in its type) ID.
	 */
	I getNext();
	
	/**
	 * Generates (and, if possible, verifies) an ID instance based on an existing
	 * IDString.
	 * 
	 * @param id String representation of the ID to be instantiated.
	 * @return
	 */
	public I buildId(String id);
	
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
	
	/**
	 * Gets the type ("name") of IDs this generator produces, e.g. "gpohid"
	 * 
	 * @return
	 */
	public String getIdType();
}
