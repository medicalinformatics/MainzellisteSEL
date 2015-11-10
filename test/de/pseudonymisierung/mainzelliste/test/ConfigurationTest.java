package de.pseudonymisierung.mainzelliste.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.*;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

/**
 * Test cases for reading configuration parameters (/configuration).
 */
public class ConfigurationTest extends JerseyTest {

	public ConfigurationTest() {
		super(TestUtilities.setUpTest());
	}

	@Test
	public void testConfigurationResource() {
		WebResource webResource = resource().path("/configuration/fieldKeys");
		ClientResponse response;

		// Call without mainzellisteApiKey
		response = webResource.get(ClientResponse.class);
		assertEquals("Reading field keys without mainzellisteApiKey did not return 401 status.", 401,
				response.getStatus());

		// Call with wrong mainzellisteApiKey
		response = webResource.header("mainzellisteApiKey", "wrongKey").get(ClientResponse.class);
		assertEquals("Creating session with wrong mainzellisteApiKey did not return 401 status.", 401,
				response.getStatus());

		// Call with right key and check result
		response = webResource.header("mainzellisteApiKey", TestUtilities.getApikey()).get(ClientResponse.class);
		assertEquals("Reading field keys did not return 200 status.", 200, response.getStatus());
		JSONArray result = response.getEntity(JSONArray.class);
		Set<String> expectedFieldKeys = new HashSet<String>(Arrays.asList(TestUtilities.getPatientKeys()));
		try {
			for (int i = 0; i < result.length(); i++) {
				String thisFieldKey = result.getString(i);
				if (!expectedFieldKeys.contains(thisFieldKey))
					fail("Field name " + thisFieldKey + " was returned by the server but is not a configured field.");
				expectedFieldKeys.remove(thisFieldKey);
			}
			if (expectedFieldKeys.size() > 0)
				fail("Some field keys were not returned by the server: " + expectedFieldKeys);
		} catch (JSONException e) {
			fail("JSONException while reading field keys: " + e.getMessage());
		}
	}
}
