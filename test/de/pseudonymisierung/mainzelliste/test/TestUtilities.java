package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

/**
 * Helper class to generate Test for the mainzellist
 * 
 * @author rmelloni
 *
 */
public class TestUtilities {

	// --- Visible outside ---
	private static final String apiKey = "mdat1234";
	private static final String[] patientKeys = { "vorname", "nachname", "geburtsname", "geburtstag", "geburtsmonat", "geburtsjahr", "ort", "plz"};

	// --- Invisible outside ---
	private static final String apiVersion = "2.0";
	private static final JSONArray resultIds = buildJSONArray("psn");
	private static final String tokenPath = "sessions/%s/tokens";
	private static final String patientsPath = "patients/";
	private static final String sessionPath = "sessions/";
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
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param tokenId
	 *            One time authorization for Requests
	 * @param apiKey
	 *            Authorize access on the mainzellist API
	 * @return WebResource Builder
	 */
	public static Builder getBuilderPatient (WebResource resource, String tokenId, String apiKey) {
		return getBuilder(resource.path(patientsPath), tokenId, null, MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, apiKey);
	}

	/**
	 * Create an Web resource Builder for Patient Edit interaction (<b>Is
	 * temporally needed because editing an Patient could not be done with
	 * MediaType.APPLICATION_FORM_URLENCODED</b>)
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param tokenId
	 *            One time authorization for Requests
	 * @param apiKey
	 *            Authorize access on the mainzellist API
	 * @return WebResource Builder
	 */
	public static Builder getBuilderPatientEdit (WebResource resource, String tokenId, String apiKey) {
		WebResource editPatientResource = resource.path(patientsPath);

		if (tokenId != null) {
			editPatientResource = editPatientResource.path("tokenId").path(tokenId);
		}

		return getBuilder(editPatientResource, null, null, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiKey);
	}

	/**
	 * Create Web resource Builder for Session interaction
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param sessionUri
	 *            full path of session. If null it will be ignored.
	 * @param apiKey
	 *            Authorize access on the mainzellist API
	 * @return WebResource Builder
	 */
	public static Builder getBuilderSession (WebResource resource, URI sessionUri, String apiKey) {
		WebResource sessionResource = resource.path(sessionPath);

		if (sessionUri != null) {
			sessionResource = sessionResource.uri(sessionUri);
		}

		return getBuilder(sessionResource, null, null, MediaType.APPLICATION_JSON, null, apiKey);
	}

	/**
	 * Create Web resource Builder for creating a Token
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param sessionId
	 *            Id of a valid session
	 * @param apiKey
	 *            Authorize access on the mainzellist API
	 * @return WebResource Builder
	 */
	public static Builder getBuilderCreateToken (WebResource resource, String sessionId, String apiKey) {
		return getBuilder(resource, null, sessionId, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiKey);
	}

	/**
	 * Create Web resource Builder to view or delete a Token
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param sessionId
	 *            Id of a valid session
	 * @param tokenId
	 *            One time authorization for Requests
	 * @param apiKey
	 *            Authorize access on the mainzellist API
	 * @return WebResource Builder
	 */
	public static Builder getBuilderModifyToken (WebResource resource, String sessionId, String tokenId, String apiKey) {
		if (tokenId != null) {
			resource = resource.path(String.format(tokenPath, sessionId) + "/" + tokenId);
			sessionId = null;
		}

		return getBuilder(resource, null, sessionId, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, apiKey);
	}

