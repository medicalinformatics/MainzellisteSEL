package de.unimainz.imbei.mzid.webservice;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class PersonUnknownException extends WebApplicationException {
	private static String message = "Person unknown.";
	
	public PersonUnknownException() {
        super(Response.status(Status.NOT_FOUND).entity(message).build());
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
