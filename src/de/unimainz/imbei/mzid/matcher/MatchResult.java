package de.unimainz.imbei.mzid.matcher;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import de.unimainz.imbei.mzid.Patient;

@Embeddable
public class MatchResult {
	
	public enum MatchResultType {
		MATCH, NON_MATCH, POSSIBLE_MATCH, AMBIGOUS;
	};
	
	@Basic
	private MatchResultType type;
	
	private double bestMatchedWeight; 

	/**
	 * @return the bestMatchedWeight
	 */
	public double getBestMatchedWeight() {
		return bestMatchedWeight;
	}

	@ManyToOne
	private Patient bestMatchedPatient;
	
	public Patient getBestMatchedPatient()
	{
		return this.bestMatchedPatient;
	}
	
	/**
	 * Get the match result type of this result.
	 * 
	 * @return <ul>
	 * 	<li>MATCH: A sure match is found. getPatient() retreives the 
	 * 	matching bestMatchedPatient.
	 * 	<li>POSSIBLE_MATCH: An unsure match is found. getPatient() 
	 * retreives the best matching bestMatchedPatient.
	 * 	<li> NON_MATCH: No matching bestMatchedPatient was found. getPatient
	 * returns null.
	 * </ul>
	 * 
	 */
	public MatchResultType getResultType()
	{
		return this.type;
	}

	public MatchResult(MatchResultType type, Patient patient, double bestMatchedWeight)
	{
		this.type = type;
		this.bestMatchedPatient = patient;
		if (Double.isInfinite(bestMatchedWeight) || Double.isNaN(bestMatchedWeight))
			this.bestMatchedWeight = -Double.MAX_VALUE;
		else
			this.bestMatchedWeight = bestMatchedWeight;
	}	
}
