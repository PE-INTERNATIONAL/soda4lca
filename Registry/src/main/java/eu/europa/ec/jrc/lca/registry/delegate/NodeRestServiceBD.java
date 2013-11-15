package eu.europa.ec.jrc.lca.registry.delegate;

import java.security.spec.RSAPublicKeySpec;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.rest.dto.KeyWrapper;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;

public final class NodeRestServiceBD extends RestServiceBD {
	private NodeRestServiceBD(Node node) {
		super(node);
		if(node.getRegistryCredentials()!=null){
			this.accessAccount = node.getRegistryCredentials().getAccessAccount();
		}
	}
	
	private NodeRestServiceBD(Node node, RegistryCredentials rc) {
		super(node);
		this.accessAccount = rc.getAccessAccount();
	}
	
	private String accessAccount;

	public static NodeRestServiceBD getInstance(Node node) {
		return new NodeRestServiceBD(node);
	}
	
	public static NodeRestServiceBD getInstance(Node node, RegistryCredentials rc) {
		return new NodeRestServiceBD(node, rc);
	}
	
	public void informAboutChanges(NodeChangeLog log) throws RestWSUnknownException {
		try {
			WebResource wr = getResource("/synchronization/inform/"+accessAccount);
			wr.put(ClientResponse.class, log);
		} catch (ClientHandlerException ex) {
			throw new RestWSUnknownException(ex.getMessage());
		}
	}

	public void informAboutDatasetDeregistration(DataSet ds) throws RestWSUnknownException {
		try {
			WebResource wr = getResource("/synchronization/informAboutDSDeregistration/"+accessAccount);
			wr.put(ClientResponse.class, ds);
		} catch (ClientHandlerException ex) {
			throw new RestWSUnknownException(ex.getMessage());
		}
	}

	
	public RSAPublicKeySpec getPublicKey() throws RestWSUnknownException {
		try {
			ClientResponse cr = getResponse("/key/public");
			KeyWrapper kw = cr.getEntity(KeyWrapper.class);
			return kw.getRSAPublicKeySpec();
		} catch (ClientHandlerException ex) {
			throw new RestWSUnknownException(ex.getMessage());
		}
	}
}
