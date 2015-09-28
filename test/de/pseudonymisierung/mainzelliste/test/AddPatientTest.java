package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class AddPatientTest extends JerseyTest {
	private WebResource resource;
	private ClientResponse response;
	
	private int callbackPort = 8888;
	private String callbackRessource = "/receiveCallback";
	
	public AddPatientTest() {
		super(TestUtilities.setUpTest());
	}
	
	@Before
	public void setUp() {
		resource = resource();
	}
	
	/**
	 * Test functionality of the add patient token
	 */
	@Test
	public void testAddPatientToken() throws Exception {
		String sessionId = TestUtilities.createSession(resource);
		String tokenRequestPath = "sessions/" + sessionId + "/tokens";
		
		// Generate tokenData
		JSONObject resultFields = TestUtilities.buildJSONObject("vorname", "Max", "nachname", "Muster");
		JSONArray idTypes = TestUtilities.buildJSONArray("psn");
		String redirect = "https://example.org/index.php?token={tokenId}";
		JSONObject tokenData = TestUtilities.createTokenDataAddPatient(idTypes, null, null, null);
		
		// TODO: Anlegen mit falscher IP-Adresse → Erwarte 401 Unauthorized
		
		// Call without mainzellisteApiKey
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, null)
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token without mainzellisteApiKey did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		// Call with wrong mainzellisteApiKey
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, "wrongKey")
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with wrong mainzellisteApiKey did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		// Call with no existing session
		response = TestUtilities.getBuilderTokenPost(resource, "/sessions/unbekannt/tokens", TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with non existing session did not return 404 status. Message from server: " + response.getEntity(String.class), 404, response.getStatus());
		
		// Request with not existing IdType
		tokenData = TestUtilities.createTokenDataAddPatient(TestUtilities.buildJSONArray("pid", "noPid"), null, null, redirect);
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token for addPatient with wrong ID type did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		// Request with not existing field
		tokenData = TestUtilities.createTokenDataAddPatient(idTypes, TestUtilities.buildJSONObject("unbekannt", "unbekannt"), null, redirect);
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token for addPatient with wrong Field did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		
		
//		// TODO: Request with callback check if id's (idType, IdString) and tokenId is right -> 201 Create
//		HttpReceiver receiver = new HttpReceiver(callbackPort, callbackRessource);
//		receiver.start();
//		String tokenId = TestUtilities.createTokenIdAddPatient(resource, tokenRequestPath, "psn");
//		String result = resource.path("/patients")
//				.queryParam("tokenId", tokenId)
//				.type(MediaType.APPLICATION_FORM_URLENCODED)
//				.accept(MediaType.APPLICATION_JSON)
//				.header("mainzellisteApiVersion", "2.0")
//				.post(String.class, resultFields);
//		receiver.stop();
//		
//		HttpExchange callback = receiver.getReceivedRequest();
//		assertEquals("Wrong HTTP method in callback.", "POST", callback.getRequestMethod());
//
//		JSONObject callbackObject = new JSONObject(receiver.getReceivedEntity());
//		assertTrue("No Token-ID in callback", callbackObject.has("tokenId"));
//		assertEquals("Wrong Token-ID in callback", tokenId, callbackObject.getString("tokenId"));
//		assertTrue("No IDs in callback", callbackObject.has("ids"));
//		JSONArray callbackIds = callbackObject.getJSONArray("ids");

		
		
		// TODO: Request with redirect and with non existing IdType -> 400 Bad Request
		
		// TODO: Request with redirect check if id's (idType, IdString) and tokenId is right -> 201 Create
		
		// Create Token for addPatient
		tokenData = TestUtilities.createTokenDataAddPatient(idTypes, resultFields, null, null);
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		JSONObject jsonObject = response.getEntity(JSONObject.class);
		assertEquals("Creating token not return 201 status. Message from server: " + jsonObject, 201, response.getStatus());
		
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
	 * Test functionality of the add patient 
	 */
	@Test
	public void testAddPatient() {
		String sessionId = TestUtilities.createSession(resource);
		String tokenPath = "sessions/" + sessionId + "/tokens";
		
		String patientsPath = "patients/";
		
		// Generate tokenData
		JSONObject resultFields = TestUtilities.buildJSONObject("vorname", "Max", "nachname", "Muster", "geburtstag", "27", "geburtsmonat", "05", "geburtsjahr", "1996");
		JSONArray idTypes = TestUtilities.buildJSONArray("psn");
		JSONObject tokenData = TestUtilities.createTokenDataAddPatient(idTypes, resultFields, null, null);
		
		// Add Dummy Patient for Testing
		TestUtilities.addDummyPatient(resource);
		
		// Generate Formula Data
		Form formData = TestUtilities.createForm("Peter", "Bauer", "Hans", "01", "01", "2000", "Mainz", "55120");
		
		// Call without token
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), null, null)
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient without token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Call with invalid (non-existing) token
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), "invalid", null)
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with non-existing token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Call with readToken not addToken
		String tokenId = TestUtilities.createTokenIdEditPatient(resource, tokenPath, TestUtilities.buildJSONObject("idType", "psn", "idString", "1"));
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, null)
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with wrong token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Generate tokenId just one time because status 400 and 409 do not invalidate the tokenId
		tokenId = TestUtilities.createTokenIdAddPatient(resource, tokenPath, "psn");
		
		// Add Patient without a field with a loop one by one
		String[] keyArray = {"vorname", "nachname", "geburtsname", "geburtstag", "geburtsmonat", "geburtsjahr", "ort", "plz"};
		for (int i = 0; i < keyArray.length; i++) {
			String[] valueArray = {"Peter", "Bauer", "Hans", "01", "01", "2000", "Mainz", "55120"};
			valueArray[i] = null;
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], valueArray[2], valueArray[3], valueArray[4], valueArray[5], valueArray[6], valueArray[7]);
			response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient without field '" + keyArray[i] + "' did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		}
		
		// Add Patient without a required field with a loop one by one
		for (int i = 0; i < 5; i++) {
			String[] valueArray = {"Peter", "Bauer", "01", "01", "2000"};
			valueArray[i] = null;
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], "Hans", valueArray[2], valueArray[3], valueArray[4], "Mainz", "55120");
			response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient without required field did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		}
		
		// Add Patient with empty required field with a loop one by one
		for (int i = 0; i < 5; i++) {
			String[] valueArray = {"Peter", "Bauer", "01", "01", "2000"};
			valueArray[i] = "";
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], "Hans", valueArray[2], valueArray[3], valueArray[4], "Mainz", "55120");
			response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient with empty required field did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		}

		// Add Patient with wrong birth date
		formData = TestUtilities.createForm("Peter", "Bauer", "Hans", "29", "02", "2001", "Mainz", "55120");
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with wrong birth date did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		// Add Patient with birth date in the future
		formData = TestUtilities.createForm("Peter", "Bauer", "Hans", "01", "01", "2050", "Mainz", "55120");
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with birth date in the future did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());

		// Add Patient with a empty Field
		formData = TestUtilities.createForm("Peter", "Bauer", "Hans", "01", "01", "2000", "Mainz", "");
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Adding Patient without a not required field(plz) did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());

		// Add Patient with an unsafe match
		tokenId = TestUtilities.createTokenIdAddPatient(resource, tokenPath, "psn");		
		formData = TestUtilities.createForm("Peter", "Brauer", "Hanz", "01", "01", "2000", "Mainz", "");
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Adding Patient with a little change did not return 409 status. Message from server: " + response.getEntity(String.class), 409, response.getStatus());
		
		// Adding Patient with predefined Values
		response = TestUtilities.getBuilderTokenPost(resource, tokenPath, TestUtilities.getApikey()).post(ClientResponse.class, tokenData);
		tokenId = TestUtilities.getTokenIdOfJSON(response.getEntity(JSONObject.class));
		formData = TestUtilities.createForm(null, null, "", null, null, null, "", "");
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Adding Patient with predefined values did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());
		
		// Add Patient without a field with a loop one by one
		for (int i = 0; i < 3; i++) {
			tokenId = TestUtilities.createTokenIdAddPatient(resource, tokenPath, "psn");
			String[] valueArray = {"Hans", "Mainz", "55120"};
			valueArray[i] = "";
			formData = TestUtilities.createForm("Peter", "Bauer", valueArray[0], "01", "01", "2000", valueArray[1], valueArray[2]);
			response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient with empty field did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());
		}
		
		// TODO: sureness true testen
	}
}
