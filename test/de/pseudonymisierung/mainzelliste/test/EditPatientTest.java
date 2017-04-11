package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

import de.pseudonymisierung.mainzelliste.IDGeneratorFactory;
import de.pseudonymisierung.mainzelliste.Patient;
import de.pseudonymisierung.mainzelliste.dto.Persistor;

public class EditPatientTest extends JerseyTest {

	private WebResource resource = resource();
	private ClientResponse response;
	
	public EditPatientTest() {
		super(TestUtilities.setUpTest());
	}
	
	/**
	 * Test functionality of the edit patient token
	 */
	@Test
	public void testEditPatientToken() {
		String sessionId = TestUtilities.createSession(resource);

		// Generate tokenData
		JSONObject patientId = TestUtilities.buildJSONObject("idType", "psn", "idString", "1");
		JSONObject tokenData = TestUtilities.createTokenDataEditPatient(patientId, null);

		// TODO: Anlegen mit falscher IP-Adresse → Erwarte 401 Unauthorized

		// Call without mainzellisteApiKey
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, null)
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token without mainzellisteApiKey did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		// Call with wrong mainzellisteApiKey
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, "wrongKey")
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with wrong mainzellisteApiKey did not return 401 status.", 401, response.getStatus());
		
		// Call with no existing session
		response = TestUtilities.getBuilderCreateToken(resource, "unbekannt", TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with non existing session did not return 404 status.", 404, response.getStatus());
		
		// Create Token with not existing id/pseudonym
		tokenData = TestUtilities.createTokenDataEditPatient(TestUtilities.buildJSONObject("idType", "psn", "idString", "-1"), null);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown id/pseudonym not return 400 status.", 400, response.getStatus());

		// Create Token for editPatient
		tokenData = TestUtilities.createTokenDataEditPatient(patientId, null);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		JSONObject jsonObject = response.getEntity(JSONObject.class);
		assertEquals("Creating token not return 201 status.", 201, response.getStatus());

		// Create Token for editPatient with extid
		tokenData = TestUtilities.createTokenDataEditPatient(TestUtilities.buildJSONObject("idType", "extid", "idString", "1234"), null);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with extid not return 201 status.", 201, response.getStatus());

		// Extract tokenId
		String tokenId = TestUtilities.getTokenIdOfJSON(jsonObject);
		
		// Get Request to prove the tokenId for availability
		response = TestUtilities.getBuilderModifyToken(resource, sessionId, tokenId, TestUtilities.getApikey())
				.get(ClientResponse.class);
		assertEquals("Get Token did not return 200 status. Message from server: " + response.getEntity(String.class), 200, response.getStatus());
		
		// Delete Token
		response = TestUtilities.getBuilderModifyToken(resource, sessionId, tokenId, TestUtilities.getApikey())
				.delete(ClientResponse.class);
		assertEquals("Delete Token did not return 204 status.", 204, response.getStatus());
		
		// Prove if the token is deleted
		response = TestUtilities.getBuilderModifyToken(resource, sessionId, tokenId, TestUtilities.getApikey())
				.get(ClientResponse.class);
		assertEquals("Get Token did not return 404 status. Message from server: " + response.getEntity(String.class), 404, response.getStatus());
	}
	
	/**
	 * Test functionality of the edit patient
	 * @throws Exception to avoid errors
	 */
	@Test
	public void testEditPatient() throws Exception {
		String sessionId = TestUtilities.createSession(resource);
		
		JSONArray editPatient;
		String[] patientKeys = TestUtilities.getPatientKeys();
		
		// Add Dummy Patient for Testing
		JSONObject patientId = TestUtilities.addPatient(resource, "Edit", "Patient", "Schmitd", "15", "05", "2001", "Frankfurt", "60311");
		
		// Generate Formula Data
		JSONObject formData = TestUtilities.createJSONForm("Peter", "Baier", "Hans", "01", "01", "2000", "Mainz", "55120");
		
		// Call without token
		response = TestUtilities.getBuilderPatientEdit(resource, null, null)
				.put(ClientResponse.class, formData);
		assertEquals("Edit patient without token did not return 405 status. Message from server: " + response, 405, response.getStatus());

		// Call with invalid (non-existing) token
		response = TestUtilities.getBuilderPatientEdit(resource, "invalidToken", null)
				.put(ClientResponse.class, formData);
		assertEquals("Edit patient with non-existing token did not return 401 status. Message from server: " + response, 401, response.getStatus());
		
		// Call with addToken not editToken 
		String tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "psn");
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, null)
				.put(ClientResponse.class, formData);
		assertEquals("Edit patient with wrong token did not return 401 status. Message from server: " + response, 401, response.getStatus());

		// Edit patients fields without permission in given token
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, TestUtilities.buildJSONArray("vorname", "nachname"));
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit patient fields which are not given in the token did not return 401. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		editPatient = TestUtilities.readPatient(resource, patientId);
		for (int i = 0; i < patientKeys.length; i++) {
			assertNotEquals("Edit patient '" + patientKeys[i] + "' not expectet.", formData.get(patientKeys[i]), TestUtilities.getStringOfJSON(editPatient, patientKeys[i]));
		}
		
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		
		// Edit Patient without a required field with a loop one by one
		for (int i = 0; i < 5; i++) {
			String[] valueArray = {"Peter", "Bauer", "01", "01", "2000"};
			valueArray[i] = "";
			formData = TestUtilities.createJSONForm(valueArray[0], valueArray[1], null, valueArray[2], valueArray[3], valueArray[4], null, null);
			response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
					.put(ClientResponse.class, formData);
			assertEquals("Edit Patient without required field did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		}
		
		// Edit Patient with wrong birth date
		formData = TestUtilities.createJSONForm(null, null, null, "29", "02", "2001", null, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient with wrong birth date did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		// Edit Patient with birth date in the future
		formData = TestUtilities.createJSONForm(null, null, null, "01", "02", "2050", null, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient with birth date in the future did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());

		
		// --- Edit all Fields ---
		String[] patientValues = {"Jon", "Carlos", "Hans", "01", "01", "2000", "Mainz", "55120"};

		// Edit 'vorname'
		formData = new JSONObject();
		formData.put(patientKeys[0], patientValues[0]);
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient did not return 204 status. Message from server: " + response, 204, response.getStatus());
		editPatient = TestUtilities.readPatient(resource, patientId);
		assertEquals("Edited Patient field '" + patientKeys[0] + "' is not as edited.", patientValues[0], TestUtilities.getStringOfJSON(editPatient, patientKeys[0]));
		
		// Edit 'nachname'
		formData = new JSONObject();
		formData.put(patientKeys[1], patientValues[1]);
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient did not return 204 status. Message from server: " + response, 204, response.getStatus());
		editPatient = TestUtilities.readPatient(resource, patientId);
		assertEquals("Edited Patient field '" + patientKeys[1] + "' is not as edited.", patientValues[1], TestUtilities.getStringOfJSON(editPatient, patientKeys[1]));
		
		// Edit 'geburtsname'
		formData = new JSONObject();
		formData.put(patientKeys[2], patientValues[2]);
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient did not return 204 status. Message from server: " + response, 204, response.getStatus());
		editPatient = TestUtilities.readPatient(resource, patientId);
		assertEquals("Edited Patient field '" + patientKeys[2] + "' is not as edited.", patientValues[2], TestUtilities.getStringOfJSON(editPatient, patientKeys[2]));
		
		// Edit 'geburtstag'
		formData = new JSONObject();
		formData.put(patientKeys[3], patientValues[3]);
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit 'geburtstag' without 'geburtsmonat' or 'geburtsjahr' did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		// Edit 'geburtstag', 'geburtsmonat', 'geburtsjahr'
		formData = new JSONObject();
		formData.put(patientKeys[3], patientValues[3]);
		formData.put(patientKeys[4], patientValues[4]);
		formData.put(patientKeys[5], patientValues[5]);
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient did not return 204 status. Message from server: " + response, 204, response.getStatus());
		editPatient = TestUtilities.readPatient(resource, patientId);
		assertEquals("Edited Patient field '" + patientKeys[3] + "' is not as edited.", patientValues[3], TestUtilities.getStringOfJSON(editPatient, patientKeys[3]));
		assertEquals("Edited Patient field '" + patientKeys[4] + "' is not as edited.", patientValues[4], TestUtilities.getStringOfJSON(editPatient, patientKeys[4]));
		assertEquals("Edited Patient field '" + patientKeys[5] + "' is not as edited.", patientValues[5], TestUtilities.getStringOfJSON(editPatient, patientKeys[5]));
		
		// Edit 'ort'
		formData.put(patientKeys[6], patientValues[6]);
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient did not return 204 status. Message from server: " + response, 204, response.getStatus());
		editPatient = TestUtilities.readPatient(resource, patientId);
		assertEquals("Edited Patient field '" + patientKeys[6] + "' is not as edited.", patientValues[6], TestUtilities.getStringOfJSON(editPatient, patientKeys[6]));
		
		// Edit 'plz'
		formData.put(patientKeys[7], patientValues[7]);
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient not return 204 status. Message from server: " + response, 204, response.getStatus());
		editPatient = TestUtilities.readPatient(resource, patientId);
		assertEquals("Edited Patient field '" + patientKeys[7] + "' is not as edited.", patientValues[7], TestUtilities.getStringOfJSON(editPatient, patientKeys[7]));

        // Edit 'extid'
        formData.put("extid", "1235");
        tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
        response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
                .put(ClientResponse.class, formData);
        assertEquals("Edit Patient not return 204 status. Message from server: " + response, 204, response.getStatus());

		// Edit 'extid' (extid for this patient isn't empty)
		formData.put("extid", "1357");
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, patientId, null);
		response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient not return 409 status. Message from server: " + response, 409, response.getStatus());
	}

    /**
     * Test if fields set to null are represented correctly.
     *
     * Test for issue PROB-278.
     *
     */
    @Test
    public void testSetFieldToNull() throws JSONException {
        String sessionId = TestUtilities.createSession(resource);

        String[] keyArray = { "vorname", "nachname", "geburtsname", "geburtstag", "geburtsmonat", "geburtsjahr", "ort",
                "plz" };
        String[] valueArray = { "EditPatientVorname", "EditPatientNachname", "Wolfgang", "11", "11", "1977", "Wiesbaden",
                "65197" };

        // Add Dummy Patient for Testing
        JSONObject dummyPatientId = TestUtilities.addPatient(resource, valueArray[0], valueArray[1], valueArray[2],
                valueArray[3], valueArray[4], valueArray[5], valueArray[6], valueArray[7]);

        // Set 'ort' to null
        String keyToSetToNull = keyArray[6];
        JSONObject formData = new JSONObject();
        formData.put(keyToSetToNull, JSONObject.NULL);
        String tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, dummyPatientId, null);
        response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
                .put(ClientResponse.class, formData);

        // Verify that null is translated to an empty string internally
        Patient patientToRead = Persistor.instance.getPatient(IDGeneratorFactory.instance.idFromJSON(dummyPatientId));
        assertEquals("Field set to null in edit request is not empty string in patient object", "",
                patientToRead.getFields().get(keyToSetToNull).getValue());

        // Verify that null is translated to an empty string in REST response
        JSONArray readPatientData = TestUtilities.readPatient(resource, dummyPatientId);
        Object fieldToCheck = readPatientData.getJSONObject(0).getJSONObject("fields").get(keyToSetToNull);
        assertEquals("Field set to null in edit request is not emtpy string in REST response", "", fieldToCheck);
    }
}