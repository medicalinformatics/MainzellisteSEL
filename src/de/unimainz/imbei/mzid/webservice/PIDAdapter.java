package de.unimainz.imbei.mzid.webservice;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unimainz.imbei.mzid.PID;

public class PIDAdapter extends XmlAdapter<PIDDto, PID>{
	@Override
	public PIDDto marshal(PID v) throws Exception {
		return new PIDDto(v.toString());
	}

	@Override
	public PID unmarshal(PIDDto v) throws Exception {
		return new PID(v.getPid());
	}
}
