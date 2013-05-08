/*
 * Copyright (C) 2013 Martin Lablans, Andreas Borg, Frank Ückert
 * Contact: info@mainzelliste.de

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
package de.unimainz.imbei.mzid.matcher;

import java.util.Properties;
import java.util.Vector;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.Phonet;
import de.unimainz.imbei.mzid.PhonetRules;
import de.unimainz.imbei.mzid.PlainTextField;

/**
 * FieldTransformerPhonet is the transformation of one PlainTextField into another...
 *  
 * @author warnecke
 *
 */
public class FieldTransformerPhonet extends FieldTransformer<PlainTextField, PlainTextField>{

	private static final int FIRST_RULES = 0;

	@Override
	public PlainTextField transform(PlainTextField input) {
		// TODO Auto-generated method stub
		
		char[] result = new char[255];
	    String str = input.getValue();
	    int retCode = Phonet.phonet(str, result, 255, FIRST_RULES);

	    String resultString = new String(result);
	    int resIndex = resultString.indexOf('\0');
	        
	    return new PlainTextField(new String(resultString.substring(0, resIndex)));
	        
	}

	@Override
	public Class getInputClass() {
		// TODO Doku
		return PlainTextField.class;
	}

	@Override
	public Class getOutputClass() {
		// TODO Doku
		return PlainTextField.class;
	}
	
	// TODO Doku
	@Override
	public CompoundField<PlainTextField> transform(CompoundField<PlainTextField> input)
	{
		Vector<PlainTextField> outFields = new Vector<PlainTextField>(input.getSize());
		for (PlainTextField thisField : input.getValue())
		{
			outFields.add(this.transform(thisField));
		}
		CompoundField<PlainTextField> result = new CompoundField<PlainTextField>(outFields);
		return result;
	}
	
	
	// TODO Doku
	public void LoadRules()
	{
		Properties props = Config.instance.getProperties();
		if (props.containsKey("path_phonet"))
		{
			Phonet.loadPhonetRules(Config.instance.getProperty("path_phonet"));
		}
		else 
		{
			Phonet.setPhonetRules(PhonetRules.phonetRulesGerman);
		}
		
	}

}
