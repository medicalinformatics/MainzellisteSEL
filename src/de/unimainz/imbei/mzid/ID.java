package de.unimainz.imbei.mzid;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.unimainz.imbei.mzid.exceptions.InvalidIDException;

/**
 * A person's identificator. Once created, the ID is guaranteed to be valid. Immutable.
 * 
 * @see IDGenerator to generate IDs.
 * @author Martin Lablans
 */
@Entity
@Table(name="ID", uniqueConstraints=@UniqueConstraint(columnNames={"idString","type"}))
public abstract class ID {
	@Id
	@GeneratedValue
	@JsonIgnore
	protected int idJpaId;
	
	@Basic
	protected String idString;
	
	@Basic
	protected String type;
	
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
		setIdString(idString);
	}
	
	/**
	 * String representation of this ID.
	 */
	public abstract String getIdString();
	protected abstract void setIdString(String id) throws InvalidIDException;
	
	/**
	 * Type of this ID according to config.
	 */
	public String getType(){
		return type;
	}
	
	protected void setType(String type){
		this.type = type;
	}
	
	/**
	 * Returns a generator that can be used to create IDs of the same type as this ID.
	 */
	@JsonIgnore
	@Transient
	public IDGenerator<? extends ID> getFactory(){
		return IDGeneratorFactory.instance.getFactory(getType());
	}
	
	@Override
	public String toString() {
		return String.format("%s=%s", getType(), getIdString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
