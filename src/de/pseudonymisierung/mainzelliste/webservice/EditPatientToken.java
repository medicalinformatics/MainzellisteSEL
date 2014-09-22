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
package de.pseudonymisierung.mainzelliste.webservice;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.pseudonymisierung.mainzelliste.Config;
import de.pseudonymisierung.mainzelliste.ID;
import de.pseudonymisierung.mainzelliste.IDGeneratorFactory;
import de.pseudonymisierung.mainzelliste.dto.Persistor;
import de.pseudonymisierung.mainzelliste.exceptions.InvalidFieldException;
import de.pseudonymisierung.mainzelliste.exceptions.InvalidIDException;

public class EditPatientToken extends Token {
	
	/**
	 * ID of the patient that can be edited with this token.
	 */
	ID patientId;
	
	/**
	 * Names of fields that can be changed with this token. If null,
	 * all fields can be changed.
	 */
	Set<String> fields;
	
	@Override
	public void setData(Map<String, ?> data) {
		super.setData(data);

		// Read patient id from token and check if valid
		Map<String, ?> idJSON = this.getDataItemMap("patientId");
		if (!idJSON.containsKey("idString") || !idJSON.containsKey("idType"))
			throw new InvalidIDException("Please provide a valid patient id as data item 'patientId'!");
		
		this.patientId = IDGeneratorFactory.instance.buildId(
				idJSON.get("idType").toString(), idJSON.get("idString").toString());
		
		if (!Persistor.instance.patientExists(patientId))
			throw new InvalidIDException("No patient exists with id " + patientId.toString());
		
		// Read field list (if present) from data and check if valid
		List<?> fieldsJSON = this.getDataItemList("fields");
		if (fieldsJSON == null)
			return;
		
		this.fields = new HashSet<String>();
		for (Object thisField : fieldsJSON) {
			String fieldName = thisField.toString();
			if (!Config.instance.fieldExists(fieldName))
				throw new InvalidFieldException("No field '" + fieldName + "' defined!");
			this.fields.add(fieldName);
		}
	}	

}
