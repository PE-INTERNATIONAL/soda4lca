package eu.europa.ec.jrc.lca.registry.service;

import javax.mail.internet.AddressException;

import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeRegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.Node;

public interface NodeRegistrationService {
	/**
	 * Registering node in registry
	 * @param node
	 * @return
	 * @throws NodeRegistrationException - node with same url or nodeId already exists
	 * @throws RestWSUnknownException - can't communicate with node
	 * @throws AddressException - node admin's email address is invalid
	 */
	RegistryCredentials registerNode(Node node)
			throws NodeRegistrationException, RestWSUnknownException, AddressException ;

	/**
	 * Accepting node registration request
	 * @param node
	 * @return
	 * @throws NodeIllegalStatusException - node is already accepted
	 */
	Node acceptNodeRegistration(Node node) throws NodeIllegalStatusException;

	/**
	 * Rejecting node registration request
	 * @param node
	 * @param reason
	 * @throws NodeIllegalStatusException - node is already accepted
	 */
	void rejectNodeRegistration(Node node, String reason)
			throws NodeIllegalStatusException;

	// For test purposes only
	void setBroadcastingService(BroadcastingService broadcastingService);
}
