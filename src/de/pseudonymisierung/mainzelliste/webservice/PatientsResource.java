/*
 * Copyright (C) 2013 Martin Lablans, Andreas Borg, Frank Ückert
 * Contact: info@mainzelliste.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it 
 * with Jersey (https://jersey.java.net) (or a modified version of that 
 * library), containing parts covered by the terms of the General Public 
 * License, version 2.0, the licensors of this Program grant you additional 
 * permission to convey the resulting work.
 */
package de.pseudonymisierung.mainzelliste.webservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.api.view.Viewable;

import de.pseudonymisierung.mainzelliste.Config;
import de.pseudonymisierung.mainzelliste.Field;
import de.pseudonymisierung.mainzelliste.ID;
import de.pseudonymisierung.mainzelliste.IDGeneratorFactory;
import de.pseudonymisierung.mainzelliste.IDRequest;
import de.pseudonymisierung.mainzelliste.Patient;
import de.pseudonymisierung.mainzelliste.Servers;
import de.pseudonymisierung.mainzelliste.Session;
import de.pseudonymisierung.mainzelliste.Validator;
import de.pseudonymisierung.mainzelliste.dto.Persistor;
import de.pseudonymisierung.mainzelliste.exceptions.InternalErrorException;
import de.pseudonymisierung.mainzelliste.exceptions.NotImplementedException;
import de.pseudonymisierung.mainzelliste.exceptions.UnauthorizedException;
import de.pseudonymisierung.mainzelliste.matcher.MatchResult;
import de.pseudonymisierung.mainzelliste.matcher.MatchResult.MatchResultType;

/**
 * Resource-based access to patients.
 */
@Path("/patients")
public class PatientsResource {
	
