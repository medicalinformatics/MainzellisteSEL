package de.pseudonymisierung.mainzelliste.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class HttpReceiver {

	private HttpServer server;
	private String receivedEntity;
	private HttpExchange receivedRequest;
	
	
	/**
	 * @param receivedEntity the receivedEntity to set
	 */
	protected void setReceivedEntity(String receivedEntity) {
		this.receivedEntity = receivedEntity;
	}

	/**
	 * @param receivedRequest the receivedRequest to set
	 */
	protected void setReceivedRequest(HttpExchange receivedRequest) {
		this.receivedRequest = receivedRequest;
	}

	/**
	 * @return the receivedEntity
	 */
	public String getReceivedEntity() {
		return receivedEntity;
	}
	
	public HttpReceiver(int port, String context) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 1);
        server.createContext(context, new MyHandler());
        server.setExecutor(null); // creates a default executor
	}

	public void start() {
		receivedEntity = null;
		server.start();
	}
	
	public void stop() {
		server.stop(0);
	}
	
    public HttpExchange getReceivedRequest() {
		return receivedRequest;
	}

	private class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	HttpReceiver.this.receivedRequest = t;
        	InputStream requestBodyStream = t.getRequestBody();
        	StringWriter requestWriter = new StringWriter();        	
        	IOUtils.copy(requestBodyStream, requestWriter, "UTF8");
            HttpReceiver.this.receivedEntity = requestWriter.toString();
            t.sendResponseHeaders(200, 0);
        }
    }
}