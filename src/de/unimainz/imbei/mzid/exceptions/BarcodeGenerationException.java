package de.unimainz.imbei.mzid.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class BarcodeGenerationException extends WebApplicationException {
	final static String message = "An internal error occured generating your barcode.";

	public BarcodeGenerationException() {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).build());
	}
}
