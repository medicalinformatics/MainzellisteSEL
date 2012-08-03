package de.unimainz.imbei.mzid.webservice;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.unimainz.imbei.mzid.Config;

public class FieldKeyParam extends AbstractParam<String> {
	public FieldKeyParam(String s) {
		super(s);
	}
	
	@Override
	protected String parse(String s) throws WebApplicationException {
		if(!Config.instance.getFieldKeys().contains(s)){
			throw new WebApplicationException(Response
				.status(Status.BAD_REQUEST)
				.entity("There is no Field key called " + s + ".")
				.build()
			);
		}
		return s;
	}
}
