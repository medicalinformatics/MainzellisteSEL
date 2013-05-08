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

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.PlainTextField;


/**
 * Wrapper for transformations conducted by PSX:
 * <ul>
 * 	<li> Removal of leading and trailing delimiters.
 * 	<li> Conversion of umlauts.
 * 	<li> Decomposition into three components.
 * @author borg
 *
 */
@Deprecated // War nötig, solange man in der Config nicht mehrere Transformer in einem Feld angeben konnte
public class PSXLastNameNormalizer extends FieldTransformer<PlainTextField, CompoundField<PlainTextField>> {

	private StringNormalizer stringNormalizer = new StringNormalizer();
	private GermanLastNameDecomposer lastNameDecomposer = new GermanLastNameDecomposer();
	
	@Override
	public CompoundField<PlainTextField> transform(PlainTextField input) {
		return lastNameDecomposer.transform(stringNormalizer.transform(input)); 
	}
	
	@Override
	public Class<PlainTextField> getInputClass()
	{
		return PlainTextField.class;
	}
	
	@Override
	public Class<CompoundField<PlainTextField>> getOutputClass()
	{
		return (Class<CompoundField<PlainTextField>>) new CompoundField<PlainTextField>(3).getClass();
	}

}
