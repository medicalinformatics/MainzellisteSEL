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
	
	// --- Visible outside ---
	private static final String apiKey = "mdat1234";
	private static final String sessionPath = "/sessions";
	private static final String[] patientKeys = {"vorname", "nachname",	"geburtsname", "geburtstag", "geburtsmonat", "geburtsjahr", "ort", "plz"};
	
	// --- Invisible outside ---
	private static final String apiVersion = "2.0";
	private static final JSONArray resultIds = buildJSONArray("psn");
	private static final String tokenPath = "sessions/%s/tokens";
	private static final String patientsPath = "patients/";
	// Setup Paths
	private static final String packagePath = "de.pseudonymisierung.mainzelliste.webservice";
	private static final String configPackagePath = "de.pseudonymisierung.mainzelliste.ConfigurationFile";
	private static final String configFile = "/mainzelliste.conf.test";
	// Keys of JSON
	private static final String sessionUriKey = "uri";
	private static final String sessionIdKey = "sessionId";
	private static final String tokenIdKey = "id";
	
	
	// --- BUILDER METHODS ---
	
	/**
	 * Create an Web resource Builder for Patient interaction
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param tokenId One time authorization for Requests
	 * @param apiKey Authorize access on the mainzellist API 
	 * @return WebResource Builder 
	 */
	public static Builder getBuilderPatient(WebResource resource, String tokenId, String apiKey) {
		return getBuilder(resource.path(patientsPath), tokenId, null, MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, apiKey);
	}
	
	/**
	 * Create an Web resource Builder for Patient Edit interaction (<b>Is temporally needed because editing 
	 * an Patient could not be done with MediaType.APPLICATION_FORM_URLENCODED</b>)
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param tokenId One time authorization for Requests
	 * @param apiKey Authorize access on the mainzellist API 
	 * @return WebResource Builder 
	 */
	public static Builder getBuilderPatientEdit(WebResource resource, String tokenId, String apiKey) {
		WebResource editPatientResource = resource.path(patientsPath).path("tokenId");
		
		if (tokenId != null) {
			editPatientResource = editPatientResource.path(tokenId);
		}
		
		return getBuilder(editPatientResource, null, null, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiKey);
	}

	/**
	 * Create Web resource Builder for Session interaction
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param apiKey Authorize access on the mainzellist API
	 * @return WebResource Builder
	 */
	public static Builder getBuilderSession(WebResource resource, String apiKey) {
		return getBuilder(resource, null, null, MediaType.APPLICATION_JSON, null, apiKey);
	}
	
	/**
	 * Create Web resource Builder for creating a Token 
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param sessionId Should look like this sessions/{sessionId}/tokens
	 * @param apiKey Authorize access on the mainzellist API
	 * @return WebResource Builder
	 */
	public static Builder getBuilderCreateToken(WebResource resource, String sessionId, String apiKey) {
		return getBuilder(resource, null, sessionId, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiKey);
	}

	/**
	 * Create Web resource Builder to view or delete a Token 
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param sessionId Should look like this sessions/{sessionId}/tokens
	 * @param apiKey Authorize access on the mainzellist API
	 * @return WebResource Builder
	 */
	public static Builder getBuilderModifyToken(WebResource resource, String sessionId, String tokenId, String apiKey) {
		if (tokenId != null) {
			resource = resource.path(String.format(tokenPath, sessionId) + "/" + tokenId);
			sessionId = null;
		}
		
		return getBuilder(resource, null, sessionId, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiKey);
	}
	
	private static Builder getBuilder(WebResource resource, String tokenId, String sessionId, String acceptType, String contentType, String apiKey) {
		if (resource != null) {

			if (sessionId != null) {
				resource = resource.path(String.format(tokenPath, sessionId));
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
	// If a JSONObject parsing error occurs the reason could be that the ID Type is unknown!
	
	/**
	 * Create a tokenId to add a patient
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param sessionId Id of the current session
	 * @param idTypes Pseudonym types, which should be created and returned
	 * @return TokenId
	 */
	public static String createTokenIdAddPatient(WebResource resource, String sessionId, String... idTypes) {
		JSONObject tokenData = createTokenDataAddPatient(buildJSONArray(idTypes), null, null, null);
		return createTokenId(resource, sessionId, tokenData);
	}

	/**
	 * Create a tokenId to read a patient
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param sessionId Id of the current session
	 * @param resultFields TODO
	 * @param resultIds TODO
	 * @param searchIds TODO
	 * @return TokenId
	 */
	public static String createTokenIdReadPatient(WebResource resource, String sessionId, JSONArray resultFields, JSONArray resultIds, JSONObject searchIds) {
		JSONObject tokenData = createTokenDataReadPatient(resultFields, resultIds, searchIds);
		return createTokenId(resource, sessionId, tokenData);
	}
	
	/**
	 * Create a tokenId to edit a patient
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param sessionId Id of the current session
	 * @param patientId TODO
	 * @param fields TODO
	 * @return TokenId
	 */
	public static String createTokenIdEditPatient(WebResource resource, String sessionId, JSONObject patientId, JSONArray fields) {
		JSONObject tokenData = createTokenDataEditPatient(patientId, fields);
		return createTokenId(resource, sessionId, tokenData);
	}
	
	/**
	 * Create a tokenId specified in the tokenData
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param sessionId Id of the current session
	 * @param tokenData TODO
	 * @return TokenId
	 */
	public static String createTokenId(WebResource resource, String sessionId, JSONObject tokenData) {
		ClientResponse response = getBuilderCreateToken(resource, sessionId, apiKey)
				.post(ClientResponse.class, tokenData);
		
		if (response.getStatus() == 201) {
			return getTokenIdOfJSON(response.getEntity(JSONObject.class));
		} else {
			throw new Error("Error while creating token.");
		}
	}
	
	// - Create TokenData Methods -
	
	/**
	 * Create a json object with data to create a patient
	 * @param idtypes TODO
	 * @param fields TODO
	 * @param callback TODO
	 * @param redirect TODO
	 * @return TokenData which is needed to create a patient
	 */
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
	
	/**
	 * Create a json object with data to view a patient
	 * @param resultFields TODO
	 * @param resultIds TODO
	 * @param searchIds TODO
	 * @return TokenData which is needed to view a patient
	 */
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
	
	/**
	 * Create a json object with data to edit a patient
	 * @param patientId TODO
	 * @param fields TODO
	 * @return TokenData which is needed to edit a patient
	 */
	public static JSONObject createTokenDataEditPatient(JSONObject patientId, JSONArray fields) {
		JSONObject tokenData = new JSONObject();
		
		try {
			
			tokenData.put("type", "editPatient");
			
			JSONObject data = new JSONObject();
			
			if (patientId != null) {
				data.put("patientId", patientId);
			}
			
			if (fields != null) {
				data.put("fields", fields);
			}
			
			tokenData.put("data", data);
			
		} catch (JSONException e) {
			fail("JSON Key should not be null");
		}
		
		return tokenData;
	}

	// - Create PatientData Methods -
	
	/**
	 * Create a formula with the given parameters and returns it.
	 * Null parameters will be ignored.
	 * @param vorname first name
	 * @param nachname last name
	 * @param geburtsname birth name
	 * @param geburtstag birth day in this format 01
	 * @param geburtsmonat birth month in this format 01
	 * @param geburtsjahr birth year in this format 2015
	 * @param ort place
	 * @param plz postal code
	 * @return Formula with the given parameters
	 */
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
	
	/**
	 * Create a json formula with the given parameters and returns it.
	 * Null parameters will be ignored.
	 * @param vorname first name
	 * @param nachname last name
	 * @param geburtsname birth name
	 * @param geburtstag birth day in this format 01
	 * @param geburtsmonat birth month in this format 01
	 * @param geburtsjahr birth year in this format 2015
	 * @param ort place
	 * @param plz postal code
	 * @return Json formula with the given parameters
	 */
	public static JSONObject createJSONForm(String vorname, String nachname, String geburtsname, String geburtstag, String geburtsmonat, String geburtsjahr, String ort, String plz) {
		JSONObject jsonForm = new JSONObject();
		try {
			if (vorname != null) {
				jsonForm.put("vorname", vorname);
			}
			
			if (nachname != null) {
				jsonForm.put("nachname", nachname);
			}

			if (geburtsname != null) {
				jsonForm.put("geburtsname", geburtsname);
			}
			
			if (geburtstag != null) {
				jsonForm.put("geburtstag", geburtstag);
			}
			
			if (geburtsmonat != null) {
				jsonForm.put("geburtsmonat", geburtsmonat);
			}
					
			if (geburtsjahr != null) {
				jsonForm.put("geburtsjahr", geburtsjahr);
			}

			if (ort != null) {
				jsonForm.put("ort", ort);
			}
			
			if (plz != null) {
				jsonForm.put("plz", plz);
			}
			
		} catch (JSONException e) {
			throw new Error("Error while creating JsonForm!");
		}
		
		return jsonForm;
	}
	
	/**
	 * Create a session and return its Id.
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @return sessionId Id of the current session
	 */
	public static String createSession(WebResource resource) {
		ClientResponse response = getBuilderSession(resource.path(sessionPath), apiKey).post(ClientResponse.class);
		
		JSONObject entity = response.getEntity(JSONObject.class);
		
		return getSessionIdOfJSON(entity);
	}
	
	/**
	 * Create a session and path it to the web resource as an tokenPath.
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @return webResource TODO
	 */
	public static WebResource createTokenPath(WebResource resource) {
		return resource.path(String.format(tokenPath, createSession(resource)));
	}
	
	private static URI createUri(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			fail("'"+ uri + "' is not a URI");
			return null;
		}
	}
	
	
	// --- DATABASE METHODS ---
	
	/**
	 * Add a dummy Patient to the database.
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @return is true if the dummy Patient could be added.
	 */
	public static JSONObject addPatient(WebResource resource, String firstName, String lastName, String birthname, String birthDay, String birthmothe, String birthyear, String city, String plz) {
		String tokenId = createTokenIdAddPatient(resource, createSession(resource), "psn");
		
		Form formData = TestUtilities.createForm(firstName, lastName, birthname, birthDay, birthmothe, birthyear, city, plz);
		ClientResponse response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
				.post(ClientResponse.class, formData);
		
		int status = response.getStatus();
		if (status == 201) {
			System.out.println("Add Dummy Patient to Database");
			try {
				return response.getEntity(JSONArray.class).getJSONObject(0);
			} catch (Exception e) {
				System.out.println("Error while creating new Patient.");
				throw new Error("Error while creating new Patient.");
			} 
		}
		
		System.err.println("Could not add Dummy Patient to Database");
		throw new Error("Error while creating new Patient. (" + status + ")");
	}
	
	public static void addDummyPatient(WebResource resource) {
		addPatient(resource, "DummyFirstName", "DummyLastName", "DummySecondName", "01", "01", "2000", "Mainz", "55120");
	}
	
	/**
	 * Make an request and returns the given patient.
	 * @param resource Web resource whose URI refers to the base URI the Web application is deployed at
	 * @param patientId to search for
	 * @return patient as Array (fields, ids)
	 */
	public static JSONArray readPatient (WebResource resource, JSONObject patientId) {
		String tokenId = createTokenIdReadPatient(resource, createSession(resource), buildJSONArray(patientKeys), resultIds, patientId);
		ClientResponse response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey())
				.get(ClientResponse.class);
		
		if (response.getStatus() == 200) {
			return response.getEntity(JSONArray.class);
		}
		
		throw new Error("Error while searching for Patient " + patientId);
	}
	
	public static int deletePatient(WebResource resource, JSONObject patientId) {
		String tokenId = createTokenIdEditPatient(resource, createSession(resource), patientId, null);
		
		ClientResponse response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey())
				.put(Builder.class).delete(ClientResponse.class);
		
		return response.getStatus();
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
	
	public static String getStringOfJSON(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			fail("'"+ key + "' does not exist in JSONObject: " + jsonObject);
			return null;
		}
	}
	
	public static String getStringOfJSON(JSONArray jsonArray, String key) {
		try {	
			return getStringOfJSON(jsonArray.getJSONObject(0).getJSONObject("fields"), key);
		} catch (JSONException e) {
			fail("'"+ jsonArray + "' has no JSONObject");
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
		return new WebAppDescriptor.Builder(packagePath)
				.contextParam(configPackagePath, configFile)
				.contextPath("/mainzelliste")
				.contextListenerClass(Initializer.class)		
				.build();
	}

	
	// --- GETTER METHODS ---
	public static String getApikey() {
		return apiKey;
	}
	
	public static String[] getPatientKeys() {
		return patientKeys;
	}

	public static String getSessionpath() {
		return sessionPath;
	}
}
