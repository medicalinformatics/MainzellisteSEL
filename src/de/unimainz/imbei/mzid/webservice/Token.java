package de.unimainz.imbei.mzid.webservice;

import java.util.Map;

/**
 * A temporary "ticket" to realize authorization and/or access to a resource.
 * 
 * @author Martin
 *
 */
public abstract class Token {
	private String id;
	private String type;
	private Map<String, String> data;
}
