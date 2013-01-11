package de.unimainz.imbei.mzid;

import javax.persistence.Entity;

import de.unimainz.imbei.mzid.exceptions.InvalidIDException;

@Entity
public class IntegerID extends ID {

	public IntegerID(String idString, String type) throws InvalidIDException {
		super(idString, type);
	}

	@Override
	public String getIdString() {
		return idString;
	}

	@Override
	protected void setIdString(String id) throws InvalidIDException {
		try {
			Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new InvalidIDException();
		}
		this.idString = id;
	}

}
