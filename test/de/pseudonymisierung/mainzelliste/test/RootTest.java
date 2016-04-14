package de.pseudonymisierung.mainzelliste.test;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

import de.pseudonymisierung.mainzelliste.Config;

public class RootTest extends JerseyTest{

	public RootTest() {
		super(TestUtilities.setUpTest());
	}
	
	@Test
	public void testRootResource() {
		WebResource webResource = resource();
		
		ClientResponse response = webResource.path("/").get(ClientResponse.class);
		assertEquals("Wrong status code", 200, response.getStatus());
		// FIXME: Mit Jersey-Test wird Filter nicht angewendet
//		List<String> serverHeaders = response.getHeaders().get("Server");
//		assertTrue("Expected server not found in response, returned headers: " + serverHeaders.toString(), serverHeaders.contains("Mainzelliste/" + Config.instance.getVersion()));
	}
}
