package de.unimainz.imbei.mzid.webservice;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import de.unimainz.imbei.mzid.Servers;
import de.unimainz.imbei.mzid.Session;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;

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
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<URI> getSessionIds(@Context HttpServletRequest req){
		//TODO: Auth: IDAT-Admin (sieht alle Sessions) oder MDAT-Server (sieht seine eigenen).
		Servers.instance.checkPermission(req, "showSessionIds");
		
		Set<URI> ret = new HashSet<URI>();
		for(String s: Servers.instance.getSessionIds()){
			URI u = UriBuilder
				.fromResource(SessionsResource.class)
				.path("{sid}")
				.build(s);
			ret.add(u);
		}
		
		return ret;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public URI newSession(@Context HttpServletRequest req) throws ServletException{
		Servers.instance.checkPermission(req, "createSession");
		String sid = Servers.instance.newSession().getId();
		return UriBuilder
			.fromResource(SessionsResource.class)
			.path("{sid}")
			.build(sid);
	}
	
/*
 * For future use
 *
	@Path("/{session}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Session getSession(
			@PathParam("session") SessionIdParam sid){
		// No authentication other than knowing the session id.
		Session s = sid.getValue();
		synchronized (s) { // refresh
			//TODO: Besitzt der MDAT diese Sitzung?
			return s;
		}
	}
	
	@Path("/{session}")
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Session updateSession(
			@PathParam("session") SessionIdParam sid,
			Session sNew){
		// No authentication other than knowing the session id.
		Session s = sid.getValue();
		synchronized (s) { // refresh
			//TODO: Besitzt der MDAT diese Sitzung?
			s.putAll(sNew);
			return s;
		}
	}
*/
	
	@Path("/{session}")
	@DELETE
	public Response deleteSession(
			@PathParam("session") String sid){
		// No authentication other than knowing the session id.
		Servers.instance.deleteSession(sid);
		return Response
			.status(Status.OK)
			.build();
	}
	
	@Path("/{session}/tokens")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Token> getTokens(
			@PathParam("session") SessionIdParam sid){
		return Servers.instance.getAllTokens(sid.getValue().getId());
	}
	
	@Path("/{session}/tokens")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newToken(
			@PathParam("session") SessionIdParam sid,
			Token t){
		//Token erstellen, speichern und URL zurückgeben
		Session s = sid.getValue();
		
		Token t2 = Servers.instance.newToken(s.getId());
		t2.setData(t.getData());
		t2.setType(t.getType());
		
		return Response
			.status(Status.CREATED)
			.entity(UriBuilder
				.fromResource(SessionsResource.class)
				.path("/tokens/{tid}")
				.build(t2.getId()))
			.build();
	}
	
	@Path("/{session}/tokens/{tokenid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Token getSingleToken(
			@PathParam("session") SessionIdParam sid,
			@PathParam("tokenid") String tokenId){
		// TODO: double-check that the token is indeed part of session sid
		return Servers.instance.getTokenByTid(tokenId);
	}
}
