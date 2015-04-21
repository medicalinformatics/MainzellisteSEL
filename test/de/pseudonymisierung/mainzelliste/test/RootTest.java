package de.pseudonymisierung.mainzelliste.test;

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
	
	public void testRootResource() {
		WebResource webResource = resource();
		String responseMsg = webResource.path("/").get(String.class);
		System.out.println(responseMsg);
	}
}
