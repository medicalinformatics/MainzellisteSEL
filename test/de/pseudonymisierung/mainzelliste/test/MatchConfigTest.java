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

/**
 * Test default matcher configuration with typical cases.
 */
@SuppressWarnings("restriction")
public class MatchConfigTest extends JerseyTest {
	
	private WebResource resource;
	private ClientResponse response;

	public MatchConfigTest() {
		super(TestUtilities.setUpTest());
	}

	@Before
	public void setUp() {
		resource = resource();
	}

	/**
	 * Test implementation.
	 */
	@Test
	public void testAddPatient() throws Exception {
		String sessionId = TestUtilities.createSession(resource);

		// Add test patient for matching
		Form formData = TestUtilities.createForm("Max", "Mustermann", "", "01", "01", "2000", "Wiesbaden", "65195");
		String tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "pid");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).post(
				ClientResponse.class, formData);
		int statusCode = response.getStatus();
		String entity = response.getEntity(String.class);
		assertEquals("Adding test patient for matching failed with status " + statusCode + " and message " + entity,
				201, statusCode);

		// Extract generated ID
		String testPatientId = getIdStringFromJson(entity, "pid");

		// Slight typographical error should be corrected
		formData = TestUtilities.createForm("Max", "Musterman", "", "01", "01", "2000", "Wiesbaden", "65195");
		tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "pid");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).post(
				ClientResponse.class, formData);
		statusCode = response.getStatus();
		entity = response.getEntity(String.class);
		assertEquals("Adding patient with slight typographical error returned unexpected status code.", 201, statusCode);
		String newPatientId = getIdStringFromJson(entity, "pid");
		assertEquals("Adding patient with slight typographical error returned new ID", testPatientId, newPatientId);

		// Different date of birth should lead to conflict
		// Different day
		formData = TestUtilities.createForm("Max", "Mustermann", "", "02", "01", "2000", "Wiesbaden", "65195");
		tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "pid");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).post(
				ClientResponse.class, formData);
		statusCode = response.getStatus();
		assertEquals("Adding patient with different day of birth returned unexpected status code.", 409, statusCode);

		// Different month
		formData = TestUtilities.createForm("Max", "Mustermann", "", "01", "02", "2000", "Wiesbaden", "65195");
		tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "pid");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).post(
				ClientResponse.class, formData);
		statusCode = response.getStatus();
		assertEquals("Adding patient with different month of birth returned unexpected status code.", 409, statusCode);

		// Different year
		formData = TestUtilities.createForm("Max", "Mustermann", "", "01", "01", "1950", "Wiesbaden", "65195");
		tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "pid");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).post(
				ClientResponse.class, formData);
		statusCode = response.getStatus();
		assertEquals("Adding patient with different year of birth returned unexpected status code.", 409, statusCode);
		
		// Different postal code should be unsure match
		formData = TestUtilities.createForm("Max", "Mustermann", "", "01", "01", "2000", "Wiesbaden", "65197");
        formData.add("sureness", true);

        tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "pid");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).post(
				ClientResponse.class, formData);
		statusCode = response.getStatus();
		assertEquals("Adding patient with different postal code returned unexpected status code.", 201, statusCode);

		// Different city should lead to unsure match
		formData = TestUtilities.createForm("Max", "Mustermann", "", "01", "01", "2000", "Horath", "54497");
        formData.add("sureness", true);
		tokenId = TestUtilities.createTokenIdAddPatient(resource, sessionId, "pid");
		response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).post(
				ClientResponse.class, formData);
		statusCode = response.getStatus();
		entity = response.getEntity(String.class);
		assertEquals("Adding patient with different city returned unexpected status code.", 201, statusCode);
	}

	/**
	 * Extract ID from return value of POST /patients
	 * 
	 * @param json
	 *            JSON array of IDs as returned by POST /patients.
	 * @return ID of the given type, extracted from json.
	 */
	private String getIdStringFromJson(String json, String idType) throws Exception {
		JSONArray array = new JSONArray(json);
		for (int index = 0; index < array.length(); index++) {
			JSONObject thisIdObject = array.getJSONObject(index);
			if (idType.equals(thisIdObject.getString("idType")))
				return thisIdObject.getString("idString");
		}
		throw new Exception("ID type " + idType + " not found in JSON array.");
	}
}
