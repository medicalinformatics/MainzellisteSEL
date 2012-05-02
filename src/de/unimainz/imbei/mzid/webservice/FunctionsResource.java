package de.unimainz.imbei.mzid.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Access to functions added via pluggable modules.
 * 
 * @author Martin
 *
 */
@Path("/functions/{module}/.*")
public class FunctionsResource {
	@GET
	public Response delegate(
			@PathParam("module") String subMod){
		return Response
			.status(Status.SERVICE_UNAVAILABLE)
			.entity("Module " + subMod + " is unknown.")
			.build();
	}
}
