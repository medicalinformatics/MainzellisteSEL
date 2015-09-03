package de.pseudonymisierung.mainzelliste.test;

import org.junit.Test;

import static org.junit.Assert.*;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

public class RootTest extends JerseyTest{

	public RootTest() {
		super(TestUtilities.setUpTest());
	}
	
	@Test
	public void testRootResource() {
		WebResource webResource = resource();
		
		ClientResponse response = webResource.path("/").get(ClientResponse.class);
		assertEquals("Wrong status code", 200, response.getStatus());
	}
}
