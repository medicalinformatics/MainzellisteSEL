package de.pseudonymisierung.mainzelliste.test;

import javax.persistence.Temporal;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import static org.junit.Assert.*;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import de.pseudonymisierung.mainzelliste.Initializer;

public class SessionsTest extends JerseyTest{

	public SessionsTest() {
		super(new WebAppDescriptor.Builder("de.pseudonymisierung.mainzelliste.webservice")
		.contextParam("de.pseudonymisierung.mainzelliste.ConfigurationFile", "/mainzelliste.conf.test")
		.contextPath("/mainzelliste")
		.contextListenerClass(Initializer.class)		
		.build());
	}
	
	@Test
	public void testSessionsResource() {
		WebResource resource = resource().path("/sessions");
		ClientResponse response;
		// Testen von Zugriffen aus Sessions
		response = resource
				.accept(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class);
		assertEquals("Creating session without mainzellisteApiKey did not return 401 status.", 401, response.getStatus());
		
		// Call with wrong mainzellisteApiKey should fail
		response = resource
				.accept(MediaType.APPLICATION_JSON)
				.header("mainzellisteApiKey", "wrongKey")
				.post(ClientResponse.class);
		assertEquals("Creating session with wrong mainzellisteApiKey did not return 401 status.", 401, response.getStatus());

	}
}
