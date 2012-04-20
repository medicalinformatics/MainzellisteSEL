package de.unimainz.imbei.mzid;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {
	private PID id;
	private Map<String, Characteristic> characteristics;
	
	public Person() {}
	
	public Person(PID id, Map<String, Characteristic> c) {
		this.id = id;
		this.characteristics = c;
	}

	public PID getId() {
		return id;
	}
	
	public void setId(PID id) {
		this.id = id;
	}
	
	public Map<String, Characteristic> getCharacteristics() {
		return characteristics;
	}
	
	public void setCharacteristics(Map<String, Characteristic> characteristics) {
		this.characteristics = characteristics;
	}
	
	@Override
	public String toString() {
		return id.toString() + characteristics.toString();
	}
}