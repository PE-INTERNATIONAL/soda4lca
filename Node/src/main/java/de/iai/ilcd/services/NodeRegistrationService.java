package de.iai.ilcd.services;

import javax.mail.internet.AddressException;

import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeDegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeRegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnexpectedStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.Node;

public interface NodeRegistrationService {

	/**
	 * Registering node
	 * 
	 * @param registry
	 * @param node
	 * @throws NodeRegistrationException
	 * @throws RestWSUnexpectedStatusException
	 * @throws RestWSUnknownException
	 * @throws AddressException
	 */
	void registerNode( Registry registry, Node node ) throws NodeRegistrationException, RestWSUnexpectedStatusException, RestWSUnknownException,
			AddressException;

	/**
	 * Deregistering node
	 * 
	 * @param registry
	 * @param credentials
	 * @throws RestWSUnexpectedStatusException
	 * @throws RestWSUnknownException
	 * @throws AuthenticationException
	 * @throws NodeDegistrationException
	 */
	void deregisterNode( Registry registry, NodeCredentials credentials ) throws RestWSUnexpectedStatusException, RestWSUnknownException,
			AuthenticationException, NodeDegistrationException;

	/**
	 * Deregistering node. Callback method, invoked when deregistration is initiated by registry
	 * 
	 * @param registry
	 */
	void deregisterNode( Registry registry );

	/**
	 * @param registry
	 */
	void clearRegistrationData( Registry registry );

}
