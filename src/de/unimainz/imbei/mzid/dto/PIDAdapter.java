/*package de.unimainz.imbei.mzid.dto;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unimainz.imbei.mzid.PID;

public class PIDAdapter extends XmlAdapter<IDDto, PID>{
	@Override
	public IDDto marshal(PID p) throws Exception {
		return new IDDto(p.getId(), p.getType());
	}

	@Override
	public PID unmarshal(IDDto i) throws Exception {
		return new PID(i.getId(), i.getType());
	}
}
*/