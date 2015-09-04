package de.pseudonymisierung.mainzelliste.test;

import static org.junit.Assert.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.test.framework.JerseyTest;

public class ReadPatientTest extends JerseyTest {

	private WebResource resource = resource();
	private ClientResponse response;
	
	public ReadPatientTest() {
		super(TestUtilities.setUpTest());
	}
	
	@Test
	public void testReadPatientToken() {
		String sessionId = TestUtilities.createSession(resource);
		String tokenRequestPath = "sessions/" + sessionId + "/tokens";
		
		// Generate tokenData
		JSONArray resultFields = TestUtilities.buildJSONArray("vorname");
		JSONArray resultIds = TestUtilities.buildJSONArray("pid");
		JSONObject searchId = TestUtilities.buildJSONObject("idType", "psn", "idString", "1");
		JSONObject tokenData = TestUtilities.createTokenDataReadPatient(resultFields, resultIds, searchId);
		
		// TODO: Anlegen mit falscher IP-Adresse → Erwarte 401 Unauthorized
		
		// Call without mainzellisteApiKey
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, null)
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token without mainzellisteApiKey did not return 401 status.", 401, response.getStatus());
		
		// Call with wrong mainzellisteApiKey
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, "wrongKey")
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with wrong mainzellisteApiKey did not return 401 status.", 401, response.getStatus());
		
		// Call with no existing session
		response = TestUtilities.getBuilderTokenPost(resource, "/sessions/unbekannt/tokens", TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with non existing session did not return 404 status.", 404, response.getStatus());
		
		// TODO: Anlegen mit falschem Format → Erwarte 400 Bad Request
		
		// TODO: User mit psn 1 in der datenbank anlegen 
		// Create Token for readPatient
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token not return 201 status. Message from server: " + response.getEntity(String.class), 201, response.getStatus());
		// TODO: Rückgabe des Tokens Testen
		
		// Create Token with an unknown idType
		tokenData = TestUtilities.createTokenDataReadPatient(resultFields, resultIds, TestUtilities.buildJSONObject("idType", "unknownid", "idString", "1"));
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown idType not return 400 status.", 400, response.getStatus());
		
		// Create Token with an unknown resultField
		tokenData = TestUtilities.createTokenDataReadPatient(TestUtilities.buildJSONArray("unbekannt"), resultIds, searchId);
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown resultField not return 400 status.", 400, response.getStatus());

		// Create Token with an unknown resultId
		tokenData = TestUtilities.createTokenDataReadPatient(resultFields, TestUtilities.buildJSONArray("unknownresultid"), searchId);
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown resultField not return 400 status.", 400, response.getStatus());

		// Create Token with not existing id/pseudonym
		tokenData = TestUtilities.createTokenDataReadPatient(resultFields, resultIds, TestUtilities.buildJSONObject("idType", "psn", "idString", "-1"));
		response = TestUtilities.getBuilderTokenPost(resource, tokenRequestPath, TestUtilities.getApikey())
				.post(ClientResponse.class, tokenData);
		assertEquals("Creating token with unknown id/pseudonym not return 401 status.", 401, response.getStatus());
	}
	
	@Test
	public void testReadPatient() {
		String sessionId = TestUtilities.createSession(resource);
		String tokenPath = "sessions/" + sessionId + "/tokens";
		
		String patientsPath = "patients";

		// Call without token
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), null, null)
				.get(ClientResponse.class);
		assertEquals("Read patients without token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());

		// Call with invalid (non-existing) token
		response = TestUtilities.getBuilderPatient(resource.path(patientsPath), "invalidToken", null)
				.get(ClientResponse.class);
		assertEquals("Read patients with non-existing token did not return 401 status. Message from server: " + response.getEntity(String.class), 401, response.getStatus());
		
		//		ClientConfig clientConfig = new DefaultClientConfig();
//		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
//				Boolean.TRUE);
//		Client client = Client.create(clientConfig);
//
//		WebResource resource = client
//				.resource("http://localhost:8080/mainzelliste");
//		Map<String, Object> tokenData = new HashMap<String, Object>();
//		JSONArray idTypes = new JSONArray();
//		idTypes.put("pid");
//		idTypes.put("psn");
//		// tokenData.put("idtypes", Arrays.asList("pid", "psn"));
//		tokenData.put("idtypes", idTypes);
//		try {
//			Token t = rSession.getToken("addPatient", tokenData);
//
//			String result = resource.path("/patients")
//					.queryParam("tokenId", t.getId())
//					.type(MediaType.APPLICATION_FORM_URLENCODED)
//					.accept(MediaType.APPLICATION_JSON)
//					.post(String.class, testPatientFields);
//
//			// System.out.println(result);
//
//			Map<String, String> createdIDs = new HashMap<String, String>();
//
//			JSONArray resultArray = new JSONArray(result);
//			for (int i = 0; i < resultArray.length(); i++) {
//				JSONObject thisId = resultArray.getJSONObject(i);
//				createdIDs.put(thisId.getString("idType"),
//						thisId.getString("idString"));
//			}
//
//			// Teste einzelne ID-Typen, suche nach pid
//			for (String searchIdType : createdIDs.keySet()) {
//				for (String thisIdType : createdIDs.keySet()) {
//					tokenData = new HashMap<String, Object>();
//					tokenData.put(
//							"searchIds",
//							new JSONArray().put(new JSONObject().put("idType",
//									searchIdType).put("idString",
//									createdIDs.get(searchIdType))));
//					tokenData.put("resultIds", new JSONArray().put(thisIdType));
//
//					t = rSession.getToken("readPatients", tokenData);
//
//					// List resultList = resource.path("patients")
//					// .queryParam("tokenId", t.getId())
//					// .type(MediaType.APPLICATION_JSON)
//					// .accept(MediaType.APPLICATION_JSON)
//					// .get(List.class);
//
//					List resultList = resource
//							.path("patients/tokenId/" + t.getId())
//							.type(MediaType.APPLICATION_JSON)
//							.accept(MediaType.APPLICATION_JSON).get(List.class);
//
//					// Es sollte genau ein Patient geliefert werden
//					assertEquals(1, resultList.size());
//
//					// Format des Patientenobjekts pr�fen
//					Map<String, ?> thisPatient = (Map<String, ?>) resultList
//							.get(0);
//					assertTrue(thisPatient.containsKey("ids"));
//					// Hier noch keine Felder angefordert
//					assertFalse(thisPatient.containsKey("fields"));
//
//					// Pr�fe IDs
//					List<Map<String, String>> ids = (List<Map<String, String>>) thisPatient
//							.get("ids");
//					for (Map<String, String> thisId : ids) {
//						assertEquals("Received an ID that was not requested.",
//								thisIdType, thisId.get("idType"));
//						assertEquals("Reveived different ID than on creation.",
//								createdIDs.get(thisId.get("idType")),
//								thisId.get("idString"));
//					}
//				}
//
//				// Hole alle IDs
//				tokenData = new HashMap<String, Object>();
//				tokenData.put(
//						"searchIds",
//						new JSONArray().put(new JSONObject().put("idType",
//								searchIdType).put("idString",
//								createdIDs.get(searchIdType))));
//				tokenData.put("resultIds", new JSONArray(createdIDs.keySet()));
//
//				t = rSession.getToken("readPatients", tokenData);
//
////				List resultList = resource
////						.path("patients/tokenId/" + t.getId())
////						.type(MediaType.APPLICATION_JSON)
////						.accept(MediaType.APPLICATION_JSON).get(List.class);
//
//				List resultList = resource
//						.path("patients/")
//						.queryParam("tokenId", t.getId())
//						.type(MediaType.APPLICATION_JSON)
//						.accept(MediaType.APPLICATION_JSON)
//						.get(List.class);
//
//				// Store expected ID types, remove received types, at the end
//				// set should be empty
//				Set<String> expectedIds = new HashSet<String>(
//						createdIDs.keySet());
//				Map<String, ?> thisPatient = (Map<String, ?>) resultList.get(0);
//				List<Map<String, String>> ids = (List<Map<String, String>>) thisPatient
//						.get("ids");
//				for (Map<String, String> thisId : ids) {
//					assertTrue("Received ID type that was not requested.",
//							expectedIds.contains(thisId.get("idType")));
//					expectedIds.remove(thisId.get("idType"));
//				}
//				assertEquals("Did not receive all requested IDs", 0,
//						expectedIds.size());
//
//				// Check fields
//				// Perform some iterations with different selections of fields
//				// increase proportion from 0 to all fields
//				int nIterations = 20;
//				for (int i = 0; i < nIterations; i++) {
//					Set<String> selectedFields = new HashSet<String>();
//					for (String thisField : testPatientFields.keySet()) {
//						// select a field with probability 0.5
//						if (Math.random() >= (double) i / (nIterations - 1))
//							selectedFields.add(thisField);
//					}
//					tokenData = new HashMap<String, Object>();
//					tokenData.put(
//							"searchIds",
//							new JSONArray().put(new JSONObject().put("idType",
//									searchIdType).put("idString",
//									createdIDs.get(searchIdType))));
//					tokenData.put("fields", new JSONArray(selectedFields));
//					t = rSession.getToken("readPatients", tokenData);
//
//					resultList = resource.path("patients/tokenId/" + t.getId())
//							.type(MediaType.APPLICATION_JSON)
//							.accept(MediaType.APPLICATION_JSON).get(List.class);
//					thisPatient = (Map<String, ?>) resultList.get(0);
//					Map<String, String> fields = (Map<String, String>) thisPatient
//							.get("fields");
//					assertEquals("Received different fields than expected", selectedFields, fields.keySet());
//					
//					for (String thisField : selectedFields) {
//						assertEquals("Received field " + thisField + " differed from original input",
//								testPatientFields.getFirst(thisField), fields.get(thisField));
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			fail("Exception thrown in test:" + e.getMessage());
//			e.printStackTrace();
//		}
	}
}
