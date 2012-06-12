package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.view.Viewable;

@Path("/html")
public class HTMLResource {

	@GET
	@Path("createPerson")
	@Produces(MediaType.TEXT_HTML)
	public Response createPatient(
			@QueryParam("tokenId") String tokenId){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tokenId", tokenId);
		return Response.ok(new Viewable("/createPerson.jsp", map)).build();
	}
}
