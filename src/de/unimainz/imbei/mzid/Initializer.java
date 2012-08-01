package de.unimainz.imbei.mzid;

import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import de.unimainz.imbei.mzid.dto.Persistor;

/**
 * This class is responsible for setting up all singletons in the right order
 * and to fail early if anything goes wrong.
 * 
 * @author Martin Lablans
 */
@Provider public class Initializer extends SingletonTypeInjectableProvider<Context, Initializer> {
	@Context ServletContext servletContext;
	private static ServletContext context;
	
	public Initializer() {
		super(Initializer.class, null);
	}
	
	@PostConstruct
	public void initialize(){
		Logger logger = Logger.getLogger(Initializer.class);
		logger.info("MZID: Initializing Singletons...");
		Initializer.context = servletContext;
		
		//<DEBUG>
		Enumeration<String> en = context.getInitParameterNames();
		while(en.hasMoreElements()){
			String paramName = en.nextElement();
			logger.debug("Init param " + paramName + "=" + context.getInitParameter(paramName));
		}
		//</DEBUG>
		
		Config c = Config.instance;
		Persistor p = Persistor.instance;
		IDGeneratorFactory idgf = IDGeneratorFactory.instance;
		Servers s = Servers.instance;

		logger.info("MZID: Singletons initialized successfully.");
	}
	
	static ServletContext getServletContext(){
		return context;
	}
}
