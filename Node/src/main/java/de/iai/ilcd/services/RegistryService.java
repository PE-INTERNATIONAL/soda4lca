package de.iai.ilcd.services;

import java.util.List;

import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.service.LazyLoader;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;

public interface RegistryService extends LazyLoader<Registry> {

	/**
	 * Finding registry by internal id
	 * 
	 * @param identity
	 * @return
	 */
	Registry findByIdentity( String identity );

	/**
	 * Finding by DB id
	 * 
	 * @param id
	 * @return
	 */
	Registry findById( Long id );

	/**
	 * Finding by UUID
	 * 
	 * @param uuid
	 * @return
	 */
	Registry findByUUID( String uuid );

	/**
	 * Finding by URL
	 * 
	 * @param url
	 * @return
	 */
	Registry findByUrl( String url );

	/**
	 * @param registry
	 * @return
	 */
	Registry saveOrUpdate( Registry registry );

	/**
	 * @return List of non virtual registries in which node is registered
	 */
	List<Registry> getNonVirtualRegistriesInWhichRegistered();

	/**
	 * @return List of registries in which node is registered
	 */
	List<Registry> getRegistriesInWhichRegistered();

	/**
	 * @return List of registries in which node is registered but not accepted yet
	 */
	List<Registry> getRegistriesToCheckAcceptance();

	/**
	 * @param reg
	 * @return List of nodes registered in given registry
	 */
	Node getNodeForRegistry( Registry reg );

	/**
	 * @param registryId
	 * @param changeLog
	 * @throws RestWSUnknownException
	 */
	void applyChange( String registryId, NodeChangeLog changeLog ) throws RestWSUnknownException;

	/**
	 * Removes registry
	 * 
	 * @param registryId
	 */
	void removeRegistry( Long registryId );

	/**
	 * Removes node
	 * 
	 * @param nodeId
	 */
	void removeNode( Long nodeId );
}
