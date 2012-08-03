package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unimainz.imbei.mzid.dto.Persistor;

public enum IDGeneratorFactory {
	instance;
	
	private final Map<String, IDGenerator<? extends ID>> generators;

	private IDGeneratorFactory() {
		Logger logger = Logger.getLogger(IDGeneratorFactory.class);
		// TODO mehrere Generatoren aus Config lesen
		try {
			int k1 = Integer.parseInt(Config.instance.getProperty("idgenerator.pid.k1"));
			int k2 = Integer.parseInt(Config.instance.getProperty("idgenerator.pid.k2"));
			int k3 = Integer.parseInt(Config.instance.getProperty("idgenerator.pid.k3"));
			int rndwidth = Integer.parseInt(Config.instance.getProperty("idgenerator.pid.rndwidth"));
			
		} catch (Exception e)
		{
			logger.fatal("Parsing of PID generator configuration failed", e);
		}
		
		PIDGenerator pidgen = PIDGenerator.init(1, 2, 3, 0);
		IDGeneratorMemory mem = Persistor.instance.getIDGeneratorMemory("pid");
		if (mem == null)
			mem = new IDGeneratorMemory("pid");
		pidgen.init(mem, "pid");
		
		HashMap<String, IDGenerator<? extends ID>> temp = new HashMap<String, IDGenerator<? extends ID>>();
		temp.put(pidgen.getIdType(), pidgen);
		generators = Collections.unmodifiableMap(temp);
		
		logger.info("ID generators have initialized successfully.");
	}
	
	public IDGenerator<? extends ID> getFactory(String idType){
		return generators.get(idType);
	}
}
