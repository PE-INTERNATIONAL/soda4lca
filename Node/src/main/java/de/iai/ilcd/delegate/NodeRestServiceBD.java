package de.iai.ilcd.delegate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.xml.bind.JAXBElement;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.registry.RegistryStatus;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.rest.CustomStatus;
import eu.europa.ec.jrc.lca.commons.rest.dto.KeyWrapper;
import eu.europa.ec.jrc.lca.commons.rest.dto.SecuredRequestWrapper;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeRegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnexpectedStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;

/*
 * Business Delegate class - encapsulates remote service methods invocations
 */

public class NodeRestServiceBD extends RestServiceBD {

	private static Map<String, RSAPublicKeySpec> keys = new HashMap<String, RSAPublicKeySpec>();

	private static final String REST_PATH = "rest";

	private NodeRestServiceBD( String serviceBasicUrl ) {
		super( serviceBasicUrl );
	}

	public static NodeRestServiceBD getInstance( Registry registry ) {
		return new NodeRestServiceBD( registry.getBaseUrl() + REST_PATH );
	}

	public RegistryCredentials register( Node node ) throws NodeRegistrationException, RestWSUnexpectedStatusException, RestWSUnknownException,
			AddressException {
		try {
			WebResource wr = getResource( "/nodeservice/register" );
			ClientResponse cr = wr.post( ClientResponse.class, node );
			if ( cr.getClientResponseStatus().equals( Status.CONFLICT ) ) {
				throw new NodeRegistrationException();
			}
			if ( cr.getClientResponseStatus().equals( Status.NOT_ACCEPTABLE ) ) {
				throw new AddressException();
			}
			if ( !cr.getClientResponseStatus().equals( Status.OK ) ) {
				throw new RestWSUnexpectedStatusException( cr.getStatus() );
			}
			RegistryCredentials result = cr.getEntity( RegistryCredentials.class );
			return result;
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
	}

	public RegistryStatus checkAcceptance( Node node ) throws RestWSUnknownException {
		try {
			WebResource wr = getResource( "/nodeservice/checkAcceptance" );
			ClientResponse cr = wr.put( ClientResponse.class, node );
			if ( cr.getStatus() == CustomStatus.ENTITY_NOT_FOUND.getCode() ) {
				return RegistryStatus.NOT_REGISTERED;
			}

			JAXBElement<String> statusJaxbEl = cr.getEntity( new GenericType<JAXBElement<String>>() {
			} );

			NodeStatus status = NodeStatus.valueOf( (String) statusJaxbEl.getValue() );
			if ( status == NodeStatus.NOT_APPROVED ) {
				return RegistryStatus.PENDING_REGISTRATION;
			}
			return RegistryStatus.REGISTERED;
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
	}

	public List<Node> wake( NodeCredentials credentials ) throws AuthenticationException, RestWSUnknownException, RestWSUnexpectedStatusException {
		try {
			WebResource wr = getResource( "/nodeservice/wake" );
			ClientResponse cr = wr.put( ClientResponse.class, new SecuredRequestWrapper( credentials ) );
			if ( cr.getClientResponseStatus().equals( Status.UNAUTHORIZED ) ) {
				throw new AuthenticationException();
			}
			else if ( !cr.getClientResponseStatus().equals( Status.OK ) ) {
				throw new RestWSUnexpectedStatusException( cr.getStatus() );
			}
			return cr.getEntity( new GenericType<List<Node>>() {
			} );
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
	}

	public Node getNodeData( String nodeId ) throws RestWSUnknownException {
		try {
			ClientResponse cr = getResponse( "/nodeservice/node/" + URLEncoder.encode( nodeId, "UTF-8" ) );
			if ( cr.getStatus() == CustomStatus.ENTITY_NOT_FOUND.getCode() ) {
				return null;
			}
			return cr.getEntity( Node.class );
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
		catch ( UnsupportedEncodingException e ) {
			return null;
		}
	}

	public void deregister( NodeCredentials credentials ) throws AuthenticationException, RestWSUnknownException, RestWSUnexpectedStatusException {
		try {
			WebResource wr = getResource( "/nodeservice/deregister" );
			ClientResponse cr = wr.put( ClientResponse.class, new SecuredRequestWrapper( credentials ) );
			if ( cr.getClientResponseStatus().equals( Status.UNAUTHORIZED ) ) {
				throw new AuthenticationException();
			}
			if ( !cr.getClientResponseStatus().equals( Status.OK ) ) {
				throw new RestWSUnexpectedStatusException( cr.getStatus() );
			}
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
	}

	public RSAPublicKeySpec getPublicKey() throws RestWSUnknownException {
		if ( keys.get( restServletUrl ) == null ) {
			try {
				ClientResponse cr = getResponse( "/key/public" );
				KeyWrapper kw = cr.getEntity( KeyWrapper.class );
				keys.put( restServletUrl, kw.getRSAPublicKeySpec() );
			}
			catch ( ClientHandlerException ex ) {
				throw new RestWSUnknownException( ex.getMessage() );
			}
		}
		return keys.get( restServletUrl );
	}
}
