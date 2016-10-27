package de.pseudonymisierung.mainzelliste.test;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

import java.net.URI;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

public class SessionsTest extends JerseyTest{
	
	public SessionsTest() {
		super(TestUtilities.setUpTest());
	}
	
	@Test
	public void testSessionsResource() {
		WebResource resource = resource();
		ClientResponse response;
		JSONObject entity;
		
		// Call without mainzellisteApiKey
		response = TestUtilities.getBuilderSession(resource, null, null).post(ClientResponse.class);
		assertEquals("Creating session without mainzellisteApiKey did not return 401 status.", 401, response.getStatus());

		// Call with wrong mainzellisteApiKey
		response = TestUtilities.getBuilderSession(resource, null, "wrongKey").post(ClientResponse.class);
		assertEquals("Creating session with wrong mainzellisteApiKey did not return 401 status.", 401, response.getStatus());

		// Create session
		response = TestUtilities.getBuilderSession(resource, null, TestUtilities.getApikey()).post(ClientResponse.class);
		assertEquals("Creating session did not return 201 status.", 201, response.getStatus());
		
		entity = response.getEntity(JSONObject.class);
		checkSessionValid(entity, null, "Create session:");
		
		String sessionId = TestUtilities.getSessionIdOfJSON(entity);
		URI sessionUri = TestUtilities.getSessionUriOfJSON(entity);
		
		// Read session
		response = TestUtilities.getBuilderSession(resource, sessionUri, TestUtilities.getApikey()).get(ClientResponse.class);		
		assertEquals("Reading session did not return 200 status.", 200, response.getStatus());
		
		entity = response.getEntity(JSONObject.class);
		checkSessionValid(entity, sessionId, "Read session: ");

		// Delete session
		response = resource.uri(sessionUri).delete(ClientResponse.class);
		assertEquals("Deleting session did not return 204 status.", 204, response.getStatus());
		
		// Try to access deleted session
		response = TestUtilities.getBuilderSession(resource, sessionUri, TestUtilities.getApikey()).get(ClientResponse.class);
		assertEquals("Reading deleted session did not return 404 status.", 404, response.getStatus());
		
		// Deleting a non-existent session
		response = resource.uri(sessionUri).delete(ClientResponse.class);
		assertEquals("Deleting non-existent session did not return 204 status.", 204, response.getStatus());
		
		// Check session timeout
		entity = TestUtilities.getBuilderSession(resource, null, TestUtilities.getApikey()).post(JSONObject.class);
		
		sessionUri = TestUtilities.getSessionUriOfJSON(entity);
		// Wait for two minutes for session to expire (one minute session
		// timeout + one minute delay between invocations of session cleanup
		TestUtilities.sleep(120000);
		
		// Read after waiting
		response = TestUtilities.getBuilderSession(resource, sessionUri, TestUtilities.getApikey()).get(ClientResponse.class);
		assertEquals("Read after timed-out session did not return 404 status.", 404, response.getStatus());
	}

	private void checkSessionValid(JSONObject sessionObj, String sessionId, String message) {
		assertTrue(message + " No id in session object", sessionObj.has("sessionId"));
		assertTrue(message + " No uri in session object", sessionObj.has("uri"));
		
		try {
			// Check URI
			String sessionIdFromJson = sessionObj.getString("sessionId");
			String uri = sessionObj.getString("uri");
			assertEquals("Wrong session URI", resource().path("sessions/").path(sessionIdFromJson + "/").getURI().toString(),
					uri);
			if (sessionId != null)
				assertEquals(message + " Unexpected id in session object", sessionId, sessionObj.get("sessionId").toString());
		} catch (JSONException e) {
			fail(message + " Retreiving id from session object failed: " + e);
		}
	}
}
