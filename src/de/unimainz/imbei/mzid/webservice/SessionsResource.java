package de.unimainz.imbei.mzid.webservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.Servers;
import de.unimainz.imbei.mzid.Session;
import de.unimainz.imbei.mzid.dto.Persistor;

/**
 * Resource-based access to server-side client sessions.
 * A server-side client session is a set of key-value pairs about a given client session
 * shared between mzid and an xDAT server. Apart from listing and creating sessions, 
 * knowing the session ID is deemed authentication for session access.
 * 
 * @author Martin Lablans
 */
@Path("/sessions")
public class SessionsResource {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray getSessionIds(@Context HttpServletRequest req){
		
		logger.info("Request to list sessions received by host " + req.getRemoteHost());
		
		//TODO: Auth: IDAT-Admin (sieht alle Sessions) oder MDAT-Server (sieht seine eigenen).
		Servers.instance.checkPermission(req, "showSessionIds");
		
		JSONArray ret = new JSONArray();
		for(String s: Servers.instance.getSessionIds()){
			URI u = UriBuilder
				.fromUri(req.getRequestURL().toString())
				.path("{sid}")
				.build(s);
			ret.put(u.toString());
		}
		
		return ret;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response newSession(@Context HttpServletRequest req) throws ServletException, JSONException{
		
		logger.info("Request to create session received by host " + req.getRemoteHost());
		
		Servers.instance.checkPermission(req, "createSession");
		String sid = Servers.instance.newSession().getId();
		
		URI newUri = UriBuilder
				.fromUri(req.getRequestURL().toString())
				.path("{sid}")
				.build(sid);
		
		logger.info("Created session " + sid);
		
		JSONObject ret = new JSONObject()
				.put("sessionId", sid)
				.put("uri", newUri);
		
		return Response
			.status(Status.CREATED)
			.entity(ret)
			.location(newUri)
			.build();
	}
	
/*
 * For future use
 */
//	@Path("/{session}")
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public Session getSession(
//			@PathParam("session") SessionIdParam sid){
//		// No authentication other than knowing the session id.
//		Session s = sid.getValue();
//		synchronized (s) { // refresh
//			//TODO: Besitzt der MDAT diese Sitzung?
//			return s;
//		}
//	}
//	
//	@Path("/{session}")
//	@POST
//	@Consumes({MediaType.APPLICATION_JSON})
//	@Produces({MediaType.APPLICATION_JSON})
//	public Session updateSession(
//			@PathParam("session") SessionIdParam sid,
//			Session sNew){
//		// No authentication other than knowing the session id.
//		Session s = sid.getValue();
//		synchronized (s) { // refresh
//			//TODO: Besitzt der MDAT diese Sitzung?
//			s.putAll(sNew);
//			return s;
//		}
//	}

	
	@Path("/{session}")
	@DELETE
	public Response deleteSession(
			@PathParam("session") String sid,
			@Context HttpServletRequest req){
		// No authentication other than knowing the session id.
		logger.info("Received request to delete session " + sid + " from host " +
				req.getRemoteHost());
		Servers.instance.deleteSession(sid);
		logger.info("Deleted session " + sid);
		return Response
			.status(Status.OK)
			.build();
	}
	
	@Path("/{session}/tokens")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Token> getTokens(
			@PathParam("session") SessionIdParam sid,
			@Context HttpServletRequest req){
		logger.info("Received request to list tokens for session " + sid + " from host " + 
			req.getRemoteHost());
		return Servers.instance.getAllTokens(sid.getValue().getId());
	}
	
	@Path("/{session}/tokens")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newToken(
			@Context HttpServletRequest req,
			@PathParam("session") SessionIdParam sid,
			String tp) throws JSONException {
		
		Session s = sid.getValue();
		
		logger.info("Received request to create token for session " + s.getId() + " by host " + 
				req.getRemoteHost() + "\n" +
				"Received data: " + tp);
		
		Token t = new TokenParam(tp).getValue();
		
		if(t.getType() == null) {
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity("Token type must not be empty.")
					.build());
		} else {
			Servers.instance.checkPermission(req, "createToken");
			Servers.instance.checkPermission(req, "tt_" + t.getType());
		}
		
		// Pr�fe Callback-URL
		String callback = t.getDataItemString("callback");
		if (callback != null && !callback.equals("")) {
			if (!Pattern.matches(Config.instance.getProperty("callback.allowedFormat"), callback)) {
				throw new WebApplicationException(Response
						.status(Status.BAD_REQUEST)
						.entity("Callback address " + callback + " does not conform to allowed format.")
						.build()); 
			}
			try {
				URI callbackURI = new URI(callback);
			} catch (URISyntaxException e) {
				throw new WebApplicationException(Response
						.status(Status.BAD_REQUEST)
						.entity("Callback address " + callback + " is not a valid URI.")
						.build());
			}
		}
				
		
		// Prüfe Existenz der ID bei Typ "readPatient"
		if (t.getType() == "readPatient") {
			String idString = t.getDataItemString("id");
			// TODO andere ID-Typen
			Patient p = Persistor.instance.getPatient(new PID(idString, "pid"));
			if (p == null) {
				throw new WebApplicationException(Response
						.status(Status.BAD_REQUEST)
						.entity("No patient with id '" + idString + "'.")
						.build());
			}
		}

		//Token erstellen, speichern und URL zur�ckgeben
		Token t2 = Servers.instance.newToken(s.getId(), t.getType());
		t2.setData(t.getData());
		
		URI newUri = UriBuilder
				.fromUri(req.getRequestURL().toString())
				.path("/{tid}")
				.build(t2.getId());
		
		JSONObject ret = new JSONObject()
				.put("tokenId", t2.getId())
				.put("uri", newUri);
		
		logger.info("Created token of type " + t2.getType() + " with id " + t2.getId() + 
				" in session " + s.getId() + "\n" +
				"Returned data: " + ret);

		return Response
			.status(Status.CREATED)
			.location(newUri)
			.entity(ret)
			.build();
	}
	
	@Path("/{session}/tokens/{tokenid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Token getSingleToken(
			@PathParam("session") SessionIdParam sid,
			@PathParam("tokenid") String tokenId,
			@Context HttpServletRequest req){

		Session s = sid.getValue();
		Token t = Servers.instance.getTokenByTid(tokenId); 

		// Nicht jeder, der eine Token-Id hat, sollte das Token lesen können,
		// insbesondere bei Temp-Ids ("readPatient"): Token enthält echte ID
		Servers.instance.checkPermission(req, "tt_" + t.getType());

		// Check that token exists and belongs to specified session
		if (t == null || !s.getTokens().contains(t))
			throw new WebApplicationException(Response
					.status(Status.NOT_FOUND)
					.entity("No token with id " + tokenId + " in session " + sid + ".")
					.build());		
		logger.info("Received request to get token " + tokenId + " in session " + sid +
				" by host " + req.getRemoteHost());
		return t;
	}
}