	private Logger logger = Logger.getLogger(PatientsResource.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllPatients(@Context HttpServletRequest req,
			@QueryParam("tokenId") String tokenId) throws UnauthorizedException {		
		
		logger.info("Received GET /patients");
		
		/*
		 * If a token (type "readPatients") is provided, use this 
		 */
		if (tokenId != null)
			return this.getPatientsToken(tokenId);
		
		/* 
		 * Unrestricted access for user role 'admin' via tomcat-users.xml. 
		 */
		if (!req.isUserInRole("admin"))
			throw new UnauthorizedException();
		return Response.ok().entity(Persistor.instance.getAllIds()).build();
	}
	

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response newPatientBrowser(
			@QueryParam("tokenId") String tokenId,
			MultivaluedMap<String, String> form){
		Token t = Servers.instance.getTokenByTid(tokenId);
		Map<String, Object> createRet = createNewPatient(tokenId, form); 
		List<ID> ids = (List<ID>) createRet.get("ids");
		IDRequest req = (IDRequest) createRet.get("request");
		MatchResult result = (MatchResult) createRet.get("result");
		Map <String, Object> map = new HashMap<String, Object>();
		if (ids == null) { // unsure case
			// Copy form to JSP model so that input is redisplayed
			for (String key : form.keySet())
			{
				map.put(key, form.getFirst(key));
			}
			map.put("readonly", "true");
			map.put("tokenId", tokenId);
			return Response.status(Status.ACCEPTED)
					.entity(new Viewable("/unsureMatch.jsp", map)).build();
		} else {
			if (t != null && t.getData() != null && t.getData().containsKey("redirect")) {
				UriTemplate redirectURITempl = new UriTemplate(t.getDataItemString("redirect"));
				HashMap<String, String> templateVarMap = new HashMap<String, String>();
				for (String templateVar : redirectURITempl.getTemplateVariables()) {
					ID thisID = req.getAssignedPatient().getId(templateVar);
					String idString = thisID.getIdString();
					templateVarMap.put(templateVar, idString);
				}
				try {
					URI redirectURI = new URI(redirectURITempl.createURI(templateVarMap));
					// Remove query parameters and pass them to JSP. The redirect is put
					// into the "action" tag of a form and the parameters are passed as 
					// hidden fields				
					MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(redirectURI, true);
					String redirectURIStripped =redirectURI.toString().substring(0,
							redirectURI.toString().indexOf("?"));
					map.put("redirect", redirectURI);
					map.put("redirectParams", queryParams);
					//return Response.status(Status.SEE_OTHER).location(redirectURI).build();
				} catch (URISyntaxException e) {
					// Wird auch beim Anlegen des Tokens geprüft.
					throw new InternalErrorException("Die übergebene Redirect-URL " + redirectURITempl.getTemplate() + "ist ungültig!");
				}
			}
			
			// If Idat are to be redisplayed in the result form...
			if (Boolean.parseBoolean(Config.instance.getProperty("result.printIdat"))) {
				//...copy input to JSP 
				for (String key : form.keySet())
				{
					map.put(key, form.getFirst(key));
				}
				// and set flag for JSP to display them
				map.put("printIdat", true);
			}
			// FIXME alle IDs übergeben und anzeigen
			map.put("id", ids.get(0).getIdString());
			map.put("tentative", ids.get(0).isTentative());
			
			if (Config.instance.debugIsOn() && result.getResultType() != MatchResultType.NON_MATCH)
			{
				map.put("debug", "on");
				map.put("weight", Double.toString(result.getBestMatchedWeight()));
				Map<String, Field<?>> matchedFields = result.getBestMatchedPatient().getFields();
				Map<String, String> bestMatch= new HashMap<String, String>();
				for(String fieldName : matchedFields.keySet())
				{
					bestMatch.put(fieldName, matchedFields.get(fieldName).toString());
				}
				map.put("bestMatch", bestMatch);
			}
			return Response.ok(new Viewable("/patientCreated.jsp", map)).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newPatientJson(
			@QueryParam("tokenId") String tokenId,
			@Context HttpServletRequest request,
			@Context UriInfo context,
			MultivaluedMap<String, String> form) throws JSONException {
		Map<String, Object> responseMap = createNewPatient(tokenId, form);
		logger.info("Accept: " + request.getHeader("Accept"));
		logger.info("Content-Type: " + request.getHeader("Content-Type"));
		List<ID> newIds = (List<ID>) responseMap.get("ids");
		MatchResult result = (MatchResult) responseMap.get("result");
		
		
		JSONArray ret = new JSONArray();
		for (ID thisID : newIds) {
			URI newUri = context.getBaseUriBuilder()
					.path(PatientsResource.class)
					.path("/{idtype}/{idvalue}")
					.build(thisID.getType(), thisID.getIdString());

			ret.put(new JSONObject()
				.put("idType", thisID.getType())
				.put("idString", thisID.getIdString())
				.put("tentative", thisID.isTentative())
				.put("uri", newUri));
		}
				
		return Response
			.status(Status.CREATED)
			.entity(ret)
			.build();
	}

	/**
	 * PID request.
	 * Looks for a patient with the specified data in the database. If a match is found, the 
	 * ID of the matching patient is returned. If no match or possible match is found, a new
	 * patient with the specified data is created. If a possible match is found and the form
	 * has an entry "sureness" whose value can be parsed to true (by Boolean.parseBoolean()),
	 * a new patient is created. Otherwise, return null.
	 * @param tokenId
	 * @param form
	 * @return A map with the following members:
	 * 	<ul>
	 * 		<li> id: The generated id as an object of class ID. Null, if no id was generated due to an unsure match result.
	 * 		<li> result: Result as an object of class MatchResult. 
	 * @throws WebApplicationException if called with an invalid token.
	 */
	private Map<String, Object> createNewPatient(
			String tokenId,
			MultivaluedMap<String, String> form) throws WebApplicationException {

		HashMap<String, Object> ret = new HashMap<String, Object>();
		// create a token if started in debug mode
		Token t;
		if (Config.instance.debugIsOn())
		{
			Session s = Servers.instance.newSession();
			t = new Token(null, "addPatient");
			Servers.instance.registerToken(s.getId(), t);
			tokenId = t.getId();
		} else {
			t = Servers.instance.getTokenByTid(tokenId);
		}

		List<ID> returnIds = new LinkedList<ID>();
		MatchResult match;
		// synchronize on token 
		if (t == null) {
			String infoLog = "Received ID request with invalid token. Token-ID: " + tokenId;
			logger.info(infoLog);
			throw new WebApplicationException(Response
					.status(Status.UNAUTHORIZED)
					.entity("Please supply a valid 'addPatient' token.")
					.build());
		}
		synchronized (t) {
			/* Get token again and check if it still exist.
			 * This prevents the following race condition:
			 *  1. Thread A gets token t and enters synchronized block
			 *  2. Thread B also gets token t, now waits for A to exit the synchronized block
			 *  3. Thread A deletes t and exits synchronized block
			 *  4. Thread B enters synchronized block with invalid token
			 */
			t = Servers.instance.getTokenByTid(tokenId);
			// create a token if started in debug mode
			if (t == null && Config.instance.debugIsOn())
			{
				t = new Token("debug", "addPatient");
				t.setType("addPatient");
			}
			if(t == null || !t.getType().equals("addPatient")){
				String infoLog = "Received ID request with invalid token. Token with ID: " + tokenId;
				if(t == null)
					infoLog += " is unknown.";
				else
					infoLog += " has unexpected type: " + t.getType();
				logger.info(infoLog);
				throw new WebApplicationException(Response
					.status(Status.UNAUTHORIZED)
					.entity("Please supply a valid 'addPatient' token.")
					.build());
			}
			logger.info("Handling ID Request with token " + (t == null ? "(null)" : t.getId()));
			Patient p = new Patient();
			Map<String, Field<?>> chars = new HashMap<String, Field<?>>();
			
			// get fields transmitted from MDAT server
			if (t.getData().containsKey("fields")) {
					Map<String, ?> serverFields = t.getDataItemMap("fields");
					for (String key : serverFields.keySet()) {
						String value = serverFields.get(key).toString();
						// TODO check if a value is already present
						form.add(key, value);
					}
			}

			Validator.instance.validateForm(form);
			
			for(String s: Config.instance.getFieldKeys()){
				if (!form.containsKey(s)) {
					logger.error("Field " + s + " not found in input data!");
					throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Field " + s + " not found in input data!").build());
				}
				chars.put(s, Field.build(s, form.getFirst(s)));
			}
	
			p.setFields(chars);
			
			// Normalisierung, Transformation
			Patient pNormalized = Config.instance.getRecordTransformer().transform(p);
			pNormalized.setInputFields(chars);
			
			match = Config.instance.getMatcher().match(pNormalized, Persistor.instance.getPatients());
			Patient assignedPatient; // The "real" patient that is assigned (match result or new patient) 
			
			List<String> idTypes;
			// If a list of ID types is given in token, return these types
			if (t.getData().containsKey("idtypes"))				
					idTypes = (List<String>) t.getDataItemList("idtypes");
			else { // otherwise...
				// FIXME prüfen, ob ID-Typen definiert
				idTypes = new LinkedList<String>();
				// ...check for the deprecated way of requesting a single ID type
				if (t.getData().containsKey("idtype"))
					idTypes.add(t.getDataItemString("idtype"));
				else // if no information is given, use the default ID type
					idTypes.add(IDGeneratorFactory.instance.getDefaultIDType());
			}
			switch (match.getResultType())
			{
			case MATCH :
				for (String idType : idTypes)
					returnIds.add(match.getBestMatchedPatient().getOriginal().getId(idType));
				
				assignedPatient = match.getBestMatchedPatient();
				// log token to separate concurrent request in the log file
				logger.info("Found match with ID " + returnIds.get(0).getIdString() + " for ID request " + t.getId()); 
				break;
				
			case NON_MATCH :
			case POSSIBLE_MATCH :
				if (match.getResultType() == MatchResultType.POSSIBLE_MATCH 
				&& (form.getFirst("sureness") == null || !Boolean.parseBoolean(form.getFirst("sureness")))) {
					ret.put("id", null);
					ret.put("result", match);
					return ret;
				}
				Set<ID> newIds = IDGeneratorFactory.instance.generateIds();			
				pNormalized.setIds(newIds);
				
				for (String idType : idTypes) {
					ID thisID = pNormalized.getId(idType);
					returnIds.add(thisID);				
					logger.info("Created new ID " + thisID.getIdString() + " for ID request " + (t == null ? "(null)" : t.getId()));
				}
				if (match.getResultType() == MatchResultType.POSSIBLE_MATCH)
				{
					pNormalized.setTentative(true);
					for (ID thisId : returnIds)
						thisId.setTentative(true);
					logger.info("New ID " + returnIds.get(0).getIdString() + " is tentative. Found possible match with ID " + 
							match.getBestMatchedPatient().getId(IDGeneratorFactory.instance.getDefaultIDType()).getIdString());
				}
				assignedPatient = pNormalized;
				break;
		
			default :
				logger.error("Illegal match result: " + match.getResultType());
				throw new InternalErrorException();
			}
			
			logger.info("Weight of best match: " + match.getBestMatchedWeight());
			
			IDRequest request = new IDRequest(p.getFields(), idTypes, match, assignedPatient);
			
			ret.put("request", request);
			
			Persistor.instance.addIdRequest(request);
			
			if(t != null && ! Config.instance.debugIsOn())
				Servers.instance.deleteToken(t.getId());
		}
		// Callback aufrufen
		String callback = t.getDataItemString("callback");
		if (callback != null && callback.length() > 0)
		{
			try {
				logger.debug("Sending request to callback " + callback);
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost callbackReq = new HttpPost(callback);
				callbackReq.setHeader("Content-Type", MediaType.APPLICATION_JSON);
				
				// TODO: ID-Typ integrieren, z.B. idtype="pid", idstring="..."
				JSONObject reqBody = new JSONObject()
						.put("tokenId", t.getId())
						//FIXME mehrere IDs zurückgeben -> bricht API, die ILF mitgeteilt wurde
						.put("id", returnIds.get(0).getIdString());
//						.put("id", id.toJSON());
				
				String reqBodyJSON = reqBody.toString();
				StringEntity reqEntity = new StringEntity(reqBodyJSON);
				reqEntity.setContentType("application/json");
				callbackReq.setEntity(reqEntity);				
				HttpResponse response = httpClient.execute(callbackReq);
				StatusLine sline = response.getStatusLine();
				// Accept callback if OK, CREATED or ACCEPTED is returned
				if ((sline.getStatusCode() < 200) || sline.getStatusCode() > 202) {
					logger.error("Received invalid status form mdat callback: " + response.getStatusLine());
					throw new InternalErrorException("Request to callback failed!");
				}
						
				// TODO: Server-Antwort auslesen, Fehler abfangen.
			} catch (Exception e) {
				logger.error("Request to callback " + callback + "failed: ", e);
				throw new InternalErrorException("Request to callback failed!");
			}
		}
		ret.put("ids", returnIds);
		ret.put("result", match);
		return ret;
	}
	
	/**
	 * Interface for Temp-ID-Resolver
	 * 
	 * @param callback
	 * @param data
	 * @return
	 */
	@Path("/tempid")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveTempIds(
			@QueryParam("callback") String callback,
			@QueryParam("data") JSONObject data) {
		if (data.has("subjects")) {
			JSONObject subjects;
			JSONObject result = new JSONObject();
			try {
				subjects = data.getJSONObject("subjects");
				Iterator subjectIt = subjects.keys();
				while (subjectIt.hasNext()) {
					String subject = subjectIt.next().toString();
					JSONArray tempIds = subjects.getJSONArray(subject);
					for (int i = 0; i < tempIds.length(); i++) {
						String tempId = tempIds.getString(i);
						String value = resolveTempId(tempId, subject);
						JSONObject resultSubObject;
						if (!result.has(subject)) {
							resultSubObject = new JSONObject();
							result.putOpt(subject, resultSubObject);
						} else {
							resultSubObject = result.getJSONObject(subject);
						}
						resultSubObject.put(tempId, value);							
					}					
				}
				return Response.ok().entity(result.toString()).build();
			} catch (JSONException e) {
				throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build());
			} catch (NoSuchFieldException e) {
				throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build());
			}
		} else
			return Response.ok().build();
	}
	
	private String resolveTempId(String tempId, String subject) throws NoSuchFieldException {
		Patient p = getPatientByTempId(tempId);
		if (!p.getInputFields().containsKey(subject))
			throw new NoSuchFieldException("No subject " + subject + " for Temp-ID " + tempId);
		return p.getInputFields().get(subject).getValue().toString();
	}
	
	
	private Patient getPatientByTempId(String tid) throws UnauthorizedException {
		Token t = Servers.instance.getTokenByTid(tid);
		if (t == null || !t.getType().equals("readPatient")) {
			logger.info("Tried to access GET /patients/tempid/ with invalid token " + t);
			throw new UnauthorizedException();
		}
		// TODO: verallgemeinern für andere IDs
		String pidString = t.getDataItemString("id");
		return Persistor.instance.getPatient(IDGeneratorFactory.instance.getFactory("pid").buildId(pidString));		
	}
	
	/**
	 * Get patient via readPatient token
	 * @param tid
	 * @return
	 */
	@Path("/tokenId/{tid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPatientsToken(
			@PathParam("tid") String tid){
		logger.info("Reveived request to get patient with token " + tid);
		// Check if token exists and has the right type. 
		// Validity of token is checked upon creation
		Token t = Servers.instance.getTokenByTid(tid);
		t.checkTokenType("readPatients");
		List requests = t.getDataItemList("searchIds");
		
		JSONArray ret = new JSONArray();
		for (Object item : requests) {
			JSONObject thisPatient = new JSONObject();
			String idType;
			String idString;
			Map<String, String> thisSearchId = (Map<String, String>) item; 
			idType = thisSearchId.get("idType");
			idString = thisSearchId.get("idString");
			ID id = IDGeneratorFactory.instance.getFactory(idType).buildId(idString);
			Patient patient = Persistor.instance.getPatient(id);
			if (t.hasDataItem("fields")) {
				// get fields for output
				Map<String, String> outputFields = new HashMap<String, String>();
				List<String> fieldNames = (List<String>) t.getDataItemList("fields");
				for (String thisFieldName : fieldNames) {
					outputFields.put(thisFieldName, patient.getInputFields().get(thisFieldName).toString());
				}
				try {
					thisPatient.put("fields", outputFields);
				} catch (JSONException e) {
					logger.error("Error while transforming patient fields into JSON", e);
					throw new InternalErrorException("Error while transforming patient fields into JSON");
				}
			}
			
			if (t.hasDataItem("resultIds")) {
				try {
					List<String> idTypes = (List<String>) t.getDataItemList("resultIds");
					List<JSONObject> returnIds = new LinkedList<JSONObject>();
					for (String thisIdType : idTypes) {
						returnIds.add(patient.getId(thisIdType).toJSON());
					}
					thisPatient.put("ids", returnIds);
				} catch (JSONException e) {
					logger.error("Error while transforming patient ids into JSON", e);
					throw new InternalErrorException("Error while transforming patient ids into JSON");
				}			
			}
			
			ret.put(thisPatient);
		}
		
		return Response.ok().entity(ret).build();
	}
	
	@Path("/tempid/{tid}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPatientByTempId(
			@PathParam("tid") String tid,
			Patient p){
		//Hier keine Auth notwendig. Wenn tid existiert, ist der Nutzer dadurch autorisiert.
		//Charakteristika des Patients in DB mit TempID tid austauschen durch die von p
		logger.info("Received PUT /patients/tempid/" + tid);
		throw new NotImplementedException();
	}
}
