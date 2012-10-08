package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;
import de.unimainz.imbei.mzid.Config;

@Path("/")
public class RootResource {
	private Map<String,String> genMap(){
		Map<String,String> out = new HashMap<String, String>();
		out.put("distname", Config.instance.getDist());
		out.put("version", Config.instance.getVersion());
		return out;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject helloJSON(){
		return new JSONObject(genMap());
	}
	
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String helloHTML(){
		Map<String, String> out = genMap();
		return String.format("This is mzid running version %s for %s.", out.get("version"), out.get("distname"));
	}
}
