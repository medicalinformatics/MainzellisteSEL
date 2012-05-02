package de.unimainz.imbei.mzid.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import de.unimainz.imbei.mzid.Characteristic;
import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.Matcher;
import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Person;
import de.unimainz.imbei.mzid.exceptions.NotImplementedException;
import de.unimainz.imbei.mzid.exceptions.UnauthorizedException;

/**
 * Resource-based access to patients.
 * 
 * @author Martin
 *
 */
@Path("/patients")
public class PatientsResource {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> getAllPatients() throws UnauthorizedException {
		//1. Auth prüfen: Falls nicht IDAT-Admin, UnauthorizedException werfen
		
		//2. Jeden Patienten aus der DB laden. Von EntityManager abkoppeln. Alle Felder, die keine IDs sind, streichen.
	
		//3. Patienten in Liste zurückgeben.
		
		throw new NotImplementedException();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public PID newPatient(MultivaluedMap<String, String> form){
		Person p = new Person();
		Map<String, Characteristic<?>> chars = new HashMap<String, Characteristic<?>>();
		
		for(String s: form.keySet()){ //TODO: Testfall mit defekten/leeren Eingaben
			chars.put(s, Characteristic.build(s, form.getFirst(s)));
		}

		p.setCharacteristics(chars);
		
		PID match = Matcher.instance.match(p);
		
		if(match != null)
			return match;
		
		PID pid = new PID(Config.instance.getPidgen().getNextPIDString());
		
		p.setId(pid);
		//TODO: Person speichern
		
		return pid;
	}
	
	@Path("/pid/{pid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Person getPatient(
			@PathParam("pid") PID pid){
		//IDAT-Admin?
		//Patient mit PID pid aus DB laden und zurückgeben
		throw new NotImplementedException();
	}
	
	@Path("/pid/{pid}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setPatient(
			@PathParam("pid") PID pid,
			Person p){
		//IDAT-Admin?
		//Charakteristika des Patients in DB mit PID pid austauschen durch die von p
		throw new NotImplementedException();
	}
	
	@Path("/tempid/{tid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Person getPatient(
			@PathParam("tid") String tid){
		//Hier keine Auth notwendig. Wenn tid existiert, ist der Nutzer dadurch autorisiert.
		//Patient mit TempID tid zurückgeben
		throw new NotImplementedException();
	}
	
	@Path("/tempid/{tid}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPatient(
			@PathParam("tid") String tid,
			Person p){
		//Hier keine Auth notwendig. Wenn tid existiert, ist der Nutzer dadurch autorisiert.
		//Charakteristika des Patients in DB mit TempID tid austauschen durch die von p
	}
}
