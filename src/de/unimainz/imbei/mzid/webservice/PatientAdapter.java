package de.unimainz.imbei.mzid.webservice;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.Session;

public class PatientAdapter extends XmlAdapter<PatientDto, Patient>{

	@Override
	public PatientDto marshal(Patient arg0) throws Exception {
		return new PatientDto(arg0);
	}

	@Override
	public Patient unmarshal(PatientDto arg0) throws Exception {
		Patient p = new Patient(arg0.getId(), arg0.getFields());
		return p;
	}

}
