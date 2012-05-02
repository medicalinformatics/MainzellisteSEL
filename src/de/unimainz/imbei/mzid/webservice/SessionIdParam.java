package de.unimainz.imbei.mzid.webservice;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.Session;

public class SessionIdParam extends AbstractParam<Session> {
	public SessionIdParam(String s) {
		super(s);
	}
	
	@Override
	protected Session parse(String sid) throws Throwable {
		Session s = Config.instance.getSession(sid);
		if(s == null) {
			throw new WebApplicationException(Response
				.status(Status.NOT_FOUND)
				.entity("Session-ID " + sid + " unknown.")
				.build()
			);
		}
		return s;
	}
}
