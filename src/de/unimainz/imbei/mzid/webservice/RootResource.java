package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unimainz.imbei.mzid.Config;

@Path("/")
public class RootResource {	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,String> helloJSON(){
		Map<String,String> out = new HashMap<String, String>();
		out.put("distname", Config.instance.getDist());
		out.put("version", Config.instance.getVersion());
		return out;
	}
	
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String helloHTML(){
		Map<String,String> out = helloJSON();
		return String.format("This is %s running version %s.", out.get("distname"), out.get("version"));
	}
}