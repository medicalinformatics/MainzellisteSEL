package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import de.unimainz.imbei.mzid.dto.Persistor;
import de.unimainz.imbei.mzid.exceptions.InternalErrorException;

public enum IDGeneratorFactory {
	instance;
	
	private final Map<String, IDGenerator<? extends ID>> generators;

	private Logger logger = Logger.getLogger(this.getClass());
	private IDGeneratorFactory() {
		
		HashMap<String, IDGenerator<? extends ID>> temp = new HashMap<String, IDGenerator<? extends ID>>();
		Preferences prefs = Preferences.userRoot().node("de/unimainz/imbei/mzid/idgenerator");
		Properties props = Config.instance.getProperties();
		if(!props.containsKey("idgenerators") || props.getProperty("idgenerators").length() == 0) {
			logger.fatal("No ID generators defined!");
			throw new Error("No ID generators defined!");
		}
		// split list of ID generators: comma-separated, ignore whitespace around commas
		String[] idTypes = props.getProperty("idgenerators").split("\\s*,\\s*");
		// Iterate over ID types
		for (String thisIdType : idTypes) {
			String thisIdGenerator = prefs.get(thisIdType, "");
			try {
				// Add mzid package to class name if none is given 
				// (check by searching for a dot in the class name)
				if (!thisIdGenerator.contains("."))
					thisIdGenerator = "de.unimainz.imbei.mzid." + thisIdGenerator;
				IDGenerator<?> thisGenerator = (IDGenerator<?>) Class.forName(thisIdGenerator).newInstance();
				IDGeneratorMemory mem = Persistor.instance.getIDGeneratorMemory(thisIdType);
				if (mem == null)
					mem = new IDGeneratorMemory(thisIdType);				
				// Get properties for this ID generator from Preferences 
				Properties thisIdProps = new Properties();
				Preferences thisIdPrefs = prefs.node(thisIdType);
				for (String key : thisIdPrefs.keys()) {
					thisIdProps.put(key, thisIdPrefs.get(key, ""));
				}
				thisGenerator.init(mem, thisIdType, thisIdProps);
				temp.put(thisIdType, thisGenerator);
			} catch (ClassNotFoundException e) {
				logger.fatal("Unknown ID generator: " + thisIdType);
				throw new Error(e);
			} catch (Exception e) {
				logger.fatal("Could not initialize ID generator " + thisIdType, e);
				throw new Error(e);
			}
		}
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
		
//		PIDGenerator pidgen = PIDGenerator.init(1, 2, 3, 0);
//		IDGeneratorMemory mem = Persistor.instance.getIDGeneratorMemory("pid");
//		if (mem == null)
//			mem = new IDGeneratorMemory("pid");
//		//pidgen.init(mem, "pid");
//		
//		temp.put(pidgen.getIdType(), pidgen);
		generators = Collections.unmodifiableMap(temp);
		
		logger.info("ID generators have initialized successfully.");
	}
	
	public IDGenerator<? extends ID> getFactory(String idType){
		return generators.get(idType);
	}
	
	/**
	 * Generates a set of IDs for a new patient by calling every ID generator defined
	 * in the configuration.
	 */
	public Set<ID> generateIds() {
		HashSet<ID> ids = new HashSet<ID>();
		for (String idType : this.generators.keySet()) {
			ids.add(this.generators.get(idType).getNext());
		}
		return ids;
	}
}
