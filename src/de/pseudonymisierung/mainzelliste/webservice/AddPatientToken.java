package de.pseudonymisierung.mainzelliste.webservice;

import java.util.HashMap;
import java.util.Map;

public class AddPatientToken extends Token {

	/**
	 * Fields transmitted on token creation
	 */
	private Map<String, String> fields;

	public AddPatientToken(String tid, String type) {
		super(tid, type);

		// read fields from JSON data
		this.fields = new HashMap<String, String>();
	}
	
	AddPatientToken() {
		super();
		this.setType("addPatient");
	}

	public void setData(Map<String, ?> data) {
		super.setData(data);
		// read fields from JSON data
		this.fields = new HashMap<String, String>();
		if (this.getData().containsKey("fields")) {
			Map<String, ?> serverFields = this.getDataItemMap("fields");
			for (String key : serverFields.keySet()) {
				String value = serverFields.get(key).toString();
				fields.put(key, value);
			}
		}		
	}
	/**
	 * Return the fields transmitted on token creation.
	 */
	public Map<String, String> getFields() {
		return this.fields;
	}
}
