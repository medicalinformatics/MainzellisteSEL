package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

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
	
	@Test
	public void testEditPatientToken() {
		String sessionId = TestUtilities.createSession(resource);
		String tokenRequestPath = "sessions/" + sessionId + "/tokens";

		// Generate tokenData
		JSONObject patientId = TestUtilities.buildJSONObject("idType", "psn", "idString", "1");
		JSONObject tokenData = TestUtilities.createTokenDataEditPatient(patientId);
		
		// TODO: Anlegen mit falscher IP-Adresse → Erwarte 401 Unauthorized

		// TODO: In Datenbank ein Patienten mit psn 1 erstellen
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
		
		// TODO: Anlegen mit falschem Format → Erwarte 400 Bad Request
		
		// TODO: User mit inId 1 in der datenbank anlegen
		// Create Token for editPatient
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token not return 201 status.", 201, response.getStatus());
		// TODO: Rückgabe des Tokens Testen
		
		// Create Token with not existing id/pseudonym
		tokenData = TestUtilities.createTokenDataEditPatient(TestUtilities.buildJSONObject("idType", "psn", "idString", "-1"));
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown id/pseudonym not return 400 status.", 400, response.getStatus());
	}
	
	@Test
	public void testEditPatient() {
		String sessionId = TestUtilities.createSession(resource);
		String tokenPath = "sessions/" + sessionId + "/tokens";
		
		String patientsPath = "patients";
		
		// Generate Formula Data
		Form formData = TestUtilities.createForm("Peter", "Baier", "Hans", "01", "01", "2000", "Mainz", "55120");
		
		// Call without token -> yields 404 Not Found as tokenId is part of the path (/patients/tokenId/{tokenId})
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath).path("tokenId/"), null, null)
				.put(ClientResponse.class, formData);
		assertEquals("Edit patient without token did not return 404 status. Message from server: " + response.getEntity(String.class), 404, response.getStatus());

		// Call with invalid (non-existing) token
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath).path("tokenId/invalidToken"), null, TestUtilities.getApikey())
				.put(ClientResponse.class, formData);
		assertEquals("Edit patient with non-existing token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
	}
}
