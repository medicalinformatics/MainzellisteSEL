package de.pseudonymisierung.mainzelliste.test;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

import java.net.URI;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

public class SessionsTest extends JerseyTest{

	private final String sessionPath = "/sessions/";
	
	public SessionsTest() {
		super(TestUtilities.getAppDescriptor());
	}
	
	@Test
	public void testSessionsResource() {
		WebResource resource = resource().path(sessionPath);
		ClientResponse response;
		JSONObject entity;
		
		// Call without mainzellisteApiKey should fail
		response = TestUtilities.getBuilder(resource, null, MediaType.APPLICATION_JSON, null).post(ClientResponse.class);
		assertEquals("Creating session without mainzellisteApiKey did not return 401 status.", 401, response.getStatus());

		// Call with wrong mainzellisteApiKey should fail
		response = TestUtilities.getBuilder(resource, null, MediaType.APPLICATION_JSON, "wrongKey").post(ClientResponse.class);
		assertEquals("Creating session with wrong mainzellisteApiKey did not return 401 status.", 401, response.getStatus());

		// Create session
		response = TestUtilities.getBuilder(resource, null, MediaType.APPLICATION_JSON, TestUtilities.getApikey()).post(ClientResponse.class);
		assertEquals("Creating session did not return 201 status.", 201, response.getStatus());
		
		entity = response.getEntity(JSONObject.class);
		checkSessionValid(entity, null, "Create session:");
		
		String sessionId = TestUtilities.getStringOfJSON(entity, "sessionId");
		URI sessionUri = TestUtilities.getUriOfJSON(entity, "uri");
		
		// Read the created session
		response = TestUtilities.getBuilder(resource, sessionUri, MediaType.APPLICATION_JSON, TestUtilities.getApikey()).get(ClientResponse.class);
		assertEquals("Reading session did not return 200 status.", 200, response.getStatus());
		
		entity = response.getEntity(JSONObject.class);
		checkSessionValid(entity, sessionId, "Read session: ");

		// Delete session
		response = TestUtilities.getBuilder(resource, sessionUri, null, null).delete(ClientResponse.class);
		assertEquals("Deleting session did not return 204 status.", 204, response.getStatus());
		
		// Try to access deleted session
		response = TestUtilities.getBuilder(resource, sessionUri, MediaType.APPLICATION_JSON, TestUtilities.getApikey()).get(ClientResponse.class);
		assertEquals("Reading deleted session did not return 404 status.", 404, response.getStatus());
		
		// Deleting a non-existent session should be Ok
		response = TestUtilities.getBuilder(resource, sessionUri, null, TestUtilities.getApikey()).delete(ClientResponse.class);
		assertEquals("Deleting non-existent session did not return 204 status.", 204, response.getStatus());
		
		// Check session timeout by creating a session and trying to read after waiting
		entity = TestUtilities.getBuilder(resource, null, MediaType.APPLICATION_JSON, TestUtilities.getApikey()).post(JSONObject.class);
		
		sessionUri = TestUtilities.getUriOfJSON(entity, "uri");
		
		TestUtilities.sleep(120000);
		
		// Read after waiting
		response = TestUtilities.getBuilder(resource, sessionUri, MediaType.APPLICATION_JSON, TestUtilities.getApikey()).get(ClientResponse.class);
		assertEquals("Unexpected status (!=404) on trying to read timed-out session.", 404, response.getStatus());
	}

	private void checkSessionValid(JSONObject sessionObj, String sessionId, String message) {
		assertTrue(message + " No id in session object", sessionObj.has("sessionId"));
		assertTrue(message + " No uri in session object", sessionObj.has("uri"));
		try {
			if (sessionId != null)
				assertEquals(message + " Unexpected id in session object", sessionId, sessionObj.get("sessionId").toString());
		} catch (JSONException e) {
			fail(message + " Retreiving id from session object failed: " + e);
		}
	}
}
