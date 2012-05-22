package de.unimainz.imbei.mzid;

import java.util.Map;

import de.unimainz.imbei.mzid.matcher.MatchResult;

/** Data structure for an ID request: Input fields, match result, type of ID */
public class IDRequest {
	
	/** Map of fields as provided by the input form. */
	private Map<String, Field<?>> inputFields;
	/** Type of the requested ID */
	private String idType;
	/** The match result, including the assigned patient */
	private MatchResult matchResult;
	
	public IDRequest(Map<String, Field<?>> inputFields, String idType,
			MatchResult matchResult) {
		super();
		this.inputFields = inputFields;
		this.idType = idType;
		this.matchResult = matchResult;
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
