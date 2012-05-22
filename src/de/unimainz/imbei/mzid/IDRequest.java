package de.unimainz.imbei.mzid;

import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.unimainz.imbei.mzid.matcher.MatchResult;

/** Data structure for an ID request: Input fields, match result, type of ID */
@Entity
@Table(name="IDRequest")
public class IDRequest {
	@Id
	@GeneratedValue
	@JsonIgnore
	private int idRequestId; //JPA
	
	/** Map of fields as provided by the input form. */
	private Map<String, Field<?>> inputFields;
	/** Type of the requested ID */
	private String idType;
	/** The match result, including the matched patient */
	@Embedded
	private MatchResult matchResult;
	/** The patient object that was actually assigned. In case of a match this is usually equal
	 * to matchResult.patient.
	 */
	private Patient assignedPatient;

	
	
	/**
	 * @param inputFields
	 * @param idType
	 * @param matchResult
	 * @param assignedPatient
	 */
	public IDRequest(Map<String, Field<?>> inputFields, String idType,
			MatchResult matchResult, Patient assignedPatient) {
		super();
		this.inputFields = inputFields;
		this.idType = idType;
		this.matchResult = matchResult;
		this.assignedPatient = assignedPatient;
	}

	public Patient getAssignedPatient() {
		return assignedPatient;
	}

	public Map<String, Field<?>> getInputFields() {
		return inputFields;
	}

	public String getIdType() {
		return idType;
	}

	public MatchResult getMatchResult() {
		return matchResult;
	}
		
	
}
