package de.unimainz.imbei.mzid.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Person;

public enum PersonDAO {
	instance; // http://electrotek.wordpress.com/2008/08/06/singleton-in-java-the-proper-way/
	
	private HashMap<PID, Person> persons = new HashMap<PID, Person>();
	
	Person getPerson(PID id){
		return persons.get(id);
	}
	
	void putPerson(Person p){
		persons.put(p.getId(), p);
	}
	
	List<PID> getPIDList(){
		return new ArrayList<PID>(persons.keySet());
	}
	
	List<Person> getPersonList(){
		return new ArrayList<Person>(persons.values());
	}
}
