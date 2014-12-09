/*
 * Copyright (C) 2013 Martin Lablans, Andreas Borg, Frank Ückert
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;


import de.pseudonymisierung.mainzelliste.exceptions.InternalErrorException;
import de.pseudonymisierung.mainzelliste.exceptions.ValidatorException;

/**
 * Form validation.
 * Validation checks are stored in a Properties object passed to the constructor.
 * Supported checks:
 * 
 * <ul>
 * 	<li> Check required fields (i.e. not empty): validator.field.<i>fieldname</i>.required marks
 * 		field <i> fieldname</i> as required.
 *  <li> Check format: validator.field.<i>fieldname</i>.format defines a regular expression against
 *  	which the specified field is checked.
 *  <li>
 * </ul>
 */
public enum Validator {

	instance;

	private Set<String> requiredFields = new HashSet<String>();
	private Map<String, String> formats = new HashMap<String, String>();
	private List<List<String>> dateFields = new LinkedList<List<String>>();
	private List<String> dateFormat = new LinkedList<String>();
	private Logger logger = Logger.getLogger(this.getClass());
	
	private Validator() {

		Properties props = Config.instance.getProperties();
		
		Pattern pRequired = Pattern.compile("^validator\\.field\\.(\\w+)\\.required");
		Pattern pFormat = Pattern.compile("^validator\\.field\\.(\\w+)\\.format");
		Pattern pDateFields = Pattern.compile("^validator\\.date\\.(\\d+).fields");
		java.util.regex.Matcher m;
		
		for (Object thisPropKeyObj : props.keySet()) {
			String thisPropKey = (String) thisPropKeyObj;
			
			// Look for required fields
			m = pRequired.matcher(thisPropKey);
			if (m.find())
			{
				requiredFields.add(m.group(1).trim());
			}
			
			// Look for format definitions
			m = pFormat.matcher(thisPropKey);
			
			if (m.find())
			{
				String fieldName = m.group(1);
				String format = props.getProperty(thisPropKey).trim();
				// Check if format is a valid regular expression
				try {
					Pattern.compile(format);
				} catch (PatternSyntaxException e) {
					throw new InternalErrorException(e);
				}				
				formats.put(fieldName, format);
			}

			// Look for format definitions
			m = pDateFields.matcher(thisPropKey);
			if (m.find())
			{
				try {
					int dateInd = Integer.parseInt(m.group(1));
					List<String> theseFields = new LinkedList<String>();
					for (String thisFieldName : props.getProperty("validator.date." + dateInd + ".fields").split(",")) {
						theseFields.add(thisFieldName.trim());
					}
					dateFields.add(theseFields);
					dateFormat.add(props.getProperty("validator.date." + dateInd + ".format").trim());
					} catch (NumberFormatException e) {
					throw new InternalErrorException(e);
				}
			}
			
		}		
	}
	
	public void validateField(String key, String value) {
		// Format error message with human-readable field label (if such defined)
		String label = Config.instance.getFieldLabel(key);
			
		if (requiredFields.contains(key)) {
			if (value == null || value.equals("")) {
				throw new ValidatorException("Field '" + label + "' must not be empty!");
			}
		}

		if (formats.containsKey(key)) {
			String format = formats.get(key);
			if (value != null && !value.equals("") && !Pattern.matches(format, value)) {
				throw new ValidatorException("Field " + label + 
						" does not conform to the required format" + format);
			}
		}
	}
	
	/**
	 * Validates dates in input form according to format definition in configuration.
	 */
	public void validateDates(MultivaluedMap<String, String> form) {
		// List to collect all dates in the form
		List<String> dateStrings = new LinkedList<String>();
		for (List<String> thisDateFields : this.dateFields) {
			StringBuffer dateString = new StringBuffer();
			for (String fieldName : thisDateFields) {
				dateString.append(form.getFirst(fieldName));				
			}
			dateStrings.add(dateString.toString());
		}		
		checkDates(this.dateFormat, dateStrings);
	}
	
	/**
	 * Validates dates in input form according to format definition in configuration.
	 */
	public void validateDates(Map<String, String> form) {

		// List to collect all dates in the form
		List<String> dateStrings = new LinkedList<String>();
		for (List<String> thisDateFields : this.dateFields) {
			StringBuffer dateString = new StringBuffer();
			for (String fieldName : thisDateFields) {
				dateString.append(form.get(fieldName));				
			}
			dateStrings.add(dateString.toString());
		}		
		checkDates(this.dateFormat, dateStrings);
	}

	/**
	 * Validate input form according to the format definitions in the configuration.
	 */
	public void validateForm(MultivaluedMap<String, String> form, boolean checkFieldKeys) {
		// Check that all fields are present in form
		if (checkFieldKeys) 
			checkFieldKeys(form);
		// Check fields values
		for (String key : form.keySet()) {
			for (String value : form.get(key)) {
				validateField(key, value);
			}			
		}
		validateDates(form);
	}
	
	/**
	 * Validate input form according to the format definitions in the configuration.
	 */
	public void validateForm(Map<String, String> form, boolean checkFieldKeys) {
		// Check that all fields are present in form
		if (checkFieldKeys) 
			checkFieldKeys(form);
		// Check fields values
		for (String key : form.keySet()) {
			validateField(key, form.get(key));
		}
		validateDates(form);
	}

	private void checkFieldKeys(Map<String, ?> form) {
		for(String s: Config.instance.getFieldKeys()){
			if (!form.containsKey(s)) {
				logger.error("Field " + s + " not found in input data!");
				throw new ValidatorException("Field " + s + " not found in input data!");
			}
		}
	}
	
	private void checkDates(Iterable<String> formatStrings, Iterable<String> dateStrings) {		
		Iterator<String> formatIt = formatStrings.iterator();
		Iterator<String> dateIt = dateStrings.iterator();
		
		while (formatIt.hasNext() && dateIt.hasNext()) {
			SimpleDateFormat sdf = new SimpleDateFormat(formatIt.next());
			sdf.setLenient(false);
			String dateString = dateIt.next();
			try {				
				Date date = sdf.parse(dateString); 
				if (date == null)
					throw new ValidatorException(dateString + " is not a valid date!");
			} catch (ParseException e) {
				throw new ValidatorException(dateString + " is not a valid date!");
			}			
		}
	}
}
