package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.test.framework.JerseyTest;

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
		String tokenRequestPath = "sessions/" + sessionId + "/tokens";

		// Generate tokenData
		JSONObject patientId = TestUtilities.buildJSONObject("idType", "psn", "idString", "1");
		JSONObject tokenData = TestUtilities.createTokenDataEditPatient(patientId);

		// Add Dummy Patient for Testing
		TestUtilities.addDummyPatient(resource);
		
		// TODO: Anlegen mit falscher IP-Adresse → Erwarte 401 Unauthorized

		// Call without mainzellisteApiKey
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, null)
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token without mainzellisteApiKey did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		// Call with wrong mainzellisteApiKey
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, "wrongKey")
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with wrong mainzellisteApiKey did not return 401 status.", 401, response.getStatus());
		
		// Call with no existing session
		response = TestUtilities.getBuilderTokenPost(resource, "/sessions/unbekannt/tokens", TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with non existing session did not return 404 status.", 404, response.getStatus());
		
		// Create Token with not existing id/pseudonym
		tokenData = TestUtilities.createTokenDataEditPatient(TestUtilities.buildJSONObject("idType", "psn", "idString", "-1"));
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown id/pseudonym not return 400 status.", 400, response.getStatus());
		
		// Create Token for editPatient
		tokenData = TestUtilities.createTokenDataEditPatient(patientId);
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		JSONObject jsonObject = response.getEntity(JSONObject.class);
		assertEquals("Creating token not return 201 status.", 201, response.getStatus());
		
		// Extract tokenId
		String tokenId = TestUtilities.getTokenIdOfJSON(jsonObject);
		
		// Get Request to prove the tokenId for availability
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath + "/" + tokenId, TestUtilities.getApikey())
				.get(ClientResponse.class);
		assertEquals("Get Token did not return 200 status. Message from server: " + response.getEntity(String.class), 200, response.getStatus());
		
		// Delete Token
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath + "/" + tokenId, TestUtilities.getApikey())
				.delete(ClientResponse.class);
		assertEquals("Delete Token did not return 204 status.", 204, response.getStatus());
		
		// Prove if the token is deleted
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath + "/" + tokenId, TestUtilities.getApikey())
				.get(ClientResponse.class);
		assertEquals("Get Token did not return 404 status. Message from server: " + response.getEntity(String.class), 404, response.getStatus());
	}
	
	/**
	 * Test functionality of the edit patient
	 */
	@Test
	public void testEditPatient() {
		String sessionId = TestUtilities.createSession(resource);
		String tokenPath = "sessions/" + sessionId + "/tokens";
		
		String patientsPath = "patients/";
		
		// Add Dummy Patient for Testing
		JSONObject patienId = TestUtilities.addPatient(resource, "Edit", "Patient", "Schmitd", "15", "05", "2001", "Frankfurt", "60311");
		
		// Generate Formula Data
		Form formData = TestUtilities.createForm("Peter", "Baier", "Hans", "01", "01", "2000", "Mainz", "55120");
		
		// Call without token
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), null, null, MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, formData);
		assertEquals("Edit patient without token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Call with invalid (non-existing) token
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), "invalidToken", null, MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, formData);
		assertEquals("Edit patient with non-existing token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		// Call with addToken not editToken 
		String tokenId = TestUtilities.createTokenIdAddPatient(resource, tokenPath, "psn");
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, null, MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient with wrong token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		
		// TODO BELOW		
		
		tokenId = TestUtilities.createTokenIdEditPatient(resource, tokenPath, patienId);
		
		// Edit Patient without a required field with a loop one by one
		for (int i = 0; i < 5; i++) {
			String[] valueArray = {"Peter", "Bauer", "01", "01", "2000"};
			valueArray[i] = "";
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], null, valueArray[2], valueArray[3], valueArray[4], null, null);
			response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey(), MediaType.APPLICATION_JSON)
					.put(ClientResponse.class, formData);
			assertEquals("Edit Patient without required field did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		}
		
		// Edit Patient with wrong birth date
		formData = TestUtilities.createForm(null, null, null, "29", "02", null, null, null);
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey(), MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient with wrong birth date did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		// Edit Patient with birth date in the future
		formData = TestUtilities.createForm(null, null, null, null, null, "2050", null, null);
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey(), MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, formData);
		assertEquals("Edit Patient with birth date in the future did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());

		// Edit Patient with a empty field one by one
		for (int i = 0; i < 3; i++) {
			tokenId = TestUtilities.createTokenIdEditPatient(resource, tokenPath, patienId);
			String[] valueArray = {"Jon", "Carlos", "Hans", "01", "01", "2000", "Mainz", "55120"};
			valueArray[i] = "";
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], valueArray[2], valueArray[3], valueArray[4], valueArray[5], valueArray[6], valueArray[7]);
			response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey(), MediaType.APPLICATION_JSON)
					.put(ClientResponse.class, formData);
			assertEquals("Edit Patient with empty field did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());
		}
	}
}
