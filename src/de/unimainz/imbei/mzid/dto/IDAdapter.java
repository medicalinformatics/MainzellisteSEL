package de.unimainz.imbei.mzid.dto;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.ID;

public class IDAdapter extends XmlAdapter<IDDto, ID>{

	@Override
	public IDDto marshal(ID v) {
		return new IDDto(v.getId(), v.getType());
	}

	@Override
	public ID unmarshal(IDDto v) {
		return Config.instance.getFactory(v.getType()).buildId(v.getId(), v.getType());
	}
}
