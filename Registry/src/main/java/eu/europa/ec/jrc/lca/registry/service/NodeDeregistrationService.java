package eu.europa.ec.jrc.lca.registry.service;

import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeDegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;

public interface NodeDeregistrationService {
	
	/**
	 * Deregistering node
	 * @param nodeId
	 * @return
	 * @throws ResourceNotFoundException - when requested node doesn't exist
	 */
	NodeChangeLog deregisterNode(String nodeId) throws ResourceNotFoundException;
	
	/**
	 * Deregistering node
	 * @param node
	 * @param reason
	 * @return
	 * @throws NodeDegistrationException - when requested node doesn't exist
	 */
	NodeChangeLog deregisterNode(Node node, String reason) throws NodeDegistrationException;
	
	/**
	 * Broadcasting node deregistration information
	 * @param log
	 */
	void broadcastNodeDeregistration(NodeChangeLog log);
	
	/**
	 * @param log
	 * @param node
	 * @param rc
	 */
	void broadcastNodesChangeInformationToDeregistered(NodeChangeLog log, Node node, RegistryCredentials rc );

	// For test purposes only
	void setBroadcastingService(BroadcastingService broadcastingService);
}
