package de.iai.ilcd.services;

import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnexpectedStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;

public interface Synchronizator {

	/**
	 * Synchronizes nodes from registry
	 * 
	 * @param reg
	 * @throws AuthenticationException
	 * @throws RestWSUnexpectedStatusException
	 * @throws RestWSUnknownException
	 */
	void synchronizeNodes( Registry reg ) throws AuthenticationException, RestWSUnexpectedStatusException, RestWSUnknownException;

	/**
	 * Synchronizes datasets from registry
	 * 
	 * @param reg
	 * @throws AuthenticationException
	 * @throws RestWSUnexpectedStatusException
	 * @throws RestWSUnknownException
	 */
	void synchronizeDataSets( Registry reg ) throws AuthenticationException, RestWSUnexpectedStatusException, RestWSUnknownException;
}
