package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

public class ReadPatientTest extends JerseyTest {

	private WebResource resource = resource();
	private ClientResponse response;
	
	public ReadPatientTest() {
		super(TestUtilities.setUpTest());
	}
	
	/**
	 * Test functionality of the read patient token
	 */
	@Test
	public void testReadPatientToken() {
		String sessionId = TestUtilities.createSession(resource);
		
		// Generate tokenData
		JSONArray resultFields = TestUtilities.buildJSONArray("vorname");
		JSONArray resultIds = TestUtilities.buildJSONArray("pid");
		JSONObject searchId = TestUtilities.buildJSONObject("idType", "psn", "idString", "1");
		JSONObject tokenData = TestUtilities.createTokenDataReadPatient(resultFields, resultIds, searchId);
		
		// TODO: Anlegen mit falscher IP-Adresse → Erwarte 401 Unauthorized
		
		// Call without mainzellisteApiKey
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, null)
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token without mainzellisteApiKey did not return 401 status.", 401, response.getStatus());
		
		// Call with wrong mainzellisteApiKey
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, "wrongKey")
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with wrong mainzellisteApiKey did not return 401 status.", 401, response.getStatus());
		
		// Call with no existing session
		response = TestUtilities.getBuilderCreateToken(resource, "unbekannt", TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with non existing session did not return 404 status.", 404, response.getStatus());
		
		// Create Token with an unknown idType
		tokenData = TestUtilities.createTokenDataReadPatient(resultFields, resultIds, TestUtilities.buildJSONObject("idType", "unknownid", "idString", "1"));
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown idType not return 400 status.", 400, response.getStatus());
		
		// Create Token with an unknown resultField
		tokenData = TestUtilities.createTokenDataReadPatient(TestUtilities.buildJSONArray("unknownresultfield"), resultIds, searchId);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown resultField not return 400 status.", 400, response.getStatus());

		// Create Token with an unknown resultId
		tokenData = TestUtilities.createTokenDataReadPatient(resultFields, TestUtilities.buildJSONArray("unknownresultid"), searchId);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown resultId not return 400 status.", 400, response.getStatus());

		// Create Token with not existing id/pseudonym
		tokenData = TestUtilities.createTokenDataReadPatient(resultFields, resultIds, TestUtilities.buildJSONObject("idType", "psn", "idString", "-1"));
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown id/pseudonym not return 400 status.", 400, response.getStatus());
		
		// Create Token for readPatient
		tokenData = TestUtilities.createTokenDataReadPatient(resultFields, resultIds, searchId);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token not return 201 status.", 201, response.getStatus());
		
		// Extract tokenId
		String tokenId = TestUtilities.getTokenIdOfJSON(response.getEntity(JSONObject.class));
		
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
	 * Test functionality of the read patient
	 */
	@Test
	public void testReadPatient() {
		String sessionId = TestUtilities.createSession(resource);

		String[] keyArray = {"vorname", "nachname", "geburtsname", "geburtstag", "geburtsmonat", "geburtsjahr", "ort", "plz"};
		String[] valueArray = {"ReadPatientVorname", "ReadPatientNachname", "Hans", "01", "10", "2000", "Mainz", "55120"};
		
		// Add Dummy Patient for Testing
		JSONObject dummyPatientId = TestUtilities.addPatient(resource, valueArray[0], valueArray[1], valueArray[2], valueArray[3], valueArray[4], valueArray[5], valueArray[6], valueArray[7]);
		
		// Call without token
		response = TestUtilities.getBuilderPatient(resource, null, null)
				.get(ClientResponse.class);
		assertEquals("Read patients without token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Call with invalid (non-existing) token
		response = TestUtilities.getBuilderPatient(resource, "invalidToken", null)
				.get(ClientResponse.class);
		assertEquals("Read patients with non-existing token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		// Call with addToken not readToken 
		String tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "psn");
		response = TestUtilities.getBuilderPatient(resource, tokenId, null)
				.get(ClientResponse.class);
		assertEquals("Read Patient with wrong token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Generate tokenId for dummy Patient
		JSONArray resultFields = TestUtilities.buildJSONArray(keyArray);
		JSONArray resultIds = TestUtilities.buildJSONArray("psn");
		String dummyPatientTokenId = TestUtilities.createTokenIdReadPatient(resource, sessionId, resultFields, resultIds, dummyPatientId);
		
		// Read Patient n times and check if output is correct
		for (int i = 1; i < 20; i++) {
			response = TestUtilities.getBuilderPatient(resource, dummyPatientTokenId, TestUtilities.getApikey())
					.get(ClientResponse.class);
			assertEquals("Read Patient for the '" + i + "' time did not return 200 status. Message from server: " + response, 200, response.getStatus());
			
			JSONArray fields = response.getEntity(JSONArray.class);
			
			for (int j = 0; j < valueArray.length; j++) {
				assertEquals(i + ". time of Loop: Field '" + keyArray[j] + "' of Patient is not the same as it was given.", valueArray[j], TestUtilities.getStringOfJSON(fields, keyArray[j]));
			}
		}
	}
}
