/*package de.unimainz.imbei.mzid.webservice;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext>{
	private JAXBContext context;
	private Class<?>[] types = {Person.class};
	
	public JAXBContextResolver() throws Exception{
		context = new JSONJAXBContext(JSONConfiguration.natural().build());
	}
	
	@Override
	public JAXBContext getContext(Class<?> objectType) {
		for(Class<?> type: types){
			if(type == objectType){
				return context;
			}
		}
		return null;
	}
}
*/