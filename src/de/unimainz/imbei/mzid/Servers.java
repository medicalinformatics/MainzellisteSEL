package de.unimainz.imbei.mzid;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.unimainz.imbei.mzid.exceptions.UnauthorizedException;
import de.unimainz.imbei.mzid.webservice.Token;

public enum Servers {
	instance;
	
	class Server {
		String apiKey;
		Set<String> permissions;
		Set<String> allowedRemoteAdresses;
	}
	
	private final Map<String, Server> servers = new HashMap<String, Server>();
	private final Map<String, Session> sessions = new HashMap<String, Session>();
	private final Map<String, Token> tokensByTid = new HashMap<String, Token>();
	
	private Servers() {
		Server mdat = new Server();
		mdat.apiKey = "mdat1234";
		mdat.permissions = new HashSet<String>(Arrays.asList("addNewPatient", "showSessionIds", "createSession"));
		mdat.allowedRemoteAdresses = new HashSet<String>(Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1"));
		servers.put(mdat.apiKey, mdat);
	}
	
	public Session newSession(){
		String sid = UUID.randomUUID().toString();
		Session s = new Session(sid);
		synchronized (sessions) {
			sessions.put(sid, s);
		}
		return s;
	}
	
	/**
	 * Returns Session with sid (or null if unknown)
	 * Caller MUST ensure proper synchronization on the session.
	 * 
	 * @return
	 */
	public Session getSession(String sid) {
		synchronized (sessions) {
			return sessions.get(sid);
		}
	}
	
	/**
	 * Returns all known session ids.
	 * 
	 * @return
	 */
	public Set<String> getSessionIds(){
		return Collections.unmodifiableSet(new HashSet<String>(sessions.keySet()));
	}
	
	public void deleteSession(String sid){
		Session s;
		synchronized (sessions) {
			s = sessions.get(sid);
			sessions.remove(sid);
		}
		
		for(Token t: s.getTokens()){
			deleteToken(sid, t.getId());
		}
	}
	
	public void checkPermission(HttpServletRequest req, String permission){
		Set<String> perms = (Set<String>) req.getSession(true).getAttribute("permissions");

		if(perms == null){ // Login
			String apiKey = req.getHeader("mzidApiKey");
			Server server = servers.get(apiKey);
			
			if(server == null){
				throw new WebApplicationException(Response
						.status(Status.UNAUTHORIZED)
						.entity("Please supply your API key in HTTP header field 'mzidApiKey'.")
						.build());
			}
			
			if(!server.allowedRemoteAdresses.contains(req.getRemoteAddr())){
				throw new WebApplicationException(Response
						.status(Status.UNAUTHORIZED)
						.entity(String.format("Rejecting your IP address %s.", req.getRemoteAddr()))
						.build());
			}
			
			perms = server.permissions;
			req.getSession().setAttribute("permissions", perms);
		}
		
		if(!perms.contains(permission)){ // Check permission
			throw new WebApplicationException(Response
					.status(Status.UNAUTHORIZED)
					.entity("Your permissions do not allow the requested access.")
					.build());
		}
	}
	
	public Token newToken(String sessionId){
		String tid = UUID.randomUUID().toString();
		Token t = new Token(tid);
		
		getSession(sessionId).addToken(t);

		synchronized(tokensByTid){
			tokensByTid.put(t.getId(), t);
		}

		return t;
	}

	public void deleteToken(String sessionId, String tokenId) {
		getSession(sessionId).deleteToken(tokenId);
		
		synchronized (tokensByTid) {
			tokensByTid.remove(tokenId);
		}
	}
	
	public Set<Token> getAllTokens(String sid){
		Session s = getSession(sid);
		if(s == null) return Collections.emptySet();
		
		return s.getTokens();
	}

	public Token getTokenByTid(String tokenId) {
		synchronized (tokensByTid) {
			return tokensByTid.get(tokenId);
		}
	}
}
