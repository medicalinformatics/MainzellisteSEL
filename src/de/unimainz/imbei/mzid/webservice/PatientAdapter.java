package de.unimainz.imbei.mzid.webservice;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unimainz.imbei.mzid.Person;
import de.unimainz.imbei.mzid.Session;

public class PatientAdapter extends XmlAdapter<PatientDto, Person>{

	@Override
	public PatientDto marshal(Person arg0) throws Exception {
		return new PatientDto(arg0);
	}

	@Override
	public Person unmarshal(PatientDto arg0) throws Exception {
		Person p = new Person(arg0.getId(), arg0.getCharacteristics());
		return p;
	}

}
