package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.WebAppDescriptor;

import de.pseudonymisierung.mainzelliste.Initializer;

public class TestUtilities {
	
	// Visible outside
	private static final String apiKey = "mdat1234";
	private static final String sessionPath = "/sessions";
	
	// Invisible outside
	private static final String apiVersion = "2.0";
	private static final String sessionUriKey = "uri";
	private static final String sessionIdKey = "sessionId";
	private static final String tokenIdKey = "id";
	
	
	// --- BUILDER METHODS --- 
	public static Builder getBuilderPatient(WebResource resource, String tokenId, String apiKey) {
		return getBuilder(resource, tokenId, null, MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, apiKey);
	}
	
	public static Builder getBuilderSession(WebResource resource, String apiKey) {
		return getBuilder(resource, null, null, MediaType.APPLICATION_JSON, null, apiKey);
	}
	
	public static Builder getBuilderTokenPost(WebResource resource, String path, String apiKey) {
		return getBuilder(resource, null, path, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiKey);
	}
	
	public static Builder getBuilderTokenGet(WebResource resource, String path, String apiKey) {
		return getBuilder(resource, null, path, MediaType.APPLICATION_JSON, null, apiKey);
	}
	
	public static Builder getBuilderDelete(WebResource resource) {
		return getBuilder(resource, null, null, null, null, null);
	}
	
	private static Builder getBuilder(WebResource resource, String tokenId, String path, String acceptType, String contentType, String apiKey) {
		if (resource != null) {

			if (path != null) {
				resource = resource.path(path);
			}
			
			if (tokenId != null) {
				resource = resource.queryParam("tokenId", tokenId);
			}
			
			Builder builder = resource.getRequestBuilder();
			
			if (acceptType != null) {
				builder = builder.accept(acceptType);
			}
			
			if (contentType != null) {
				builder = builder.type(contentType);
			}
			
			if (apiKey != null) {
				builder = builder.header("mainzellisteApiKey", apiKey);
			}
			
			// Test the latest ApiVersions
			builder = builder.header("mainzellisteApiVersion", apiVersion);
			
			return builder;
		}
		
		return null;
	}
	
	
	// --- CREATE METHODS ---
	
	// - Create TokenId Methods -
	public static String createTokenIdAddPatient(WebResource resource, String tokenRequestPath, String... idTypes) {
		JSONObject tokenData = createTokenDataAddPatient(buildJSONArray("intid"), null, null, null);
		ClientResponse response = getBuilderTokenPost(resource, tokenRequestPath, apiKey)
				.post(ClientResponse.class, tokenData);
		
		return getTokenIdOfJSON(response.getEntity(JSONObject.class));
	}

	public static String createTokenIdReadPatient(WebResource resource, String tokenRequestPath, JSONArray resultFields, JSONArray resultIds, JSONObject... searchIds) {
		JSONObject tokenData = createTokenDataReadPatient(resultFields, resultIds, searchIds);
		ClientResponse response = getBuilderTokenPost(resource, tokenRequestPath, apiKey)
				.post(ClientResponse.class, tokenData);
		
		return getTokenIdOfJSON(response.getEntity(JSONObject.class));
	}

	public static String createTokenIdEditPatient(WebResource resource, String tokenRequestPath, JSONObject patientId) {
		JSONObject tokenData = createTokenDataEditPatient(patientId);
		ClientResponse response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, apiKey)
				.post(ClientResponse.class, tokenData);
		
