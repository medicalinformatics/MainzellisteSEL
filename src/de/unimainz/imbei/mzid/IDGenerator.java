package de.unimainz.imbei.mzid;

import java.util.Properties;

/**
 * Generator for a given type of ID (e.g. PID, SIC, LabID, ...)
 * 
 * Each type of ID needs an own generator - even if based on the same algorithm, in which case
 * there would be several instances of the same generator implementation.
 * 
 * Implementations of this interface must provide an empty constructor and perform necessary
 * initializations via {@link #init(IDGeneratorMemory, String, Properties)}.
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
	 * @param idType "name" of the generated IDs, e.g. "gpohid"
	 * @param props Properties for this generator.These are the properties defined for
	 * this generator in the config, with the prefix idgenerators.{idtype} removed
	 */
	void init(IDGeneratorMemory mem, String idType, Properties props);
	
	
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
	 * Checks whether a given String is a valid ID.
	 * Implementations can consist of a simple data type check
	 * or on more sophisticated algorithms like {@link PIDGenerator#verify(String)} 
	 * 
	 * @param idString The ID which to check.
	 * @return true if id is a correct ID, false otherwise.
	 */
	public boolean verify(String idString);

	/**
	 * Tries to correct the given IDString.
	 * This method is only useful if the implementation uses
	 * an error-tolerant code which allows corrections of errors,
	 * for example {@link PIDGenerator#correct(String)}
	 * 
	 * @return correct IDString or null if impossible to correct
	 */
	public String correct(String idString);
	
	/**
	 * Gets the type ("name") of IDs this generator produces, e.g. "gpohid"
	 * 
	 * @return
	 */
	public String getIdType();
}
