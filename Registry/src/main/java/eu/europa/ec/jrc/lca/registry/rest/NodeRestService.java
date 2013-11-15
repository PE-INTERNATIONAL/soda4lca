package eu.europa.ec.jrc.lca.registry.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.rest.CustomStatus;
import eu.europa.ec.jrc.lca.commons.rest.dto.SecuredRequestWrapper;
import eu.europa.ec.jrc.lca.commons.security.annotations.Secured;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeRegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;
import eu.europa.ec.jrc.lca.registry.service.NodeDeregistrationService;
import eu.europa.ec.jrc.lca.registry.service.NodeRegistrationService;
import eu.europa.ec.jrc.lca.registry.service.NodeService;

@Path("/nodeservice")
@Service("nodeRestService")
public class NodeRestService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NodeRestService.class);
	
	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private NodeRegistrationService nodeRegistrationService;
	
	@Autowired
	private NodeDeregistrationService nodeDeregistrationService;

	/**
	 * Registering node
	 * @param node
	 * @return
	 */
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_XML)
	public Response registerNode(Node node) {
		try {
			RegistryCredentials result = nodeRegistrationService.registerNode(node);
			return Response.ok(result).build();
		} catch (NodeRegistrationException e) {
			throw new WebApplicationException(e, Status.CONFLICT);
		} catch (RestWSUnknownException e) {
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		} catch(AddressException e){
			throw new WebApplicationException(e, Status.NOT_ACCEPTABLE);
		}
	}
	
	/**
	 * Checking node registration request acceptance
	 * @param node
	 * @return node registration request status
	 */
	@PUT
	@Path("/checkAcceptance")
	@Produces(MediaType.APPLICATION_XML)
	public Response checkAcceptance(Node node) {
		try {
			NodeStatus status = nodeService.checkAcceptance(node);
			JAXBElement<String> nodeStatus = new JAXBElement<String>(new QName("nodeStatus"), String.class, status.toString());
			return Response.ok(nodeStatus).build();
		} catch (ResourceNotFoundException e) {
			throw new WebApplicationException(e, CustomStatus.ENTITY_NOT_FOUND.getCode());
		}
		
	}
	
	/**
	 * Getting node information
	 * @param nodeId
	 * @return node
	 */
	@GET
	@Path("/node/{nodeId}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getNode(@PathParam("nodeId") String nodeId) {
		Node node = null;
		try {
			node = nodeService.findByNodeId(URLDecoder.decode(nodeId, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("[getNode]", e);
		}
		if(node==null){
			throw new WebApplicationException(CustomStatus.ENTITY_NOT_FOUND.getCode());
		}
		return Response.ok(node).build();
	}
	
	/** 
	 * Getting list of registered nodes
	 * @param req
	 * @return List of nodes
	 */
	@PUT
	@Path("/wake")
	@Produces(MediaType.APPLICATION_XML)
	@Secured
	public Response wake(SecuredRequestWrapper req) {
		GenericEntity<List<Node>> genericEntity = new GenericEntity<List<Node>>(
				nodeService.wake()) {
		};
		return Response.ok(genericEntity).build();
	}
	
	/**
	 * Deregistering node
	 * @param req
	 * @return
	 */
	@PUT
	@Path("/deregister")
	@Produces(MediaType.APPLICATION_XML)
	@Secured
	public Response deregisterNode(SecuredRequestWrapper req) {
		try {
			NodeChangeLog log = nodeDeregistrationService.deregisterNode(((NodeCredentials)req.getCredentials()).getNodeId());
			nodeDeregistrationService.broadcastNodeDeregistration(log);
		} catch (ResourceNotFoundException e) {
			throw new WebApplicationException(e,CustomStatus.ENTITY_NOT_FOUND.getCode());
		}
		return Response.ok().build();
	}

}
