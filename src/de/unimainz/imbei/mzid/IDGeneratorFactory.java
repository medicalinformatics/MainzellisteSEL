package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum IDGeneratorFactory {
	instance;
	
	private final Map<String, IDGenerator<? extends ID>> generators;
	
	private IDGeneratorFactory() {
		PIDGenerator pidgen = PIDGenerator.init(1, 2, 3, 0);
		
		HashMap<String, IDGenerator<? extends ID>> temp = new HashMap<String, IDGenerator<? extends ID>>();
		temp.put("pid", pidgen);
		generators = Collections.unmodifiableMap(temp);
	}
	
	public IDGenerator<? extends ID> getFactory(String idType){
		return generators.get(idType);
	}
}