	private static Builder getBuilder (WebResource resource, String tokenId, String sessionId, String acceptType, String contentType, String apiKey) {
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
	// If a JSONObject parsing error occurs the reason could be that the ID Type
	// is unknown!

	/**
	 * Create a tokenId to add a patient
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param sessionId
	 *            Id of a valid session
	 * @param idTypes
	 *            Pseudonym types, which should be created and returned
	 * @return TokenId
	 */
	public static String createTokenIdAddPatient (WebResource resource, String sessionId, String... idTypes) {
		JSONObject tokenData = createTokenDataAddPatient(buildJSONArray(idTypes), null, null, null);
		return createTokenId(resource, sessionId, tokenData);
	}

	/**
	 * Create a tokenId to read a patient
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param sessionId
	 *            Id of a valid session
	 * @param resultFields
	 *            Fields which will be shown
	 * @param resultIds
	 *            Id's which will be shown
	 * @param searchIds
	 *            Id's to search for. The key is the id type and the value is
	 *            the id.
	 * @return TokenId
	 */
	public static String createTokenIdReadPatient (WebResource resource, String sessionId, JSONArray resultFields, JSONArray resultIds, JSONObject searchIds) {
		JSONObject tokenData = createTokenDataReadPatient(resultFields, resultIds, searchIds);
		return createTokenId(resource, sessionId, tokenData);
	}

	/**
	 * Create a tokenId to edit a patient
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param sessionId
	 *            Id of a valid session
	 * @param patientId
	 *            Id to search for. The key is the id name and the value is the
	 *            id.
	 * @param fields
	 *            Field's which will be editable. If Null given all will be
	 *            editable.
	 * @return TokenId
	 */
	public static String createTokenIdEditPatient (WebResource resource, String sessionId, JSONObject patientId, JSONArray fields) {
		JSONObject tokenData = createTokenDataEditPatient(patientId, fields);
		return createTokenId(resource, sessionId, tokenData);
	}

	/**
	 * Create a tokenId specified in the tokenData
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param sessionId
	 *            Id of a valid session
	 * @param tokenData
	 *            Whole information which is needed to create a token id.
	 * @return TokenId
	 */
	public static String createTokenId (WebResource resource, String sessionId, JSONObject tokenData) {
		ClientResponse response = getBuilderCreateToken(resource, sessionId, apiKey).post(ClientResponse.class, tokenData);

		if (response.getStatus() == 201) {
			return getTokenIdOfJSON(response.getEntity(JSONObject.class));
		} else {
			throw new Error("Error while creating token.");
		}
	}

	// - Create TokenData Methods -

	/**
	 * Create a json object with data
	 * 
	 * @param idtypes
	 *            Id types which should be created.
	 * @param fields
	 *            will be pre-filled.
	 * @param callback
	 *            Url which will be called after a successful insertion of the
	 *            patient and before transmission the answer of the request
	 * @param redirect
	 *            Url which will be refer to after a successful insertion of the
	 *            patient
	 * @return TokenData which is needed to create a patient
	 */
	public static JSONObject createTokenDataAddPatient (JSONArray idtypes, JSONObject fields, String callback, String redirect) {
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
	 * 
	 * @param resultFields
	 *            Fields which will be shown
	 * @param resultIds
	 *            Id's which will be shown
	 * @param searchIds
	 *            Id's to search for. The key is the id type and the value is
	 *            the id.
	 * @return TokenData which is needed to view a patient
	 */
	public static JSONObject createTokenDataReadPatient (JSONArray resultFields, JSONArray resultIds, JSONObject... searchIds) {
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
	 * 
	 * @param patientId
	 *            Id of patient which wanted to be edited
	 * @param fields
	 *            Field's which will be editable. If Null given all will be
	 *            editable.
	 * @return TokenData which is needed to edit a patient
	 */
	public static JSONObject createTokenDataEditPatient (JSONObject patientId, JSONArray fields) {
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
	 * Create a formula with the given parameters and returns it. Null
	 * parameters will be ignored.
	 * 
	 * @param vorname
	 *            first name
	 * @param nachname
	 *            last name
	 * @param geburtsname
	 *            birth name
	 * @param geburtstag
	 *            birth day in this format 01
	 * @param geburtsmonat
	 *            birth month in this format 01
	 * @param geburtsjahr
	 *            birth year in this format 2015
	 * @param ort
	 *            place
	 * @param plz
	 *            postal code
	 * @return Formula with the given parameters
	 */
	public static Form createForm (String vorname, String nachname, String geburtsname, String geburtstag, String geburtsmonat, String geburtsjahr, String ort, String plz) {
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
	 * Create a json formula with the given parameters and returns it. Null
	 * parameters will be ignored.
	 * 
	 * @param vorname
	 *            first name
	 * @param nachname
	 *            last name
	 * @param geburtsname
	 *            birth name
	 * @param geburtstag
	 *            birth day in this format 01
	 * @param geburtsmonat
	 *            birth month in this format 01
	 * @param geburtsjahr
	 *            birth year in this format 2015
	 * @param ort
	 *            city
	 * @param plz
	 *            postal code
	 * @return Json formula with the given parameters
	 */
	public static JSONObject createJSONForm (String vorname, String nachname, String geburtsname, String geburtstag, String geburtsmonat, String geburtsjahr, String ort, String plz) {
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
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @return sessionId Id of the current session
	 */
	public static String createSession (WebResource resource) {
		ClientResponse response = getBuilderSession(resource, null, apiKey).post(ClientResponse.class);

		JSONObject entity = response.getEntity(JSONObject.class);

		return getSessionIdOfJSON(entity);
	}

	private static URI createUri (String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			fail("'" + uri + "' is not a URI");
			return null;
		}
	}

	// --- DATABASE METHODS ---

	/**
	 * Add a Patient to the database.
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param firstName
	 *            first name of the patient
	 * @param lastName
	 *            last name of the patient
	 * @param birthname
	 *            birth name name of the patient
	 * @param birthDay
	 *            birth day in this format 01
	 * @param birthmothe
	 *            birth month in this format 01
	 * @param birthyear
	 *            birth year in this format 2015
	 * @param city
	 *            city of the patient
	 * @param plz
	 *            postal code of the patient
	 * @return the first entity of the request.
	 */
	public static JSONObject addPatient (WebResource resource, String firstName, String lastName, String birthname, String birthDay, String birthmothe, String birthyear, String city, String plz) {
		String tokenId = createTokenIdAddPatient(resource, createSession(resource), "psn");

		Form formData = TestUtilities.createForm(firstName, lastName, birthname, birthDay, birthmothe, birthyear, city, plz);
		ClientResponse response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).post(ClientResponse.class, formData);

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

	/**
	 * Add a dummy patient to the database.
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 */
	public static void addDummyPatient (WebResource resource) {
		addPatient(resource, "DummyFirstName", "DummyLastName", "DummySecondName", "01", "01", "2000", "Mainz", "55120");
	}

	/**
	 * Make an request and returns the given patient.
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param patientId
	 *            to search for
	 * @return patient as Array (fields, ids)
	 */
	public static JSONArray readPatient (WebResource resource, JSONObject patientId) {
		String tokenId = createTokenIdReadPatient(resource, createSession(resource), buildJSONArray(patientKeys), resultIds, patientId);
		ClientResponse response = TestUtilities.getBuilderPatient(resource, tokenId, TestUtilities.getApikey()).get(ClientResponse.class);

		if (response.getStatus() == 200) {
			return response.getEntity(JSONArray.class);
		}

		throw new Error("Error while searching for Patient " + patientId);
	}

	/**
	 * <b>NOT SUPPORTET YET!</b> Deletes the patient in the database.
	 * 
	 * @param resource
	 *            Web resource whose URI refers to the base URI the Web
	 *            application is deployed at
	 * @param patientId
	 *            to search for
	 * @return the status code of the request
	 */
	public static int deletePatient (WebResource resource, JSONObject patientId) {
		String tokenId = createTokenIdEditPatient(resource, createSession(resource), patientId, null);

		ClientResponse response = TestUtilities.getBuilderPatientEdit(resource, tokenId, TestUtilities.getApikey()).put(Builder.class).delete(ClientResponse.class);

		return response.getStatus();
	}

	// --- JSON METHODS ---

	// - Get Methods -

	/**
	 * Extract the token id of a json object.
	 * 
	 * @param jsonObject
	 *            to be extracted.
	 * @return token id of the jason object
	 */
	public static String getTokenIdOfJSON (JSONObject jsonObject) {
		return getStringOfJSON(jsonObject, tokenIdKey);
	}

	/**
	 * Extract the session id of a json object.
	 * 
	 * @param jsonObject
	 *            to be extracted
	 * @return session id of the jason object
	 */
	public static String getSessionIdOfJSON (JSONObject jsonObject) {
		return getStringOfJSON(jsonObject, sessionIdKey);
	}

	/**
	 * Extract the session uri of a json object.
	 * 
	 * @param jsonObject
	 *            to be extracted
	 * @return uri of the jason object
	 */
	public static URI getSessionUriOfJSON (JSONObject jsonObject) {
		return createUri(getStringOfJSON(jsonObject, sessionUriKey));
	}

	/**
	 * Extract the value of the key in the json object. Fails if the given key
	 * does not exist.
	 * 
	 * @param jsonObject
	 *            to be extracted
	 * @param key
	 *            of the value to search for
	 * @return string value of the key
	 */
	public static String getStringOfJSON (JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			fail("'" + key + "' does not exist in JSONObject: " + jsonObject);
			return null;
		}
	}

	/**
	 * Extract the value of the key in the json array. Fails if the given key
	 * does not exist.
	 * 
	 * @param jsonArray
	 *            to be extracted
	 * @param key
	 *            of the value to search for
	 * @return string value of the key
	 */
	public static String getStringOfJSON (JSONArray jsonArray, String key) {
		try {
			return getStringOfJSON(jsonArray.getJSONObject(0).getJSONObject("fields"), key);
		} catch (JSONException e) {
			fail("'" + jsonArray + "' has no JSONObject");
			return null;
		}
	}

	/**
	 * Extract the value of the key in the json object. Fails if the given key
	 * does not exist.
	 *
	 * @param jsonObject
	 *            to be extracted
	 * @param key
	 *            of the value to search for
	 * @return booolean value of the key
	 */
	public static Boolean getBooleanOfJSON (JSONObject jsonObject, String key) {
		try {
			return jsonObject.getBoolean(key);
		} catch (JSONException e) {
			fail("'" + key + "' does not exist in JSONObject: " + jsonObject);
			return null;
		}
	}

    /**
     * Extract the value of the key in the json array. Fails if the given key
     * does not exist.
     *
     * @param jsonArray
     *            to be extracted
     * @param key
     *            of the value to search for
     * @return boolean value of the key
     */
    public static Boolean getBooleanOfJSON (JSONArray jsonArray, String key) {
        try {
            return getBooleanOfJSON(jsonArray.getJSONObject(0), key);
        } catch (JSONException e) {
            fail("'" + jsonArray + "' has no JSONObject");
            return null;
        }
    }


	// - Build Methods -

	/**
	 * Build a json object of the given strings. It should always be key value
	 * pairs, if not the method will fail. White no given strings the method
	 * will return just an empty jason object.
	 * 
	 * @param strings
	 *            to be represent as an json object.
	 * @return the built json object
	 */
	public static JSONObject buildJSONObject (String... strings) {
		if (strings != null && strings.length > 0) {
			JSONObject jsonObject = new JSONObject();
			for (int i = 0; i + 1 < strings.length; i++) {
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

	/**
	 * Build a json array of the given strings. White no given strings the
	 * method will return just an empty jason array.
	 * 
	 * @param objects
	 *            to be represent as an json object.
	 * @return the built json array
	 */
	public static <T extends Object> JSONArray buildJSONArray (T... objects) {
		if (objects != null && objects.length > 0) {
			return new JSONArray(Arrays.asList(objects));
		} else {
			return new JSONArray();
		}
	}

	// --- HELPER METHODS ---

	/**
	 * Let the Thread stop for the given milliseconds.
	 * 
	 * @param millis
	 *            milliseconds to sleep
	 */
	public static void sleep (int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set test configurations.
	 * 
	 * @return should given the super constructor of an jersy test
	 */
	public static AppDescriptor setUpTest () {
		System.setProperty("derby.stream.error.field", "java.lang.System.err");
		return new WebAppDescriptor.Builder(packagePath).contextParam(configPackagePath, configFile).contextPath("/mainzelliste").contextListenerClass(Initializer.class).build();
	}

	// --- GETTER METHODS ---

	/**
	 * @return the api key for the mainzellist api
	 */
	public static String getApikey () {
		return apiKey;
	}

	/**
	 * @return all patient key's
	 */
	public static String[] getPatientKeys () {
		return patientKeys;
	}

	/**
	 * Get path of configuration file.
	 * @return Path of configuration file.
	 */
	public static String getConfigfilePath() {
		return configFile;
	}
	
	/**
	 * Get contents of configuration file.
	 * @return Contents of configuration file.
	 */
	public static BufferedReader getConfigfile() {
		return new BufferedReader(new InputStreamReader(TestUtilities.class.getResourceAsStream(configFile)));
	}
}
