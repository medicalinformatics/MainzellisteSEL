package de.unimainz.imbei.mzid.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class UnauthorizedException extends WebApplicationException {
	private static String message = "Unauthorized to access resource.";
	
	public UnauthorizedException() {
		super(Response.status(Status.UNAUTHORIZED).entity(message).build());
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
