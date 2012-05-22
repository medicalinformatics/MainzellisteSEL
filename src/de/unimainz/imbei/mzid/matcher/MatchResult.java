package de.unimainz.imbei.mzid.matcher;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.unimainz.imbei.mzid.Patient;

@Embeddable
public class MatchResult {
	
	public enum MatchResultType {
		MATCH, NON_MATCH, POSSIBLE_MATCH, AMBIGOUS;
	};
	
	@Basic
	private MatchResultType type;

	@ManyToOne
	private Patient bestMatchedPatient;
	
	public Patient getBestMatchedPatient()
	{
		return this.bestMatchedPatient;
	}
	
	/**
	 * Get the match result type of this result.
	 * <ul>
	 * 	<li>MATCH: A sure match is found. getPatient() retreives the 
	 * 	matching bestMatchedPatient.
	 * 	<li>POSSIBLE_MATCH: An unsure match is found. getPatient() 
	 * retreives the best matching bestMatchedPatient.
	 * 	<li> NON_MATCH: No matching bestMatchedPatient was found. getPatient
	 * returns null.
	 * </ul>
	 * @return
	 */
	public MatchResultType getResultType()
	{
		return this.type;
	}

	public MatchResult(MatchResultType type, Patient patient)
	{
		this.type = type;
		this.bestMatchedPatient = patient;
	}	
}
