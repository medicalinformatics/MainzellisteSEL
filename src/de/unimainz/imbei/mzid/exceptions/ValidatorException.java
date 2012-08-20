package de.unimainz.imbei.mzid.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ValidatorException extends WebApplicationException {

	public ValidatorException(String message) {
        super(Response.status(Status.BAD_REQUEST).entity(message).build());		
	}
}
