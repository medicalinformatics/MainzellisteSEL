package de.unimainz.imbei.mzid.webservice;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.unimainz.imbei.mzid.Config;

public class CharacteristicKeyParam extends AbstractParam<String> {
	public CharacteristicKeyParam(String s) {
		super(s);
	}
	
	@Override
	protected String parse(String s) throws Throwable {
		if(!Config.instance.getCharacteristicKeys().contains(s)){
			throw new WebApplicationException(Response
				.status(Status.BAD_REQUEST)
				.entity("There is no characteristic key called " + s + ".")
				.build()
			);
		}
		return s;
	}
}
