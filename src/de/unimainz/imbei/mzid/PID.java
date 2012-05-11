package de.unimainz.imbei.mzid;

import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unimainz.imbei.mzid.exceptions.InvalidIDException;
import de.unimainz.imbei.mzid.webservice.PIDAdapter;

/**
 * A person's identificator. Once created, the PID is guaranteed to be valid. Immutable.
 * 
 * @see PIDGenerator to generate PIDs.
 * @author Martin Lablans
 */
@XmlJavaTypeAdapter(PIDAdapter.class)
public class PID implements ID{
	String PIDString;
	IDGenerator<PID> generator;
	
	/**
	 * Creates a PID from a given PIDString.
	 * 
	 * @param PIDString String containing a valid PID.
	 * @throws InvalidIDException The given PIDString is invalid and could not be corrected.
	 */
	public PID(String PIDString) throws InvalidIDException {
		setId(PIDString);
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

	@Override
	public IDGenerator<PID> getFactory() {
		return generator;
	}

	@Override
	public String getId() {
		return toString();
	}

	@Override
	public void setId(String id) throws InvalidIDException{
		if(!getFactory().verify(PIDString)){
			throw new InvalidIDException();
		}
		PIDString = id;
	}
}
