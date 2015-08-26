package de.pseudonymisierung.mainzelliste.test;

import javax.persistence.Temporal;
import javax.ws.rs.core.Response;

import org.junit.Test;

import static org.junit.Assert.*;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import de.pseudonymisierung.mainzelliste.Initializer;

public class RootTest extends JerseyTest{

	public RootTest() {
		super(new WebAppDescriptor.Builder("de.pseudonymisierung.mainzelliste.webservice")
		.contextParam("de.pseudonymisierung.mainzelliste.ConfigurationFile", "/mainzelliste.conf.test")
		.contextPath("/mainzelliste")
		.contextListenerClass(Initializer.class)		
		.build());
	}
	
	@Test
	public void testRootResource() {
		WebResource webResource = resource();
		ClientResponse response = webResource.path("/").get(ClientResponse.class);
		assertEquals("Wrong status code", 200, response.getStatus());
	}
}
