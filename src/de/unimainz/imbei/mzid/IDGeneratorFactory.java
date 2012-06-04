package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.unimainz.imbei.mzid.dto.Persistor;

public enum IDGeneratorFactory {
	instance;
	
	private final Map<String, IDGenerator<? extends ID>> generators;
	
	private IDGeneratorFactory() {
		PIDGenerator pidgen = PIDGenerator.init(1, 2, 3, 0);
		IDGeneratorMemory mem = Persistor.instance.getIDGeneratorMemory("pid");
		if (mem == null)
			mem = new IDGeneratorMemory("pid");
		pidgen.init(mem, "pid");
		
		HashMap<String, IDGenerator<? extends ID>> temp = new HashMap<String, IDGenerator<? extends ID>>();
		temp.put(pidgen.getIdType(), pidgen);
		generators = Collections.unmodifiableMap(temp);
	}
	
	public IDGenerator<? extends ID> getFactory(String idType){
		return generators.get(idType);
	}
}
