package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.view.Viewable;

import de.unimainz.imbei.mzid.Config;

@Path("/html")
public class HTMLResource {

	@GET
	@Path("createPatient")
	@Produces(MediaType.TEXT_HTML)
	public Response createPatient(
			@QueryParam("tokenId") String tokenId,
			@QueryParam("callback") String callback){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tokenId", tokenId);
		map.put("callback", callback);
		map.put("adminPhone", Config.instance.getProperty("adminPhone"));
		return Response.ok(new Viewable("/createPatient.jsp", map)).build();
	}
}
