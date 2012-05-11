package de.unimainz.imbei.mzid;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unimainz.imbei.mzid.dto.IDAdapter;
import de.unimainz.imbei.mzid.exceptions.InvalidIDException;

@XmlJavaTypeAdapter(IDAdapter.class)
public class PID extends ID{
	String PIDString;
	String type;
	
	public PID(String PIDString, String type) throws InvalidIDException {
		super(PIDString, type);
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
	public String getId() {
		return toString();
	}

	@Override
	protected void setId(String id) throws InvalidIDException{
		PIDString = id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	protected void setType(String type) {
		this.type = type;
	}
}
