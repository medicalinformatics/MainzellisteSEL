package de.unimainz.imbei.mzid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import de.unimainz.imbei.mzid.exceptions.InternalErrorException;
import de.unimainz.imbei.mzid.matcher.*;

public enum Config {
	instance;
	
	public enum FieldType {
		PLAINTEXT,
		PLAINTEXT_NORMALIZED,
		HASHED, // Bloomfilter without prior normalization
		HASHED_NORMALIZED; // Bloomfilter with prior normalization
	}
	
	private final String configPath = "X:/workspace/mzid/WebContent/mzid.conf";
	
	private final Map<String,Class<? extends Field<?>>> FieldTypes;
	
	private Properties props;
	private Matcher matcher;
	
	Config() throws InternalErrorException{
		//TODO: Das alles irgendwoher laden.
		props = new Properties();
		try {
			File f = new File(".");
			String path = f.getAbsolutePath();
			System.err.println("Arbeitsverzeichnis: " + path);

			//InputStream is = Config.class.getResourceAsStream(configPath);
			InputStream is = new FileInputStream(configPath);
			
			props.load(is);
			is.close();
			System.out.println("Properties:");
			System.out.println(props);
			
		} catch (IOException e)
		{
			System.err.println("Error while reading configuration file!");
		}
		
		try {
			Class<?> matcherClass = Class.forName("de.unimainz.imbei.mzid.matcher." + props.getProperty("matcher"));
			Constructor<?> matcherConstructor = matcherClass.getConstructor(props.getClass());
			matcher = (Matcher) matcherConstructor.newInstance(props);
		} catch (Exception e){
			// TODO gescheites Logging
			System.err.println(e);
			throw new InternalErrorException();
		}
		
		// Read field types from configuration
		Pattern pattern = Pattern.compile("field\\.(\\w+)\\.type");
		java.util.regex.Matcher patternMatcher;
		this.FieldTypes = new HashMap<String, Class<? extends Field<?>>>();
		for (String propKey : props.stringPropertyNames())
		{
			patternMatcher = pattern.matcher(propKey);
			if (patternMatcher.find())
			{
				String fieldName = patternMatcher.group(1);	
				String fieldClass = "de.unimainz.imbei.mzid." + props.getProperty(propKey).trim();
				try {
					this.FieldTypes.put(fieldName, (Class<? extends Field<?>>) Class.forName(fieldClass));
				} catch (Exception e) {
					//TODO Logging
					throw new InternalErrorException();
				}
			}
		}
		

	}
	
	public Properties getProperties() {
		return props;
	}

	public Matcher getMatcher() {
		return matcher;
	}

	public String getProperty(String propKey){
		return props.getProperty(propKey);
	}
	
	public Set<String> getFieldKeys(){
		return FieldTypes.keySet();
	}
	
	public Class<? extends Field<?>> getFieldType(String FieldKey){
		assert FieldTypes.keySet().contains(FieldKey);
		return FieldTypes.get(FieldKey);
	}
	
	public static void main(String args[])
	{
		Config cfg;
	}
}