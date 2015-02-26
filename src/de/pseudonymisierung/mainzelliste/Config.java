/*
 * Copyright (C) 2013-2015 Martin Lablans, Andreas Borg, Frank Ückert
 * Contact: info@mainzelliste.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it 
 * with Jersey (https://jersey.java.net) (or a modified version of that 
 * library), containing parts covered by the terms of the General Public 
 * License, version 2.0, the licensors of this Program grant you additional 
 * permission to convey the resulting work.
 */
package de.pseudonymisierung.mainzelliste;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.pseudonymisierung.mainzelliste.exceptions.InternalErrorException;
import de.pseudonymisierung.mainzelliste.matcher.*;

/**
 * Configuration of the patient list. Implemented as a singleton object, which can be referenced
 * by Config.instance. The configuration is read from the properties file specified as
 * parameter de.pseudonymisierung.mainzelliste.ConfigurationFile in context.xml
 * (see {@link java.util.Properties#load(InputStream) java.util.Properties}).
 */
public enum Config {
	instance;
	
	public enum FieldType {
		PLAINTEXT,
		PLAINTEXT_NORMALIZED,
		HASHED, // Bloomfilter without prior normalization
		HASHED_NORMALIZED; // Bloomfilter with prior normalization
	}
	
	private final String version = "1.4.0";
	
	/** Default paths from where configuration is read if no path is given in the context descriptor */ 
	private final String defaultConfigPaths[] = {"/etc/mainzelliste/mainzelliste.conf", "/WEB-INF/classes/mainzelliste.conf"};


	private final Map<String,Class<? extends Field<?>>> FieldTypes;
	
	private Properties props;
	private RecordTransformer recordTransformer;
	private Matcher matcher;
	
	private Logger logger = Logger.getLogger(Config.class);
	
	private Set<String> allowedOrigins;
	
	@SuppressWarnings("unchecked")
	Config() throws InternalErrorException {
		props = new Properties();
		try {
			// Check if path to configuration file is given in context descriptor
			ServletContext context = Initializer.getServletContext();
			String configPath = context.getInitParameter("de.pseudonymisierung.mainzelliste.ConfigurationFile");

			// try to read config from configured path  
			if (configPath != null) { 
				logger.info("Reading config from path " + configPath + "...");
				props = readConfigFromFile(configPath);
				if (props == null) {
					throw new Error("Configuration file could not be read from provided location " + configPath);
				}
			} else {
				// otherwise, try default locations
				logger.info("No configuration file configured. Try to read from default locations...");
				for (String defaultConfigPath : defaultConfigPaths) {
					logger.info("Try to read configuration from default location " + defaultConfigPath);
					props = readConfigFromFile(defaultConfigPath);
					if (props != null) {
						logger.info("Found configuration file at default location " + defaultConfigPath);
						break;
					}
				}
				if (props == null) {
					throw new Error("Configuration file could not be found at any default location");
				}
			}			

			/* 
			 * Read properties into Preferences for easier hierarchical access
			 * (e.g. it is possible to get the subtree of all idgenerators.* properties)
			 */
			Preferences prefs = Preferences.userRoot().node("de/pseudonymisierung/mainzelliste");
			for (Object propName : props.keySet()) {
				Preferences prefNode = prefs;
				// Create a path in the preferences according to the property key.
				// (Path separated by ".") The last element is used as parameter name. 
				String prefKeys[] = propName.toString().split("\\.", 0);
				for (int i = 0; i < prefKeys.length - 1; i++)
					prefNode = prefNode.node(prefKeys[i]);
				prefNode.put(prefKeys[prefKeys.length - 1], props.getProperty(propName.toString()));
			}					
			logger.info("Config read successfully");
			logger.debug(props);
			
		} catch (IOException e)	{
			logger.fatal("Error reading configuration file. Please configure according to installation manual.", e);
			throw new Error(e);
		}
		
		this.recordTransformer = new RecordTransformer(props);
		
		try {
			Class<?> matcherClass = Class.forName("de.pseudonymisierung.mainzelliste.matcher." + props.getProperty("matcher"));
			matcher = (Matcher) matcherClass.newInstance();
			matcher.initialize(props);
			logger.info("Matcher of class " + matcher.getClass() + " initialized.");
		} catch (Exception e){
			logger.fatal("Initialization of matcher failed: " + e.getMessage(), e);
			throw new InternalErrorException();
		}
		
		// Read field types from configuration
		Pattern pattern = Pattern.compile("field\\.(\\w+)\\.type");
		java.util.regex.Matcher patternMatcher;
		this.FieldTypes = new HashMap<String, Class<? extends Field<?>>>();
		for (String propKey : props.stringPropertyNames()) {
			patternMatcher = pattern.matcher(propKey);
			if (patternMatcher.find())
			{
				String fieldName = patternMatcher.group(1);					
				String fieldClassStr = props.getProperty(propKey).trim();
				try {
					Class<? extends Field<?>> fieldClass;
					try {
						fieldClass = (Class<? extends Field<?>>) Class.forName(fieldClassStr);
					} catch (ClassNotFoundException e) {
						// Try with "de.pseudonymisierung.mainzelliste..."
						fieldClass = (Class<? extends Field<?>>) Class.forName("de.pseudonymisierung.mainzelliste." + fieldClassStr);
					}
					this.FieldTypes.put(fieldName, fieldClass);
					logger.debug("Initialized field " + fieldName + " with class " + fieldClass);
				} catch (Exception e) {
					logger.fatal("Initialization of field " + fieldName + " failed: ", e);
					throw new InternalErrorException();
				}
			}
		}
		
		// Read allowed origins for cross domain resource sharing (CORS)
		allowedOrigins = new HashSet<String>();
		String allowedOriginsString = props.getProperty("servers.allowedOrigins"); 
		if (allowedOriginsString != null)			
			allowedOrigins.addAll(Arrays.asList(allowedOriginsString.trim().split(";")));
		
		Locale.setDefault(Locale.ENGLISH);
	}
	
