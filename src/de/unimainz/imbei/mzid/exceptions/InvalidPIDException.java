package de.unimainz.imbei.mzid.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class InvalidPIDException extends WebApplicationException {
	private static String message = "Invalid ID.";
	
	public InvalidPIDException() {
        super(Response.status(Status.BAD_REQUEST).entity(message).build());
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}