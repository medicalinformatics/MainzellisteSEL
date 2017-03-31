package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.net.httpserver.HttpExchange;

import de.pseudonymisierung.mainzelliste.Config;

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
	public void testAddPatientToken() {
		String sessionId = TestUtilities.createSession(resource);
		
		// Generate tokenData
		JSONObject resultFields = TestUtilities.buildJSONObject("vorname", "Max", "nachname", "Muster");
		JSONArray idTypes = TestUtilities.buildJSONArray("psn");
		String redirect = "https://example.org/index.php?token={tokenId}";
		JSONObject tokenData = TestUtilities.createTokenDataAddPatient(idTypes, null, null, null);
		
		// TODO: Anlegen mit falscher IP-Adresse → Erwarte 401 Unauthorized
		
		// Call without mainzellisteApiKey
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, null)
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token without mainzellisteApiKey did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		// Call with wrong mainzellisteApiKey
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, "wrongKey")
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with wrong mainzellisteApiKey did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		// Call with no existing session
		response = TestUtilities.getBuilderCreateToken(resource, "unbekannt", TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with non existing session did not return 404 status. Message from server: " + response.getEntity(String.class), 404, response.getStatus());
		
		// Request with not existing IdType
		tokenData = TestUtilities.createTokenDataAddPatient(TestUtilities.buildJSONArray("pid", "noPid"), null, null, redirect);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token for addPatient with wrong ID type did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		// Request with not existing field
		tokenData = TestUtilities.createTokenDataAddPatient(idTypes, TestUtilities.buildJSONObject("unbekannt", "unbekannt"), null, redirect);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token for addPatient with wrong Field did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		// Create Token for addPatient
		tokenData = TestUtilities.createTokenDataAddPatient(idTypes, resultFields, null, null);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		JSONObject jsonObject = response.getEntity(JSONObject.class);
		assertEquals("Creating token not return 201 status. Message from server: " + jsonObject, 201, response.getStatus());
		
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
	 * Test functionality of the add patient 
	 * @throws Exception result in test failure
	 */
	@Test
	public void testAddPatient() throws Exception {
		String sessionId = TestUtilities.createSession(resource);
		
		// Generate tokenData
		JSONObject resultFields = TestUtilities.buildJSONObject("vorname", "Max", "nachname", "Muster", "geburtstag", "27", "geburtsmonat", "05", "geburtsjahr", "1996");
		JSONArray idTypes = TestUtilities.buildJSONArray("psn");
		JSONObject tokenData = TestUtilities.createTokenDataAddPatient(idTypes, resultFields, null, null);
		
		// Add Dummy Patient for Testing
		TestUtilities.addDummyPatient(resource);

		// Add patient with external id
		Form formData = TestUtilities.createForm("TestPatientVorname", "TestPatientNachname", "Peter", "05", "01", "1990", "Mainz", "55120");
		formData.add("extid", "1234");

        String tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "psn");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
                .post(ClientResponse.class, formData);
        assertEquals("Add Patient did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());

		// Generate Formula Data
		formData = TestUtilities.createForm("AddPatientVorname", "AddPatientNachname", "Hans", "01", "01", "2000", "Mainz", "55120");
		
		// Call without token
		response = TestUtilities.getBuilderPatient(resource, null, null)
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient without token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Call with invalid (non-existing) token
		response = TestUtilities.getBuilderPatient(resource, "invalid", null)
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with non-existing token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Call with editToken not addToken
		tokenId = TestUtilities.createTokenIdEditPatient(resource, sessionId, TestUtilities.buildJSONObject("idType", "psn", "idString", "1"), null);
		response = TestUtilities.getBuilderPatient(resource, tokenId, null)
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with wrong token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Generate tokenId just one time because status 400 and 409 do not invalidate the tokenId
		tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "psn");
		
		// Add Patient without a field with a loop one by one
		String[] keyArray = {"vorname", "nachname", "geburtsname", "geburtstag", "geburtsmonat", "geburtsjahr", "ort", "plz"};
		for (int i = 0; i < keyArray.length; i++) {
			String[] valueArray = {"AddPatientVorname", "AddPatientNachname", "Hans", "01", "01", "2000", "Mainz", "55120"};
			valueArray[i] = null;
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], valueArray[2], valueArray[3], valueArray[4], valueArray[5], valueArray[6], valueArray[7]);
			response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient without field '" + keyArray[i] + "' did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		}
		
		// Add Patient without a required field with a loop one by one
		for (int i = 0; i < 5; i++) {
			String[] valueArray = {"AddPatientVorname", "AddPatientNachname", "01", "01", "2000"};
			valueArray[i] = null;
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], "Hans", valueArray[2], valueArray[3], valueArray[4], "Mainz", "55120");
			response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient without required field did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		}
		
		// Add Patient with empty required field with a loop one by one
		for (int i = 0; i < 5; i++) {
			String[] valueArray = {"AddPatientVorname", "AddPatientNachname", "01", "01", "2000"};
			valueArray[i] = "";
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], "Hans", valueArray[2], valueArray[3], valueArray[4], "Mainz", "55120");
			response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient with empty required field did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		}

		// Add Patient with wrong birth date
		formData = TestUtilities.createForm("AddPatientVorname", "AddPatientNachname", "Hans", "29", "02", "2001", "Mainz", "55120");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with wrong birth date did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());
		
		// Add Patient with birth date in the future
		formData = TestUtilities.createForm("AddPatientVorname", "AddPatientNachname", "Hans", "01", "01", "2050", "Mainz", "55120");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with birth date in the future did not return 400 status. Message from server: " + response.getEntity(String.class), 400, response.getStatus());

		// Add Patient with an empty IntegerField
		formData = TestUtilities.createForm("AddPatientVorname", "AddPatientNachname", "Hans", "01", "01", "2000", "Mainz", "");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Adding Patient without a not required field(plz) did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());

        // Add Patient with an empty PlainTextField
        tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "psn");        
        formData = TestUtilities.createForm("AddPatientVorname", "AddPatientNachname", "Hans", "01", "01", "2000", "", "55120");
        response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
                .post(ClientResponse.class, formData);
        assertEquals("Adding Patient without a not required field(ort) did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());

		// Add Patient with callback
		formData = TestUtilities.createForm("AddPatientVornameCallback", "AddPatientNachnameCallback", "Callback", "01", "01", "2000", "Mainz", "55120");
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		
		Client client = Client.create(clientConfig);
		WebResource resourceHttpReceiv = client.resource(resource.getURI());
		tokenData = TestUtilities.createTokenDataAddPatient(idTypes, null, String.format("http://localhost:%d%s", callbackPort, callbackRessource), null);
		tokenId = TestUtilities.createTokenId(resourceHttpReceiv, sessionId, tokenData);
		
		HttpReceiver receiver = new HttpReceiver(callbackPort, callbackRessource);
		receiver.start();
		TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
				.post(formData);
		receiver.stop();
		
		HttpExchange callback = receiver.getReceivedRequest();
		assertEquals("Wrong user agent in Mainzelliste request.", "Mainzelliste/" + Config.instance.getVersion(), callback.getRequestHeaders().getFirst("User-Agent"));
		assertEquals("Wrong HTTP method in callback.", "POST", callback.getRequestMethod());
		
		JSONObject callbackObject = new JSONObject(receiver.getReceivedEntity());
		assertTrue("No Token-ID in callback", callbackObject.has("tokenId"));
		assertEquals("Wrong Token-ID in callback", tokenId, callbackObject.getString("tokenId"));
		assertTrue("No IDs in callback", callbackObject.has("ids"));
		JSONArray callbackIds = callbackObject.getJSONArray("ids");
		assertTrue("No IdType in callback", callbackIds.getJSONObject(0).has("idType"));
		assertEquals("Wrong IdType in callback", idTypes.getString(0), callbackIds.getJSONObject(0).getString("idType"));
		assertTrue("No IdString in callback", callbackIds.getJSONObject(0).has("idString"));
		
		// TODO: Request with redirect and with non existing IdType -> 400 Bad Request
		
		// TODO: Request with redirect check if id's (idType, IdString) and tokenId is right -> 201 Create

		// Adding Patient with predefined Values
		tokenData = TestUtilities.createTokenDataAddPatient(idTypes, TestUtilities.createJSONForm("Add", "Predefined", null, "01", "01", "2000", null, null), null, null);
		response = TestUtilities.getBuilderCreateToken(resource, sessionId, TestUtilities.getApikey()).post(ClientResponse.class, tokenData);
		tokenId = TestUtilities.getTokenIdOfJSON(response.getEntity(JSONObject.class));
		formData = TestUtilities.createForm(null, null, "", null, null, null, "", "");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Adding Patient with predefined values did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());
		
		// Add Patient without a field with a loop one by one
		for (int i = 0; i < 3; i++) {
			tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "psn");
			String[] valueArray = {"Hans", "Mainz", "55120"};
			valueArray[i] = "";
			formData = TestUtilities.createForm("AddPatientVorname" + (char)(65+i), "AddPatientNachname" + (char)(65+i), valueArray[0], "01", "01", "2000", valueArray[1], valueArray[2]);
			response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient with empty field did not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());
		}
	}
}
