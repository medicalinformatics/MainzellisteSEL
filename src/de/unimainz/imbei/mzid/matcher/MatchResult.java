package de.unimainz.imbei.mzid.matcher;

import de.unimainz.imbei.mzid.Patient;

public class MatchResult {
	
	public enum MatchResultType {
		MATCH, NON_MATCH, POSSIBLE_MATCH;
	};
	
	private MatchResultType type;
	private Patient patient;
	
	public Patient getPatient()
	{
		return this.patient;
	}
	
	/**
	 * Get the match result type of this result.
	 * <ul>
	 * 	<li>MATCH: A sure match is found. getPatient() retreives the 
	 * 	matching patient.
	 * 	<li>POSSIBLE_MATCH: An unsure match is found. getPatient() 
	 * retreives the best matching patient.
	 * 	<li> NON_MATCH: No matching patient was found. getPatients
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
		this.patient = patient;
	}	
}
