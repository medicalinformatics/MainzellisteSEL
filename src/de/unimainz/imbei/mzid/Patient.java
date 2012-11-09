package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.unimainz.imbei.mzid.dto.PatientAdapter;
import de.unimainz.imbei.mzid.exceptions.CircularDuplicateRelationException;
import de.unimainz.imbei.mzid.exceptions.InternalErrorException;

@XmlRootElement
@Entity
public class Patient {
	/**
	 * Internal ID. Set by JPA when on first persistance.
	 */
	@Id
	@GeneratedValue
	@JsonIgnore
	private int patientJpaId; // JPA
	

	private String stringVal = "String";

	public static String classToString(Class clazz) {
		return clazz.getName();
	}
	
	public static Class stringToClass(String clazz) {
		Class cl;
		try {
				cl = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			cl = Object.class;
		} 
		return cl;
	}
	/**
	 * Needed to determine if two Patient object refer to the same database entry.
	 * @return the patientJpaId
	 */
	public int getPatientJpaId() {
		return patientJpaId;
	}

	@OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY)
	private Set<ID> ids;
	
//	@OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
//	@Persistent
//	@Type(String.class)
//	@Column(length=4096)
//	@Externalizer("de.unimainz.imbei.mzid.dto.PatientAdapter.fieldsToString")
//	@Factory("de.unimainz.imbei.mzid.dto.PatientAdapter.stringToFields")
	@Transient
	private Map<String, Field<?>> fields;
	
	@Column(columnDefinition="text",length=-1)
	private String fieldsString;
	
	@Column(length=4096)
	private String inputFieldsString;
	
	@PrePersist
	@PreUpdate
	public void prePersist() {
		this.fieldsString = fieldsToString(this.fields);
		this.inputFieldsString = fieldsToString(this.inputFields);
	}
	
	@PostLoad
	public void postLoad() {
		this.fields = stringToFields(this.fieldsString);
		this.inputFields = stringToFields(this.inputFieldsString);
	}
	
	public static String fieldsToString(Map<String, Field<?>> fields) {
		try {
			JSONObject fieldsJson = new JSONObject();
			for (String fieldName : fields.keySet())
			{
				JSONObject thisField = new JSONObject();
				thisField.put("class", fields.get(fieldName).getClass().getName());
				thisField.put("value", fields.get(fieldName).getValueJSON());
				fieldsJson.put(fieldName, thisField);
			}
			return fieldsJson.toString();
		} catch (JSONException e) {
			Logger.getLogger(PatientAdapter.class).error("Exception: ", e);
			throw new InternalErrorException();
		}
	}
	
	public static Map<String, Field<?>> stringToFields(String fieldsJsonString) {
		try {
			Map<String, Field<?>> fields = new HashMap<String, Field<?>>();
			JSONObject fieldsJson = new JSONObject(fieldsJsonString);
			Iterator it = fieldsJson.keys();
			while(it.hasNext()) {
				String fieldName = (String) it.next();
				JSONObject thisFieldJson = fieldsJson.getJSONObject(fieldName); 
				String fieldClass = thisFieldJson.getString("class");
				String fieldValue = thisFieldJson.getString("value");
				Field<?> thisField = (Field) Class.forName(fieldClass).newInstance();
				thisField.setValue(fieldValue);
				fields.put(fieldName, thisField);
			} 
			return fields;
		} catch (Exception e) {
			Logger.getLogger(PatientAdapter.class).error("Exception: ", e);
			throw new InternalErrorException();
		}
	}
	
	//@OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	@Transient
	/**
	 * Input fields as read from form (before transformation).
	 * 
	 */
	private Map<String, Field<?>> inputFields;
	
	@Transient
	private Logger logger = Logger.getLogger(this.getClass());
	
	public Map<String, Field<?>> getInputFields() {
		return inputFields;
	}

	public void setInputFields(Map<String, Field<?>> inputFields) {
		this.inputFields = inputFields;
	}

	private boolean isTentative = false;

	public boolean isTentative() {
		return isTentative;
	}

	public void setTentative(boolean isTentative) {
		this.isTentative = isTentative;
		for (ID id : this.ids)
		{
			id.setTentative(isTentative);
		}
	}
	
	/**
	 * Check wether p refers to the same Patient in the database
	 * (i.e. their patientJpaId values are equal).
	 * @param p
	 * @return
	 */
	public boolean sameAs(Patient p)
	{
		return (this.getPatientJpaId() == p.getPatientJpaId()); 
	}

	/**
	 * Gets the original patient if this patient is a duplicate.
	 * 
	 * @return <ul>
	 * 	<li>this if this.original == null</li>
	 * 	<li>original.getOriginal() otherwise</li>
	 * </ul>
	 */
	public Patient getOriginal() {
		if (this.original == null || this.original == this) return this;
		else return this.original.getOriginal();
	}

	public void setOriginal(Patient original) {
		if (original == null || original.sameAs(this))
		{
			this.original = null;
			return;
		}
		// Check if operation would lead to a circular relation
		// (setting a as duplicate of b when b is a duplicate of a)
		if (original.getOriginal().sameAs(this))
		{
			// TODO generalisieren für andere IDs
			CircularDuplicateRelationException e = new CircularDuplicateRelationException(
					this.getId("pid").getIdString(), original.getId("pid").getIdString());
			logger.error(e.getMessage());
			throw e;
		}
			this.original = original;
	}
	
	public boolean isDuplicate()
	{
		return (this.original != null);
	}

	/**
	 * If p.original is not null, p is considered a duplicate of p.original. PID requests
	 * that find p as the best matching patient should return the PID of p.getOriginal().
	 * 
	 */
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	private Patient original = null;
	
	
	
	public Patient() {}
	
	public Patient(Set<ID> ids, Map<String, Field<?>> c) {
		this.ids = ids;
		this.fields = c;
	}
	
	public Set<ID> getIds(){
		return Collections.unmodifiableSet(ids);
	}
	
	public ID getId(String type)
	{
		for (ID thisId : ids)
		{
			if (thisId.getType().equals(type))
				return thisId;
		}
		return null;
	}
	
	public void setIds(Set<ID> ids) {
		this.ids = ids;
	}
	
	public Map<String, Field<?>> getFields() {
		return fields;
	}
	
	public void setFields(Map<String, Field<?>> Fields) {
		this.fields = Fields;
	}
	
	@Override
	public String toString() {
		return fields.toString();
	}
}