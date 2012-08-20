package de.unimainz.imbei.mzid.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class InternalErrorException extends WebApplicationException {
	private static String message = "Internal server error.";
	
	public InternalErrorException() {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).build());
	}
	
	public InternalErrorException(Throwable cause) {
		super(cause, Status.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
