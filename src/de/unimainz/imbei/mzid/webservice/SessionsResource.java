package de.unimainz.imbei.mzid.webservice;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.Session;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;

/**
 * Resource-based access to server-server sessions.
 * 
 * @author Martin
 *
 */
@Path("/sessions")
public class SessionsResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> getSessions(){
		//Auth: IDAT-Admin (sieht alle Sessions) oder MDAT-Server (sieht seine eigenen).
		throw new NotImplementedException();
	}
	
	@POST
	public Response newSession(){
		String sid = Config.instance.newSession().getId();
		URI uri = UriBuilder
			.fromPath("{sid}")
			.build(sid);
		return Response
			.status(Status.CREATED)
			.location(uri)
			.build();
	}
	
	@Path("/{session}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Session getSession(
			@PathParam("session") SessionIdParam sid){
		Session s = sid.getValue();
		synchronized (s) {
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
		Session s = sid.getValue();
		synchronized (s) {
			//TODO: Besitzt der MDAT diese Sitzung?
			s.putAll(sNew);
			return s;
		}
	}
	
	@Path("/{session}")
	@DELETE
	public Response deleteSession(
			@PathParam("session") String sid){
		Config.instance.deleteSession(sid);
		return Response
			.status(Status.OK)
			.build();
	}
	
	@Path("/{session}/tokens")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getTokens(
			@PathParam("session") SessionIdParam sid){
		Session s = sid.getValue();
		throw new NotImplementedException();
	}
	
	@Path("/{session}/tokens")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response newToken(
			@PathParam("session") SessionIdParam sid,
			Token t){
		//Token erstellen, speichern und URL zurückgeben
		throw new NotImplementedException();
	}
}
