package de.unimainz.imbei.mzid.dto;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unimainz.imbei.mzid.ID;
import de.unimainz.imbei.mzid.IDGeneratorFactory;

public class IDAdapter extends XmlAdapter<IDDto, ID>{

	@Override
	public IDDto marshal(ID v) {
		return new IDDto(v.getIdString(), v.getType());
	}

	@Override
	public ID unmarshal(IDDto v) {
		return IDGeneratorFactory.instance.getFactory(v.getType()).buildId(v.getId());
	}
}
