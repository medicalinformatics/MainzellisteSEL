package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.unimainz.imbei.mzid.Characteristic;
import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Person;
import de.unimainz.imbei.mzid.PlainTextCharacteristic;
import de.unimainz.imbei.mzid.exceptions.InvalidEntryException;
import de.unimainz.imbei.mzid.exceptions.InvalidPIDException;

@Deprecated
@Path("/persons")
public class PersonsResource {
	@Context UriInfo ui;
	@Context Request req;
	@Context HttpServletRequest sreq;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> getPersons(){
		List<Person> persList = PersonDAO.instance.getPersonList();
		return persList;
	}
	
	@Path("byid/{id}")
	public PersonResource getPerson(
			@PathParam("id") PID id
			) throws InvalidPIDException{
		return new PersonResource(ui, req, id);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response put(MultivaluedMap<String, String> formData) throws InvalidPIDException, InvalidEntryException {
		HashMap<String, Characteristic<?>> m = new HashMap<String, Characteristic<?>>();
		PID pid = null;
		for(String k: formData.keySet()){
			if(k.equals("id")) {
				pid = new PID(formData.getFirst(k));
				continue;
			}
			String value = formData.getFirst(k);
			//TODO: Wertebereich für Schlüssel definierbar machen
			m.put(k, new PlainTextCharacteristic(value));
		}
		
		Person p = new Person(pid, m);
		PersonDAO.instance.putPerson(p);
		
		return Response.created(UriBuilder.fromPath(pid.toString()).build()).entity("Patient " + pid.toString() + " angelegt.").build();
	}
}
