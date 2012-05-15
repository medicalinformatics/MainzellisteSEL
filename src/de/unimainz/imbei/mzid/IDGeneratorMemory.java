package de.unimainz.imbei.mzid;

import java.util.HashMap;
import java.util.Map;

public class IDGeneratorMemory {
	Map<String, String> mem = new HashMap<String, String>();
	
	synchronized void set(String key, String value){
		mem.put(key, value);
	}
	
	synchronized String get(String key){
		return mem.get(key);
	}
	
	synchronized void commit(){
		//TODO: Persistieren
	}
}
