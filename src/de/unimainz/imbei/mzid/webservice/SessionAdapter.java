package de.unimainz.imbei.mzid.webservice;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unimainz.imbei.mzid.Session;

public class SessionAdapter extends XmlAdapter<SessionDto, Session>{
	@Override
	public SessionDto marshal(Session arg0) throws Exception {
		return new SessionDto(arg0);
	}

	@Override
	public Session unmarshal(SessionDto arg0) throws Exception {
		Session s = new Session(arg0.getId());
		s.putAll(arg0.getData());
		return s;
	}
}
