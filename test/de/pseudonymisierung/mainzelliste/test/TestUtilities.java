package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.WebAppDescriptor;

import de.pseudonymisierung.mainzelliste.Initializer;

public class TestUtilities {
	private static final String apiKey = "mdat1234";
	private static final String apiVersion = "2.0";
	
	public static Builder getBuilder(WebResource resource, URI resourceUri, String mediaType, String apiKey) {
		if (resource != null) {

			if (resourceUri != null) {
				resource = resource.uri(resourceUri);
			}
			
			Builder builder = resource.getRequestBuilder();
			
			if (mediaType != null) {
				builder = builder.accept(MediaType.APPLICATION_JSON);
			}
			
			if (apiKey != null) {
				builder = builder.header("mainzellisteApiKey", apiKey);
			}
			
			// Die neuste ApiVersion testen
			builder = builder.header("mainzellisteApiVersion", apiVersion);
			
			return builder;
		}
		
		return null;
	}
	
	public static String getStringOfJSON(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			fail("'"+ key + "' does not exist");
			return null;
		}
	}
	
	public static URI getUriOfJSON(JSONObject jsonObject, String key) {
		try {
			try {
				return new URI(jsonObject.getString(key));
			} catch (URISyntaxException e) {
				fail("'"+ key + "' is not a URI");
				return null;
			}
		} catch (JSONException e) {
			fail("'"+ key + "' does not exist");
			return null;
		}
	}
	
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static AppDescriptor getAppDescriptor() {
		return new WebAppDescriptor.Builder("de.pseudonymisierung.mainzelliste.webservice")
				.contextParam("de.pseudonymisierung.mainzelliste.ConfigurationFile", "/mainzelliste.conf.test")
				.contextPath("/mainzelliste")
				.contextListenerClass(Initializer.class)		
				.build();
	}

	public static String getApikey() {
		return apiKey;
	}

	public static String getApiversion() {
		return apiVersion;
	}
}
