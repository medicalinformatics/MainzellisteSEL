package de.unimainz.imbei.mzid.webservice;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.view.Viewable;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.ID;
import de.unimainz.imbei.mzid.IDGeneratorFactory;
import de.unimainz.imbei.mzid.IDRequest;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.Servers;
import de.unimainz.imbei.mzid.dto.Persistor;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;
import de.unimainz.imbei.mzid.exceptions.UnauthorizedException;
import de.unimainz.imbei.mzid.matcher.MatchResult;
import de.unimainz.imbei.mzid.matcher.MatchResult.MatchResultType;

/**
 * Resource-based access to patients.
 * 
 * @author Martin
 *
 */
@Path("/patients")
public class PatientsResource {
	
	private Logger logger = Logger.getLogger(PatientsResource.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Patient> getAllPatients(@Context HttpServletRequest req) throws UnauthorizedException {
		/* Benutzerrechte prüfen, basierend auf Rollenzuweisung in tomcat-users.xml.
		 * Zusätzliche Prüfung via security-constraint in web.xml 
		 */
		logger.info("Received GET /patients");
		if (!req.isUserInRole("admin"))
			throw new UnauthorizedException();
		
		throw new NotImplementedException();
		// FIXME
		
		//2. Jeden Patienten aus der DB laden. Die müssen vom EntityManager abgekoppelt sein und nur Felder führen, die IDs sind.
	
		//3. Patienten in Liste zurückgeben.
		/*return Persistor.instance.getPatients();*/
	}
	

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response newPatientBrowser(
			@QueryParam("tokenId") String tokenId,
			MultivaluedMap<String, String> form){
		Map createRet = createNewPatient(tokenId, form); 
		ID id = (ID) createRet.get("id");
		MatchResult result = (MatchResult) createRet.get("result");
		Map <String, Object> map = new HashMap<String, Object>();
		if (id == null) {
			// Copy form to JSP model so that input is redisplayed
			for (String key : form.keySet())
			{
				map.put(key, form.getFirst(key));
			}
			
			map.put("readonly", "true");
			return Response.status(Status.ACCEPTED)
					.entity(new Viewable("/unsureMatch.jsp", map)).build();
		} else {
			map.put("id", id.getIdString());
			map.put("tentative", id.isTentative());
			
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
	
	@POST //FIXME Problem im IE; der landet immer hier drin!
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newPatientJson(
			@QueryParam("tokenId") String tokenId,
			@Context HttpServletRequest request,
			@Context UriInfo context,
			MultivaluedMap<String, String> form) throws JSONException {
		Map responseMap = createNewPatient(tokenId, form);
		logger.info("Accept: " + request.getHeader("Accept"));
		logger.info("Content-Type: " + request.getHeader("Content-Type"));
		ID newId = (ID) responseMap.get("id");
		MatchResult result = (MatchResult) responseMap.get("result");
		
		URI newUri = context.getBaseUriBuilder()
				.path(PatientsResource.class)
				.path("/{idtype}/{idvalue}")
				.build(newId.getType(), newId.getIdString());
		
		JSONObject ret = new JSONObject()
				.put("newId", newId.getIdString())
				.put("tentative", newId.isTentative())
				.put("uri", newUri);
				
		if (Config.instance.debugIsOn())
			ret.put("max_weight", result.getBestMatchedWeight());
		
		return Response
			.status(Status.CREATED)
			.entity(ret)
			.location(newUri)
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
	 * @return
	 * @throws WebApplicationException if called with an invalid token.
	 */
	private Map createNewPatient(
			String tokenId,
			MultivaluedMap<String, String> form) throws WebApplicationException {

		//Validator.instance.validateForm(form);
		HashMap ret = new HashMap();
		Token t = Servers.instance.getTokenByTid(tokenId);
		// create a token if started in debug mode
		if (t == null && Config.instance.debugIsOn())
		{
			t = new Token("debug");
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
		
		for(String s: Config.instance.getFieldKeys()){ //TODO: Testfall mit defekten/leeren Eingaben
			chars.put(s, Field.build(s, form.getFirst(s)));
		}

		p.setFields(chars);
		
		// Normalisierung, Transformation
		Patient pNormalized = Config.instance.getRecordTransformer().transform(p);
		pNormalized.setInputFields(chars);
		
		MatchResult match = Config.instance.getMatcher().match(pNormalized, Persistor.instance.getPatients());
		
		ID id;
		Patient assignedPatient; // The "real" patient that is assigned (match result or new patient) 
		
		switch (match.getResultType())
		{
		case MATCH :
			id = match.getBestMatchedPatient().getOriginal().getId("pid");
			assignedPatient = match.getBestMatchedPatient();
			// log token to separate concurrent request in the log file
			logger.info("Found match with ID " + id.getIdString() + " for ID request " + t.getId()); 
			break;
			
		case NON_MATCH :
		case POSSIBLE_MATCH :
			if (match.getResultType() == MatchResultType.POSSIBLE_MATCH 
			&& (form.getFirst("sureness") == null || !Boolean.parseBoolean(form.getFirst("sureness")))) {
				ret.put("id", null);
				ret.put("result", match);
				return ret;
			}
			id = IDGeneratorFactory.instance.getFactory("pid").getNext(); //TODO: generalisieren			
			Set<ID> ids = new HashSet<ID>();
			ids.add(id);
			pNormalized.setIds(ids);
			logger.info("Created new ID " + id.getIdString() + " for ID request " + (t == null ? "(null)" : t.getId()));
			if (match.getResultType() == MatchResultType.POSSIBLE_MATCH)
			{
				pNormalized.setTentative(true);
				id.setTentative(true);
				logger.info("New ID " + id.getIdString() + " is tentative. Found possible match with ID " + 
						match.getBestMatchedPatient().getId("pid").getIdString());
			}
			assignedPatient = pNormalized;
			break;
	
		default :
			// TODO
			return null;
		}
		
		logger.info("Weight of best match: " + match.getBestMatchedWeight());
		
		IDRequest request = new IDRequest(p.getFields(), "pid", match, assignedPatient);
		
		Persistor.instance.addIdRequest(request);
		
		if(t != null && !t.getId().equals("4223"))
			Servers.instance.deleteToken(t.getId());
		
		// Callback aufrufen
		// TODO auslagern in Funktion. Wohin?
		// TODO Fehlerbehebung
		String callback = t.getDataItem("callback");
		if (callback != null && callback.length() > 0)
		{
			try {
				logger.debug("Sending request to callback " + callback);
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost callbackReq = new HttpPost(callback);
				callbackReq.setHeader("Content-Type", MediaType.APPLICATION_JSON);
				
				JSONObject reqBody = new JSONObject()
						.put("tokenId", t.getId())
						.put("id", id);
				
				String reqBodyJSON = reqBody.toString();
				StringEntity reqEntity = new StringEntity(reqBodyJSON);
				reqEntity.setContentType("application/json");
				callbackReq.setEntity(reqEntity);				
				httpClient.execute(callbackReq);
				// TODO: Server-Antwort auslesen, Fehler abfangen.
			} catch (Exception e) {
				logger.error("Request to callback " + callback + "failed: ", e);
				throw new WebApplicationException(Response
						.status(Status.INTERNAL_SERVER_ERROR)
						.entity("Request to callback failed!")
						.build());
			}
		}
		ret.put("id", id);
		ret.put("result", match);
		return ret;
	}
	
	@Path("/pid/{pid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPatientViaPid(
			@PathParam("pid") String pidString){
		logger.info("Received GET /patients/pid/" + pidString);
		throw new NotImplementedException();
		//FIXME IDAT-Admin?
/*		PID pid = (PID) IDGeneratorFactory.instance.getFactory("pid").buildId(pidString);
		Patient pat = Persistor.instance.getPatient(pid);
		if(pat == null){
			throw new WebApplicationException(Response
					.status(Status.NOT_FOUND)
					.entity("There is no patient with PID " + pid + ".")
					.build());
		} else {
			return Response.status(Status.OK).entity(pat).build();
		}*/
	}
	
	@Path("/pid/{pid}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setPatientByPid(
			@PathParam("pid") String pid,
			Patient p){
		throw new NotImplementedException();
		//FIXME IDAT-Admin?
		/*Persistor.instance.updatePatient(p);
		return Response
				.status(Status.NO_CONTENT)
				.build();*/
	}
	
	@Path("/tempid/{tid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Patient getPatient(
			@PathParam("tid") String tid){
		//Hier keine Auth notwendig. Wenn tid existiert, ist der Nutzer dadurch autorisiert.
		//Patient mit TempID tid zurückgeben
		logger.info("Received GET /patients/tempid/" + tid);
		throw new NotImplementedException();
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
