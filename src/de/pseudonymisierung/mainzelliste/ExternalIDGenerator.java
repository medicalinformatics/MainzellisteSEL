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
 *
 *
 * This file is a Java port of the PID generation code by Klaus Pommerening.
 * The original copyright notice follows:
 *
 **** PIDgen.c *************************************************
 *                                                             *
 * Functions to support a pseudonymization service             *
 *-------------------------------------------------------------*
 * Klaus Pommerening, IMSD, Johannes-Gutenberg-Universitaet,   *
 *   Mainz, 3. April 2001                                      *
 *-------------------------------------------------------------*
 * Version 1.00, 29. Mai 2004                                  *
 ***************************************************************
 */
package de.pseudonymisierung.mainzelliste;

import org.apache.commons.lang.NotImplementedException;

import java.util.Properties;

public class ExternalIDGenerator implements IDGenerator<ExternalID>{

    /** The ID type this generator instance creates. */
    String idType;

    @Override
    public void init(IDGeneratorMemory mem, String idType, Properties props) {
        this.idType = idType;
    }

    @Override
    public boolean verify(String id) {
        return true;
    }

    @Override
    public synchronized ExternalID getNext() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public String correct(String IDString) {
        return IDString;
    }

    @Override
    public ExternalID buildId(String id) {
        return new ExternalID(id, getIdType());
    }

    @Override
    public String getIdType() {
        return idType;
    }

    @Override
	public boolean isExternal() { return true; }
}