		return TestUtilities.getTokenIdOfJSON(response.getEntity(JSONObject.class));
	}
	
	// - Create TokenData Methods -
	public static JSONObject createTokenDataAddPatient(JSONArray idtypes, JSONObject fields, String callback, String redirect) {
		JSONObject tokenData = new JSONObject();
		
		try {
			
			tokenData.put("type", "addPatient");
			
			JSONObject data = new JSONObject();
			
			if (idtypes != null) {
				data.put("idtypes", idtypes);
			}
			
			if (fields != null) {
				data.put("fields", fields);
			}
			
			if (callback != null) {
				data.put("callback", callback);
			}
			
			if (redirect != null) {
				data.put("redirect", redirect);
			}
				
			tokenData.put("data", data);
			
		} catch (JSONException e) {
			fail("JSON Key should not be null");
		}
		
		return tokenData;
	}
	
	public static JSONObject createTokenDataReadPatient(JSONArray resultFields, JSONArray resultIds, JSONObject... searchIds) {
		JSONObject tokenData = new JSONObject();
		
		try {
			
			tokenData.put("type", "readPatients");
			
			JSONObject data = new JSONObject();
			
			if (searchIds.length > 0) {
				data.put("searchIds", buildJSONArray(searchIds));
			}
			
			if (resultFields != null) {
				data.put("resultFields", resultFields);
			}
			
			if (resultIds != null) {
				data.put("resultIds", resultIds);
			}
			
			tokenData.put("data", data);
			
		} catch (JSONException e) {
			fail("JSON Key should not be null");
		}
		
		return tokenData;
	}
	
	public static JSONObject createTokenDataEditPatient(JSONObject patientId) {
		JSONObject tokenData = new JSONObject();
		
		try {
			
			tokenData.put("type", "editPatient");
			
			JSONObject data = new JSONObject();
			
			if (patientId != null) {
				data.put("patientId", patientId);
			}
			
			tokenData.put("data", data);
			
		} catch (JSONException e) {
			fail("JSON Key should not be null");
		}
		
		return tokenData;
	}

	// - Create PatientData Methods -
	public static Form createForm(String vorname, String nachname,	String geburtsname, String geburtstag, String geburtsmonat, String geburtsjahr, String ort, String plz) {
		Form form = new Form();
				
		if (vorname != null) {
			form.add("vorname", vorname);
		}
		
		if (nachname != null) {
			form.add("nachname", nachname);
		}

		if (geburtsname != null) {
			form.add("geburtsname", geburtsname);
		}
		
		if (geburtstag != null) {
			form.add("geburtstag", geburtstag);
		}
		
		if (geburtsmonat != null) {
			form.add("geburtsmonat", geburtsmonat);
		}
				
		if (geburtsjahr != null) {
			form.add("geburtsjahr", geburtsjahr);
		}

		if (ort != null) {
			form.add("ort", ort);
		}
		
		if (plz != null) {
			form.add("plz", plz);
		}
		
		return form;
	}
	
	public static String createSession(WebResource resource) {
		ClientResponse response = getBuilderSession(resource.path(sessionPath), apiKey).post(ClientResponse.class);
		
		JSONObject entity = response.getEntity(JSONObject.class);
		
		return getSessionIdOfJSON(entity);
	}
	
	private static URI createUri(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			fail("'"+ uri + "' is not a URI");
			return null;
		}
	}
	
	
	// --- JSON METHODS ---
	
	// - Get Methods -
	public static String getTokenIdOfJSON(JSONObject jsonObject) {
		return getStringOfJSON(jsonObject, tokenIdKey);
	}
	
	public static String getSessionIdOfJSON(JSONObject jsonObject) {
		return getStringOfJSON(jsonObject, sessionIdKey);
	}
	
	public static URI getSessionUriOfJSON(JSONObject jsonObject) {
		return createUri(getStringOfJSON(jsonObject, sessionUriKey));
	}
	
	private static String getStringOfJSON(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			fail("'"+ key + "' does not exist");
			return null;
		}
	}
	
	// - Build Methods -
	public static JSONObject buildJSONObject(String... strings) {
		if (strings != null && strings.length > 0) {
			JSONObject jsonObject = new JSONObject();
			for (int i = 0; i+1 < strings.length; i++) {
				try {
					jsonObject.put(strings[i], strings[++i]);
				} catch (JSONException e) {
					fail("Key should not be null");
				}
			}
			return jsonObject;
		} else {
			return new JSONObject();
		}
	}	
	
	public static <T extends Object> JSONArray buildJSONArray(T... objects) {
		if (objects != null && objects.length > 0) {
			return new JSONArray(Arrays.asList(objects));
		} else {
			return new JSONArray();
		}
	}
	
	
	// --- HELPER METHODS ---
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static AppDescriptor setUpTest() {
		return new WebAppDescriptor.Builder("de.pseudonymisierung.mainzelliste.webservice")
				.contextParam("de.pseudonymisierung.mainzelliste.ConfigurationFile", "/mainzelliste.conf.test")
				.contextPath("/mainzelliste")
				.contextListenerClass(Initializer.class)		
				.build();
	}

	
	// --- GETTER METHODS ---
	public static String getApikey() {
		return apiKey;
	}

	public static String getApiversion() {
		return apiVersion;
	}

	public static String getSessionpath() {
		return sessionPath;
	}
}
