package de.unimainz.imbei.mzid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import de.unimainz.imbei.mzid.matcher.Matcher;

public enum Config {
	instance;
	
	public enum FieldType {
		PLAINTEXT,
		PLAINTEXT_NORMALIZED,
		HASHED, // Bloomfilter without prior normalization
		HASHED_NORMALIZED; // Bloomfilter with prior normalization
	}
	
	private final String configPath = "mzid.conf";
	
	@Deprecated
	private final Map<String,FieldType> FieldTypes;
	
	private Properties props;
	private Matcher matcher;
	
	Config() {
		//TODO: Das alles irgendwoher laden.
		props = new Properties();
		try {
			InputStream is = Config.class.getResourceAsStream(configPath);
			
			props.load(is);
			is.close();
			System.out.println("Properties:");
			System.out.println(props);
			
		} catch (IOException e)
		{
			// TODO
		}
		
		try {
			matcher = (Matcher) Class.forName(props.getProperty("matcher")).newInstance();
		} catch (Exception e){
			// TODO
		}
		


		Map<String, FieldType> temp = new HashMap<String, FieldType>();
		temp.put("vorname", FieldType.PLAINTEXT);
		temp.put("nachname", FieldType.PLAINTEXT);
		temp.put("geburtsname", FieldType.PLAINTEXT);
		temp.put("geburtsdatum", FieldType.PLAINTEXT);
		FieldTypes = Collections.unmodifiableMap(temp);
	}
	
	public Matcher getMatcher() {
		return matcher;
	}

	public String getProperty(String propKey){
		return props.getProperty(propKey);
	}
	
	@Deprecated
	public Set<String> getFieldKeys(){
		return FieldTypes.keySet();
	}
	
	@Deprecated
	public FieldType getFieldType(String FieldKey){
		assert FieldTypes.keySet().contains(FieldKey);
		return FieldTypes.get(FieldKey);
	}
}