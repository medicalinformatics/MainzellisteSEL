package de.unimainz.imbei.mzid.test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class TestAddPatient {

	private static final String BASE_URL = "http://localhost:8080/mzid";
	
	@Test
	public void test() throws IOException{
		
		// POST session
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(BASE_URL + "/sessions");
		postRequest.addHeader("mzidApiKey", "mdat1234");
		postRequest.addHeader("Content-Type", "application/json");
		HttpResponse response = httpClient.execute(postRequest);
		assertEquals("Received unexpected status code for POST mzid/sessions.", 200, response.getStatusLine().getStatusCode());
		
		HttpEntity entity = response.getEntity();
		assertNotNull(entity);
		InputStream inStream = entity.getContent();
		StringBuffer responseBody = new StringBuffer();
		int thisChar;
		while ((thisChar = inStream.read()) >= 0)
			responseBody.append((char) thisChar);
		// Umschließende Anführungszeichen löschen
		responseBody.deleteCharAt(0);
		responseBody.deleteCharAt(responseBody.length() - 1);
		System.out.println(responseBody);
		
		// Token holen
		String requestURL = BASE_URL + responseBody + "/tokens";
		postRequest = new HttpPost(requestURL);
		postRequest.addHeader("mzidApiKey", "mdat1234");
		postRequest.addHeader("Content-Type", "application/json");
		postRequest.setEntity(new StringEntity("{\"type\" : \"addPatient\"}"));
		response = httpClient.execute(postRequest);
		assertEquals("Received unexpected status code for POST tokens.", 201, response.getStatusLine().getStatusCode());
		
		entity = response.getEntity();
		assertNotNull(entity);
		inStream = entity.getContent();
		responseBody = new StringBuffer();
		while ((thisChar = inStream.read()) >= 0)
			responseBody.append((char) thisChar);
		System.out.println(responseBody);
		// TokenId auslesen
		//Pattern pattern = Pattern.compile("/([^/])\"$");
		Pattern pattern = Pattern.compile("/([^/]+)\"$");
		Matcher matcher = pattern.matcher(responseBody);
		if (!matcher.find(1))
			fail("No Token in Response: " + responseBody);
		String tokenId = matcher.group(1);
		System.out.println(tokenId);
		
		// POST patient
		
		requestURL = BASE_URL + "/patients?tokenId=" + tokenId + "&name=Max&nachname=Mustermann";
		postRequest = new HttpPost(requestURL);
		postRequest.addHeader("mzidApiKey", "mdat1234");
		postRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
		response = httpClient.execute(postRequest);
		
		
	}

}
