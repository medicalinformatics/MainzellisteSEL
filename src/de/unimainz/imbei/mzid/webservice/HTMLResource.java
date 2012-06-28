package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.sun.jersey.api.view.Viewable;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.Servers;
import de.unimainz.imbei.mzid.dto.Persistor;

@Path("/html")
public class HTMLResource {

	Logger logger = Logger.getLogger(HTMLResource.class);
	
	@GET
	@Path("createPatient")
	@Produces(MediaType.TEXT_HTML)
	public Response createPatient(
			@QueryParam("tokenId") String tokenId,
			@QueryParam("callback") String callback){
		Token t = Servers.instance.getTokenByTid(tokenId);
		if (Config.instance.debugIsOn() ||
				(t != null && t.getType().equals("addPatient")))
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("tokenId", tokenId);
			map.put("callback", callback);
			map.put("adminPhone", Config.instance.getProperty("adminPhone"));
			return Response.ok(new Viewable("/createPatient.jsp", map)).build();
		} else throw new WebApplicationException(Response
				.status(Status.UNAUTHORIZED)
				.entity("Please supply a valid token id as URL parameter 'tokenId'.")
				.build());
	}
	
	@GET
	@Path("/admin/editPatient")
	@Produces(MediaType.TEXT_HTML)
	public Response editPatient(
			@QueryParam("id") String pidString
			) {
		// Authentication by Tomcat
		if (pidString == null || pidString.length() == 0)
			return Response.ok(new Viewable("/selectPatient.jsp")).build();

		/* TODO: Nach ID-Typ differenzieren (Typ als Parameter mitgeben)
		 	hier dann etwa folgendes:
			Class<? extends ID> idClass = IDGeneratorFactory.instance.getIDClass(Config.instance.getProperty("))
			// Instanz von idClass erzeugen...
			*/ 
		PID pid = new PID(pidString, "pid");
		Patient p = Persistor.instance.getPatient(pid);

		if (p == null)
			throw new WebApplicationException(Response
					.status(Status.NOT_FOUND)
					.entity("Found no patient with PID " + pidString + ".")
					.build());

		Map <String, Object> map = new HashMap<String, Object>();
		map.put("fields", p.getFields());
		map.put("id", pid.getIdString());

		return Response.ok(new Viewable("/editPatient.jsp", map)).build();
	}

}
