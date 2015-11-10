package de.pseudonymisierung.mainzelliste.webservice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;

import de.pseudonymisierung.mainzelliste.Config;
import de.pseudonymisierung.mainzelliste.Servers;

/**
 * Resource for querying configuration parameters via the REST interface. This resource is for internal use in the OSSE
 * registry system (http://www.osse-register.de) and subject to change.
 */
@Path("/configuration")
public class ConfigResource {

	/**
	 * Get field keys as an array of strings.
	 * 
	 * @param request
	 *            The injected HttpSerlvetRequest
	 * 
	 * @return Field keys as an array of strings.
	 */
	@Path("/fieldKeys")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFieldKeys(@Context HttpServletRequest request) {
		Servers.instance.checkPermission(request, "readConfiguration");
		JSONArray ret = new JSONArray(Config.instance.getFieldKeys());
		return Response.ok(ret).build();
	}
}
