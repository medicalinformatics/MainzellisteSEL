package de.unimainz.imbei.mzid.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Person;
import de.unimainz.imbei.mzid.exceptions.InvalidPIDException;

public class PersonResource {
	@Context
	UriInfo uriInfo;
	
	@Context
	Request request;
	
	PID id;
	
	public PersonResource(UriInfo uriInfo, Request request, PID pid) throws InvalidPIDException{
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = pid;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Person get() throws InvalidPIDException, PersonUnknownException {
		Person p = PersonDAO.instance.getPerson(id);
		if(p == null){
			throw new PersonUnknownException();
		} else {
			return p;
		}
	}
}
