package de.unimainz.imbei.mzid.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class NotImplementedException extends WebApplicationException {
	private static String message = "Functionality not implemented yet.";
	
	public NotImplementedException() {
		super(Response.status(Status.SERVICE_UNAVAILABLE).entity(message).build());
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
