/*
 * Copyright (C) 2013 Martin Lablans, Andreas Borg, Frank Ückert
 * Contact: info@mainzelliste.de

 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it 
 * with Jersey (https://jersey.java.net) (or a modified version of that 
 * library), containing parts covered by the terms of the General Public 
 * License, version 2.0, the licensors of this Program grant you additional 
 * permission to convey the resulting work.
 */
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
