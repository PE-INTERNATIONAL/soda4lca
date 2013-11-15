package eu.europa.ec.jrc.lca.registry.delegate;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.jrc.lca.registry.domain.Node;

public abstract class RestServiceBD {
	protected Client client;

	protected String restServletUrl;
	
	private static final String REST_PATH = "/resource";

	protected RestServiceBD(Node node) {
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		client = Client.create(cc);
		this.restServletUrl = node.getBaseUrl() + REST_PATH;
	}
	
	protected WebResource getResource(String servicePath){
		return client.resource(restServletUrl + servicePath);
	}

	protected ClientResponse getResponse(String servicePath){
		return getResource(servicePath).get(ClientResponse.class);
	}
}
