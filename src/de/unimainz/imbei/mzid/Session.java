package de.unimainz.imbei.mzid;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unimainz.imbei.mzid.Servers.Server;
import de.unimainz.imbei.mzid.webservice.Token;

//import de.unimainz.imbei.mzid.dto.SessionAdapter;

//@XmlJavaTypeAdapter(SessionAdapter.class)
public class Session extends ConcurrentHashMap<String, String>{
	private String sessionId;
	private Set<Token> tokens = new HashSet<Token>();
	
	public Session(String s) {
		sessionId = s;
	}
	
	public String getId(){
		return sessionId;
	}

	/**
	 * Delete this session and unregister all its tokens.
	 */
	void destroy(){
		Servers.instance.deleteSession(getId());
	}
	
	public Set<Token> getTokens() {
		synchronized(tokens){
			return Collections.unmodifiableSet(tokens);
		}
	}
	
	public void addToken(Token t){
		synchronized(tokens){
			tokens.add(t);
		}
	}

	public void deleteToken(String tokenId) {
		synchronized(tokens){
			tokens.remove(tokenId);
		}
	}
}
