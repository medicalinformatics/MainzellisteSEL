package de.unimainz.imbei.mzid;

import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.codehaus.jackson.annotate.JsonIgnore;

import de.unimainz.imbei.mzid.matcher.MatchResult;

/** Data structure for an ID request: Input fields, match result, type of ID */
@Entity
@Table(name="IDRequest")
public class IDRequest {
	@Id
	@GeneratedValue
	@JsonIgnore
	private int idRequestJpaId; //JPA
	
	/** Map of fields as provided by the input form. */
	@OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	private Map<String, Field<?>> inputFields;
	
	/** Type of the requested ID */
	@Basic
	private String requestedIdType;
	
	/** The match result, including the matched patient */
	@Embedded
	private MatchResult matchResult;
	
	/** The patient object that was actually assigned. In case of a match this is usually equal
	 * to matchResult.patient.
	 */
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	private Patient assignedPatient;

	
	
	/**
	 * @param inputFields
	 * @param requestedIdType
	 * @param matchResult
	 * @param assignedPatient
	 */
	public IDRequest(Map<String, Field<?>> inputFields, String idType,
			MatchResult matchResult, Patient assignedPatient) {
		super();
		this.inputFields = inputFields;
		this.requestedIdType = idType;
		this.matchResult = matchResult;
		this.assignedPatient = assignedPatient;
	}

	public Patient getAssignedPatient() {
		return assignedPatient;
	}

	public Map<String, Field<?>> getInputFields() {
		return inputFields;
	}

	public String getRequestedIdType() {
		return requestedIdType;
	}

	public MatchResult getMatchResult() {
		return matchResult;
	}
		
	
}
