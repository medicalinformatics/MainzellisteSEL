package de.unimainz.imbei.mzid.test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.unimainz.imbei.mzid.ID;
import de.unimainz.imbei.mzid.webservice.PatientsResource;

public class TestAddPatient {

	private static final String BASE_URL = "http://localhost:8080/mzid";
	
	@Test
	public void test() throws IOException{
	
		// POST session
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpPost postRequest = new HttpPost(BASE_URL + "/sessions");
//		postRequest.addHeader("mzidApiKey", "mdat1234");
//		postRequest.addHeader("Content-Type", "application/json");
//		HttpResponse response = httpClient.execute(postRequest);
//		assertEquals("Received unexpected status code for POST mzid/sessions.", 200, response.getStatusLine().getStatusCode());
//		
//		HttpEntity entity = response.getEntity();
//		assertNotNull(entity);
//		InputStream inStream = entity.getContent();
//		ObjectMapper mapper = new ObjectMapper();
//		String responseBody =  mapper.readValue(inStream, String.class); 
//		int thisChar;
//		System.out.println(responseBody);
//		
//		// Token holen
//		String requestURL = BASE_URL + responseBody + "/tokens";
//		postRequest = new HttpPost(requestURL);
//		postRequest.addHeader("mzidApiKey", "mdat1234");
//		postRequest.addHeader("Content-Type", "application/json");
//		postRequest.setEntity(new StringEntity("{\"type\" : \"addPatient\"}"));
//		response = httpClient.execute(postRequest);
//		assertEquals("Received unexpected status code for POST tokens.", 201, response.getStatusLine().getStatusCode());
//		
//		entity = response.getEntity();
//		assertNotNull(entity);
//		inStream = entity.getContent();
//		responseBody = mapper.readValue(inStream, String.class);
//		System.out.println(responseBody);
//		// TokenId auslesen
//		//Pattern pattern = Pattern.compile("/([^/])\"$");
//		Pattern pattern = Pattern.compile("/([^/]+)$");
//		Matcher matcher = pattern.matcher(responseBody);
//		if (!matcher.find(1))
//			fail("No Token in Response: " + responseBody);
//		String tokenId = matcher.group(1);
//		System.out.println(tokenId);
//		
//		// POST patient
//		
//		requestURL = BASE_URL + "/patients?tokenId=" + tokenId;
//		postRequest = new HttpPost(requestURL);
//		postRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
//		postRequest.setEntity(new StringEntity("vorname=Max&nachname=Mustermann&geburtstag=12&geburtsmonat=8&geburtsjahr=1990"));
//		response = httpClient.execute(postRequest);
		
		
		
		PatientsResource p = new PatientsResource();
		MultivaluedMap<String, String> form = new MultivaluedMapImpl();
		ID newId;
		
		form.put("vorname", Arrays.asList("Anreas"));
		form.put("nachname", Arrays.asList("Testpatient"));
		form.put("geburtstag", Arrays.asList("2"));
		form.put("geburtsmonat", Arrays.asList("3"));
		form.put("geburtsjahr", Arrays.asList("1991"));
		newId = p.newPatient("token", form);
		System.out.println(newId.getIdString());
		
//		form.put("vorname", Arrays.asList("Max"));
//		form.put("nachname", Arrays.asList("Mustermann"));
//		form.put("geburtstag", Arrays.asList("1"));
//		form.put("geburtsmonat", Arrays.asList("2"));
//		form.put("geburtsjahr", Arrays.asList("1990"));
//
//		newId = p.newPatient("token", form);
//		System.out.println(newId.getIdString());
	}
}