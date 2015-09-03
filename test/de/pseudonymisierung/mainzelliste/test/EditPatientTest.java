package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
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
		JSONObject patientId = TestUtilities.buildJSONObject("idType", "intid", "idString", "1");
		JSONObject tokenData = TestUtilities.createTokenDataEditPatient(patientId);
		
		// TODO: Anlegen mit falscher IP-Adresse → Erwarte 401 Unauthorized

		// TODO: In Datenbank ein Patienten mit intid 1 erstellen
		// Call without mainzellisteApiKey
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, null)
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token without mainzellisteApiKey did not return 401 status.", 401, response.getStatus());
		
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
		tokenData = TestUtilities.createTokenDataEditPatient(TestUtilities.buildJSONObject("idType", "intid", "idString", "-1"));
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown id/pseudonym not return 401 status.", 401, response.getStatus());
	}
	
	@Test
	public void testEditPatient() {
		
	}
}
