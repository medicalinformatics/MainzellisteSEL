package de.unimainz.imbei.mzid.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class CircularDuplicateRelationException extends WebApplicationException {
	
	String message;
	
	public CircularDuplicateRelationException(String duplicatePid, String originalPid) {
        super(Response.status(Status.BAD_REQUEST).entity(
        		"Cannot set " + duplicatePid + " to be a duplicate of " + originalPid +
				" because " + originalPid + " is itself a duplicate of " + duplicatePid 
        		).build());		
        
        this.message = "Cannot set " + duplicatePid + " to be a duplicate of " + originalPid +
				" because " + originalPid + " is itself a duplicate of " + duplicatePid;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
