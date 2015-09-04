package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.test.framework.JerseyTest;

public class AddPatientTest extends JerseyTest {
	private WebResource resource;
	private ClientResponse response;
	
	public AddPatientTest() {
		super(TestUtilities.setUpTest());
	}
	
	@Before
	public void setUp() {
		resource = resource();
	}
	
	@Test
	public void testAddPatientToken() {
		String sessionId = TestUtilities.createSession(resource);
		String tokenRequestPath = "sessions/" + sessionId + "/tokens";
		
		// Generate tokenData
		JSONObject resultFields = TestUtilities.buildJSONObject("vorname", "Max", "nachname", "Muster");
		JSONArray idTypes = TestUtilities.buildJSONArray("intid");
		String redirect = "https://example.org/index.php?token={tokenId}";
		JSONObject tokenData = TestUtilities.createTokenDataAddPatient(idTypes, null, null, null);
		
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
		
		// TODO: Anlegen mit falschem Format → 400 Bad Request
		
		// Create Token for addPatient
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token not return 201 status.", 201, response.getStatus());
		// TODO: Rückgabe des Tokens Testen
		
		// Request with not existing IdType
		tokenData = TestUtilities.createTokenDataAddPatient(TestUtilities.buildJSONArray("pid", "noPid"), resultFields, null, redirect);
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token for addPatient with wrong ID type did not return 400 status.", 400, response.getStatus());
		
		// Request with not existing field
		tokenData = TestUtilities.createTokenDataAddPatient(idTypes, TestUtilities.buildJSONObject("unbekannt", "unbekannt"), null, redirect);
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token for addPatient with wrong Field did not return 400 status.", 400, response.getStatus());
		
		// TODO: Request with callback check if ids (idType, IdString) and tokenId is right -> 201 Create
		
		// TODO: Request with redirect and with non existing IdType -> 400 Bad Request
		
		// TODO: Request with redirect check if ids (idType, IdString) and tokenId is right -> 201 Create
		
		
		// TODO: Token abrufen (GET /sessions/{sid}/tokens/{tid} (auch in edit und read)
		
		// TODO: Token löschen (DELETE /sessions/{sid}/tokens/{tid}) (auch in edit und read)
	}
	
	@Test
	public void testAddPatient() {
		String sessionId = TestUtilities.createSession(resource);
		String tokenPath = "sessions/" + sessionId + "/tokens";
		
		String patientsPath = "patients/";
		
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

		// TODO: ungültiges Token 401 (erneut verwenden)
		// TODO: falsches Token 401 (von readPatien z.B.)

		// Generate tokenId just one time because status 400 and 409 do not invalidate the tokenId
		String tokenId = TestUtilities.createTokenIdAddPatient(resource, tokenPath, "intid");
		
		// Add Patient without a field with a loop one by one
		String[] keyArray = {"vorname", "nachname", "geburtsname", "geburtstag", "geburtsmonat", "geburtsjahr", "ort", "plz"};
		for (int i = 0; i < keyArray.length; i++) {
			String[] valueArray = {"Peter", "Bauer", "Hans", "1", "1", "2000", "Mainz", "55120"};
			valueArray[i] = null;
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], valueArray[2], valueArray[3], valueArray[4], valueArray[5], valueArray[6], valueArray[7]);
			response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient without field" + keyArray[i] + "did not return 400 status.", 400, response.getStatus());
		}

		// Add Patient with empty required field with a loop one by one
		for (int i = 0; i < 5; i++) {
			String[] valueArray = {"Peter", "Bauer", "1", "1", "2000"};
			valueArray[i] = "";
			formData = TestUtilities.createForm(valueArray[0], valueArray[1], "Hans", valueArray[2], valueArray[3], valueArray[4], "Mainz", "55120");
			response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
					.post(ClientResponse.class, formData);
			assertEquals("Add Patient with empty required field did not return 400 status.", 400, response.getStatus());
		}

		// Add Patient with wrong birth date
		formData = TestUtilities.createForm("Peter", "Bauer", "Hans", "29", "2", "2000", "Mainz", "55120");
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with wrong birth date did not return 400 status.", 400, response.getStatus());
		
		// Add Patient with birth date in the future
		formData = TestUtilities.createForm("Peter", "Bauer", "Hans", "1", "1", "2050", "Mainz", "55120");
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		assertEquals("Add Patient with birth date in the future did not return 400 status.", 400, response.getStatus());
		
		// TODO: Feld leer 201
//		formData = TestUtilities.getForm("Peter", "Bauer", "Hans", "1", "1", "2000", "Mainz", null);
//		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), tokenId, TestUtilities.getApikey())
//				.post(ClientResponse.class, formData);
//		assertEquals("Adding Patient without a not required field(plz) did not return 400 status.", 400, response.getStatus());
		
		// TODO: Felder durch Token vordefinieren 201
		// TODO: bei 400 und 409 token wiederverwenden da noch gültig
		// TODO: ein unsicheren Match generieren 409
		// TODO: prüfen ob sureness by default false ist
		// TODO: surness true testen
	}
}
