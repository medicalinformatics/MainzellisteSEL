package de.unimainz.imbei.mzid.webservice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.sun.jersey.api.view.Viewable;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.PID;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.Servers;
import de.unimainz.imbei.mzid.dto.Persistor;

@Path("/html")
public class HTMLResource {

	Logger logger = Logger.getLogger(HTMLResource.class);
	
	@GET
	@Path("createPatient")
	@Produces(MediaType.TEXT_HTML)
	public Response createPatient(
			@QueryParam("tokenId") String tokenId,
			@QueryParam("callback") String callback){
		Token t = Servers.instance.getTokenByTid(tokenId);
		if (Config.instance.debugIsOn() ||
				(t != null && t.getType().equals("addPatient")))
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("tokenId", tokenId);
			map.put("callback", callback);
			map.put("adminPhone", Config.instance.getProperty("adminPhone"));
			return Response.ok(new Viewable("/createPatient.jsp", map)).build();
		} else throw new WebApplicationException(Response
				.status(Status.UNAUTHORIZED)
				.entity("Please supply a valid token id as URL parameter 'tokenId'.")
				.build());
	}
	
	@Path("/admin/editPatient")

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response editPatientForm(
			@QueryParam("id") String pidString
			) {
		// Authentication by Tomcat
		if (pidString == null || pidString.length() == 0)
			return Response.ok(new Viewable("/selectPatient.jsp")).build();

		/* TODO: Nach ID-Typ differenzieren (Typ als Parameter mitgeben)
		 	hier dann etwa folgendes:
			Class<? extends ID> idClass = IDGeneratorFactory.instance.getIDClass(Config.instance.getProperty("))
			// Instanz von idClass erzeugen...
			*/ 
		PID pid = new PID(pidString, "pid");
		Patient p = Persistor.instance.getPatient(pid);

		if (p == null)
			throw new WebApplicationException(Response
					.status(Status.NOT_FOUND)
					.entity("Found no patient with PID " + pidString + ".")
					.build());

		Map <String, Object> map = new HashMap<String, Object>();
		map.put("fields", p.getInputFields());
		map.put("id", pid.getIdString());
		map.put("tentative", p.getId("pid").isTentative());
		if (p.getOriginal() != p)
			map.put("original", p.getOriginal().getId("pid").getIdString());
		else
			map.put("original","");

		return Response.ok(new Viewable("/editPatient.jsp", map)).build();
	}

	/** Submit form for editing a patient. */
	// Eigentlich wäre das PUT auf /pid/{pid}, aber PUT aus HTML-Formular geht nicht.
	@POST
	@Path("/admin/editPatient")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response editPatient(
			@QueryParam("id") String pidString,
			MultivaluedMap<String, String> form,
			@Context HttpServletRequest req){
		
		logger.info("Handling edit operation for patient with id " + pidString);
		
		// TODO: Generalisieren für mehrere IDs
		Patient pToEdit = Persistor.instance.getPatient(new PID(pidString, "pid"));
		if (pToEdit == null)
		{
			logger.error("No patient found with id " + pidString);
			throw new WebApplicationException(Response
					.status(Status.NOT_FOUND)
					.entity("Found no patient with PID " + pidString + ".")
					.build());
		}
		
		// read input fields from form
		Patient pInput = new Patient();
		Map<String, Field<?>> chars = new HashMap<String, Field<?>>();

		for(String s: Config.instance.getFieldKeys()){ //TODO: Testfall mit defekten/leeren Eingaben
			chars.put(s, Field.build(s, form.getFirst(s)));
		}

		pInput.setFields(chars);
		
		// transform input fields
		Patient pNormalized = Config.instance.getRecordTransformer().transform(pInput);
		// set input fields
		pNormalized.setInputFields(chars);
		
		// assign changed fields to patient in database, persist
		pToEdit.setFields(pNormalized.getFields());

		// assign tentative status
		pToEdit.setTentative(form.getFirst("tentative") != null);
		
		// assign original
		// TODO: andere IDs, Checkbox dazu
		String idOriginal = form.getFirst("original");
		if (idOriginal != null && !idOriginal.equals(""))
		{
			Patient pOriginal = Persistor.instance.getPatient(new PID(idOriginal, "pid"));
			pToEdit.setOriginal(pOriginal);
		} else
		{
			pToEdit.setOriginal(pToEdit);
		}
			
		
		Persistor.instance.updatePatient(pToEdit);
		
		return Response.ok("Patient edited successfully!").build();
		// TODO: Redirect auf Edit-Formular für diesen Patienten
		/* 
		return Response
				.status(Status.SEE_OTHER)
//				.header("Cache-control", "must-revalidate")
				.location(UriBuilder
						.fromUri(req.getRequestURI())
						.path("")
						.queryParam("id", pidString)
						.build())
						.build(); */
	}	
	
}
