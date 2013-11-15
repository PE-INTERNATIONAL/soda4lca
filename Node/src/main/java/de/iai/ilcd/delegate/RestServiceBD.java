package de.iai.ilcd.delegate;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import de.iai.ilcd.xml.read.JAXBContextResolver;

public abstract class RestServiceBD {

	protected Client client;

	protected String restServletUrl;

	protected RestServiceBD( String serviceBasicUrl ) {
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add( JAXBContextResolver.class );
		cc.getProperties().put( ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true );
		client = Client.create( cc );
		this.restServletUrl = serviceBasicUrl;
	}

	protected WebResource getResource( String servicePath ) {
		return client.resource( restServletUrl + servicePath );
	}

	protected ClientResponse getResponse( String servicePath ) {
		return getResource( servicePath ).get( ClientResponse.class );
	}
}
