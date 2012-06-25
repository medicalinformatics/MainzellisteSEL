package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;

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
import de.unimainz.imbei.mzid.Servers;

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
}
