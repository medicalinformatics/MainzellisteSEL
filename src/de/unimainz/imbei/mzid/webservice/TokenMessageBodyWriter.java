package de.unimainz.imbei.mzid.webservice;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.spi.resource.Singleton;


@Provider
@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class TokenMessageBodyWriter implements
		MessageBodyWriter<de.unimainz.imbei.mzid.webservice.Token> {

	@Override
	public long getSize(Token arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
			MediaType arg3) {
		return arg0 == Token.class && arg3.isCompatible(MediaType.APPLICATION_JSON_TYPE);
	}

	@Override
	public void writeTo(Token arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4,
			MultivaluedMap<String, Object> arg5, OutputStream arg6)
			throws IOException, WebApplicationException {
		
		JSONObject ret = new JSONObject();
		
		try {
			ret.put("tokenId", arg0.getId());
			ret.put("type", arg0.getType());
			ret.put("data", new JSONObject(arg0.getData()));			
		} catch (JSONException e) {
			throw new WebApplicationException();
		}
		arg6.write(ret.toString().getBytes());
	}
}
