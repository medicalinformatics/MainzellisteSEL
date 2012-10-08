package de.unimainz.imbei.mzid.dto;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.exceptions.InternalErrorException;

public class PatientAdapter {

	@Id
	@GeneratedValue
	@JsonIgnore
	private int patientJpaId; // JPA

	@Basic
	private LinkedList<String> fieldNames = new LinkedList<String>();
	@Basic
	private LinkedList<String> fieldClasses = new LinkedList<String>();
	
	@Basic
	private LinkedList<String> fieldValues = new LinkedList<String>();
	
	public static PatientAdapter fromPatient(Patient p) {
		return new PatientAdapter(p);
	}
	
	public static String fieldsToString(Map<String, Field<?>> fields) {
		try {
			JSONObject fieldsJson = new JSONObject();
			for (String fieldName : fields.keySet())
			{
				Map<String, String> thisField = new HashMap<String, String>();
				thisField.put("class", fields.get(fieldName).getClass().getName());
				thisField.put("value", fields.get(fieldName).toString());
				fieldsJson.put(fieldName, thisField);
			}
			return fieldsJson.toString();
		} catch (JSONException e) {
			Logger.getLogger(PatientAdapter.class).error("Exception: ", e);
			throw new InternalErrorException();
		}
	}
	
	public static Map<String, Field<?>> stringToFields(String fieldsJsonString) {
		try {
			Map<String, Field<?>> fields = new HashMap<String, Field<?>>();
			JSONObject fieldsJson = new JSONObject(fieldsJsonString);
			Iterator it = fieldsJson.keys();
			while(it.hasNext()) {
				String fieldName = (String) it.next();
				JSONObject thisFieldJson = fieldsJson.getJSONObject(fieldName); 
				String fieldClass = thisFieldJson.getString("class");
				String fieldValue = thisFieldJson.getString("value");
				Field<?> thisField = (Field) Class.forName(fieldClass).newInstance();
				thisField.setValue(fieldValue);
				fields.put(fieldName, thisField);
			} 
			return fields;
		} catch (Exception e) {
			Logger.getLogger(PatientAdapter.class).error("Exception: ", e);
			throw new InternalErrorException();
		}
	}
	
	public Patient toPatient() {
		Patient p = new Patient();
		Map<String, Field<?>> fields = new HashMap<String, Field<?>>();
		Iterator<String> itName = fieldNames.iterator();
		Iterator<String> itClass = fieldClasses.iterator();
		Iterator<String> itValue = fieldValues.iterator();
		try {
			while (itName.hasNext()) {
				String fieldName = itName.next();
				String fieldClass = itClass.next();
				String fieldValue = itValue.next();
				Field<?> field = (Field<?>) Class.forName(fieldClass).newInstance();
				field.setValue(fieldValue);
				fields.put(fieldName, field);
			} 
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error("Internal Server Error:", e);
			throw new InternalErrorException();
		}
		p.setFields(fields);
		return p;
	}
	
	public PatientAdapter(Patient p) {
		this.patientJpaId = p.getPatientJpaId();
		Map<String, Field<?>> fields = p.getFields();
		for (String fieldName : fields.keySet()) {
			this.fieldNames.add(fieldName);
			Field<?> field = fields.get(fieldName);
			this.fieldClasses.add(field.getClass().getName());
			this.fieldValues.add(field.toString());
		}
	}
}
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