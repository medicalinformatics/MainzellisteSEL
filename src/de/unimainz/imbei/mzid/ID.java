package de.unimainz.imbei.mzid;

import de.unimainz.imbei.mzid.exceptions.InvalidIDException;

/**
 * A person's identificator. Once created, the ID is guaranteed to be valid. Immutable.
 * 
 * @see IDGenerator to generate IDs.
 * @author Martin Lablans
 */
public abstract class ID {
	
	/**
	 * Creates an ID from a given IDString.
	 * 
	 * @param idString String containing a valid ID.
	 * @param type Type as according to config.
	 * @throws InvalidIDException The given IdString is invalid and could not be corrected.
	 */
	public ID(String idString, String type) throws InvalidIDException {
		setType(type);
		if(!getFactory().verify(idString)){
			throw new InvalidIDException();
		}
		setId(idString);
	}
	
	/**
	 * String representation of this ID.
	 */
	public abstract String getId();
	
	/**
	 * Type of this ID according to config.
	 */
	public abstract String getType();
	
	protected abstract void setId(String id) throws InvalidIDException;
	protected abstract void setType(String type);
	
	public IDGenerator<? extends ID> getFactory(){
		return Config.instance.getFactory(getType());
	}
}
