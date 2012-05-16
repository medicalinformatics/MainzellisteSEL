package de.unimainz.imbei.mzid;

import javax.persistence.Entity;

import de.unimainz.imbei.mzid.exceptions.InvalidIDException;

//@XmlJavaTypeAdapter(IDAdapter.class)
@Entity
public class PID extends ID{
	public PID(String PIDString, String type) throws InvalidIDException {
		super(PIDString, type);
	}
	
	@Override
	public boolean equals(Object arg0) {
		if(!(arg0 instanceof PID))
			return false;
		
		PID other = (PID)arg0;
		return other.idString.equals(idString);
	}
	
	@Override
	public String getIdString() {
		return idString;
	}

	@Override
	protected void setIdString(String id) throws InvalidIDException {
		if(!getFactory().verify(id))
			throw new InvalidIDException();
		
		idString = id;
	}
}
