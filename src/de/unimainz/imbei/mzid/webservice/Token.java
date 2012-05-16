package de.unimainz.imbei.mzid.webservice;

import java.util.Map;

import de.unimainz.imbei.mzid.Servers;
import de.unimainz.imbei.mzid.Session;

/**
 * A temporary "ticket" to realize authorization and/or access to a resource.
 * Tokens are accessible via their token id (e.g. GET /patients/tempid/{tid}),
 * but also connected to a {@link Session} (e.g. DELETE /sessions/{sid}).
 * Thus, they are created using a session.
 * 
 * @author Martin Lablans
 *
 */
public class Token {
	private String id;
	private String type;
	private Map<String, String> data;
	
	Token() {}
	
	public Token(String tid) {
		this.id = tid;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Map<String, String> getData() {
		return data;
	}
	
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Token)) return false;
		
		Token t2 = (Token)obj;
		return t2.id.equals(this.id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
