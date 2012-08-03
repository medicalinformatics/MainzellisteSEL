package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;

public class TokenParam extends AbstractParam<Token> {

	public TokenParam(String param) throws WebApplicationException {
		super(param);
	}

	@Override
	protected Token parse(String param) throws WebApplicationException {
		Token t = new Token();
		try {
			JSONObject jsob = new JSONObject(param);
			if(!("".equals(jsob.optString("id"))))
				t.setId(jsob.getString("id"));
			t.setType(jsob.getString("type"));
			HashMap<String, String> data = new ObjectMapper().readValue(jsob.getString("data"), HashMap.class);
			t.setData(data);
		} catch (Exception e) {
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity("Invalid input: " + e.getMessage())
					.build()
				);
		}
		
		return t;
	}

}