	/**
	 * Get the {@link RecordTransformer} instance configured for this instance.
	 * @return The {@link RecordTransformer} instance configured for this instance.
	 */
	public RecordTransformer getRecordTransformer() {
		return recordTransformer;
	}

	/**
	 * Get configuration as Properties object.
	 * @return Properties object as read from the configuration file.
	 */
	public Properties getProperties() {
		return props;
	}

	/**
	 * Get the matcher configured for this instance.
	 * @return The matcher configured for this instance.
	 */
	public Matcher getMatcher() {
		return matcher;
	}

	/**
	 * Get the specified property from the configuration.
	 * @param propKey Property name.
	 * @return The property value or null if no such property exists. 
	 */
	public String getProperty(String propKey){
		return props.getProperty(propKey);
	}
	
	/**
	 * Get the names of fields configured for this instance.
	 * @return The names of fields configured for this instance.
	 */
	public Set<String> getFieldKeys(){
		return FieldTypes.keySet();
	}
	
	public Class<? extends Field<?>> getFieldType(String FieldKey){
		assert FieldTypes.keySet().contains(FieldKey);
		return FieldTypes.get(FieldKey);
	}
	
	/**
	 * Check whether a field with the given name is configured.
	 */
	public boolean fieldExists(String fieldName) {
		return this.FieldTypes.containsKey(fieldName);
	}

	public String getDist() {
		return getProperty("dist");
	}
	
	public String getVersion() {
		return version;
	}
	
	public boolean debugIsOn()
	{
		String debugMode = this.props.getProperty("debug");
		return (debugMode != null && debugMode.equals("true"));
	}
	
	public boolean originAllowed(String origin) {
		return this.allowedOrigins.contains(origin);
	}
	
	/**
	 * Get the logo file from the path defined by configuration parameter 'operator.logo'.
	 * @return The file object. It is checked that the file exists.
	 * @throws FileNotFoundException if the logo file cannot be found at the specified location.
	 */
	public File getLogo() throws FileNotFoundException {
		String logoFileName = this.getProperty("operator.logo");
		if (logoFileName == null || logoFileName.equals(""))
			throw new FileNotFoundException("No logo file configured.");
		File logoFile = new File(logoFileName);
		if (logoFile.exists())
			return logoFile;
		else 
			throw new FileNotFoundException("No logo file found at " + logoFileName + ".");
	}
	
	private Properties readConfigFromFile(String configPath) throws IOException {
		ServletContext context = Initializer.getServletContext();
		// First, try to read from resource (e.g. within the war file)
		InputStream configInputStream = context.getResourceAsStream(configPath);
		// Else: read from file System
		if (configInputStream == null) {
			File f = new File(configPath);
			if (f.exists()) 
				configInputStream = new FileInputStream(configPath);
			else return null;
		}
		
		Reader reader = new InputStreamReader(configInputStream, "UTF-8");
		Properties props = new Properties();
		props.load(reader);
		configInputStream.close();
		return props;
	}
	
	public ResourceBundle getResourceBunde(ServletRequest req) {
		Locale requestLocale = req.getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle("MessageBundle", requestLocale);
		return bundle;
	}
	
	Level getLogLevel() {
		String level = this.props.getProperty("log.level");
		Level ret = Level.DEBUG;
		
		if (level == null || level.equals("DEBUG"))
			ret = Level.DEBUG;
		else if (level.equals("WARN"))
			ret = Level.WARN;
		else if (level.equals("ERROR"))
			ret = Level.ERROR;
		else if (level.equals("FATAL"))
			ret = Level.FATAL;
		else if (level.equals("INFO"))
			ret = Level.INFO;
		
		return ret;
	}
}