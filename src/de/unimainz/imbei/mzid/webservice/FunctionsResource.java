package de.unimainz.imbei.mzid.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Access to functions added via pluggable modules.
 * 
 * @author Martin Lablans
 *
 */
@Path("/functions")
public class FunctionsResource {
	
	@Path("/barcode")
	public BarcodeResource barcode(){
		return new BarcodeResource();
	}
	
	@GET
	@Path("/{module}")
	public Response delegate(
			@PathParam("module") String subMod){
		
		return Response
			.status(Status.NOT_FOUND)
			.entity("Module " + subMod + " is unknown.")
			.build();
	}
}
