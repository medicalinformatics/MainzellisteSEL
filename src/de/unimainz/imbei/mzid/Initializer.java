package de.unimainz.imbei.mzid;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import de.unimainz.imbei.mzid.dto.Persistor;

/**
 * This class is responsible for setting up all singletons in the right order
 * and to fail early if anything goes wrong.
 * 
 * @author Martin Lablans
 */
@Provider public class Initializer extends SingletonTypeInjectableProvider<Context, Config> {
	public Initializer() {
		super(Config.class, Config.instance);
		System.err.println("MZID: Initializing Singletons...");
		
		Config c = Config.instance;
		Persistor p = Persistor.instance;
		IDGeneratorFactory idgf = IDGeneratorFactory.instance;
		Servers s = Servers.instance;

		System.err.println("MZID: Singletons initialized successfully.");
	}
}
