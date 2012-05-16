package de.unimainz.imbei.mzid.dto;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unimainz.imbei.mzid.ID;
import de.unimainz.imbei.mzid.Patient;

/*public class PatientAdapter extends XmlAdapter<PatientDto, Patient>{

	@Override
	public PatientDto marshal(Patient arg0) {
		PatientDto d = new PatientDto();
		IDAdapter ida = new IDAdapter();
		Set<IDDto> iddtos = new HashSet<IDDto>();
		if(arg0.getIds() != null){
			for(ID id: arg0.getIds()){
				iddtos.add(ida.marshal(id));
			}
		}
		d.setFields(arg0.getFields());
		d.setIds(iddtos);
		d.setIntPatId(arg0.getIntPatId());
		return d;
	}

	@Override
	public Patient unmarshal(PatientDto d) {
		Set<ID> ids = new HashSet<ID>();
		IDAdapter ida = new IDAdapter();
		if(d.getIds() != null){
			for(IDDto iddto: d.getIds()){
				ids.add(ida.unmarshal(iddto));
			}
		}
		return new Patient(d.getIntPatId(), ids, d.getFields());
	}

}
*/