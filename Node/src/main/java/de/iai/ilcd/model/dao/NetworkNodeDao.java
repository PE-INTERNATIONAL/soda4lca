/*******************************************************************************
 * Copyright (c) 2011 Karlsruhe Institute of Technology (KIT) - Institute for
 * Applied Computer Science (IAI).
 * 
 * This file is part of soda4LCA - the Service-Oriented Life Cycle Data Store.
 * 
 * soda4LCA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * soda4LCA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with soda4LCA. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package de.iai.ilcd.model.dao;

import java.io.PrintWriter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.model.nodes.NetworkNode;
import de.iai.ilcd.persistence.PersistenceUtil;
import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.service.LazyLoader;

/**
 * 
 * @author clemens.duepmeier
 */
public class NetworkNodeDao extends GenericDAOImpl<NetworkNode, Long> implements LazyLoader<NetworkNode> {

	private static final Logger logger = LoggerFactory.getLogger( NetworkNodeDao.class );

	@SuppressWarnings( "unchecked" )
	/**
	 * Get List of all network nodes
	 * 
	 * @return list of all network nodes
	 */
	public List<NetworkNode> getNetworkNodes() {
		EntityManager em = PersistenceUtil.getEntityManager();

		List<NetworkNode> networkNodes = (List<NetworkNode>) em.createQuery( "select node from NetworkNode node" ).getResultList();

		return networkNodes;
	}

	/**
	 * Get a network node by its node ID (not database id!)
	 * 
	 * @param nodeId
	 *            {@link NetworkNode#getNodeId() node id} (not database id!)
	 */
	public NetworkNode getNetworkNode( String nodeId, Long registryId ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		NetworkNode networkNode = null;
		try {
			networkNode = (NetworkNode) em.createQuery( "select node from NetworkNode node where node.nodeId=:nodeId and node.registry.id=:registryId" )
					.setParameter( "nodeId", nodeId ).setParameter( "registryId", registryId ).getSingleResult();
		}
		catch ( NoResultException e ) {
			// we do nothing here; just return null
		}
		return networkNode;
	}

	public NetworkNode getNetworkNode( String nodeId ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		NetworkNode networkNode = null;
		try {
			networkNode = (NetworkNode) em.createQuery( "select node from NetworkNode node where node.nodeId=:nodeId" ).setParameter( "nodeId", nodeId )
					.getSingleResult();
		}
		catch ( NoResultException e ) {
			// we do nothing here; just return null
		}
		return networkNode;
	}

	public NetworkNode getNetworkNode( long id ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		NetworkNode networkNode = (NetworkNode) em.find( NetworkNode.class, Long.valueOf( id ) );

		return networkNode;
	}

	public NetworkNode getUnitGroupById( String id ) {
		return getNetworkNode( Long.parseLong( id ) );
	}

	/**
	 * Get Node by database id
	 * 
	 * @param id
	 *            id of node
	 * @return loaded node
	 */
	public NetworkNode getNetworkNodeById( Long id ) {
		return getNetworkNode( id );
	}

	public boolean checkAndPersist( NetworkNode networkNode, PersistType pType, PrintWriter out ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		NetworkNode existingNode = this.getNetworkNode( networkNode.getNodeId() );
		if ( existingNode != null ) {
			if ( pType == PersistType.ONLYNEW ) {
				if ( out != null )
					out.println( "Warning: Network node with this nodeId already exists; change name or choose option to overwrite" );
				return false;
			}
		}
		try {
			em.getTransaction().begin();
			if ( existingNode != null && (pType == PersistType.MERGE) ) {
				if ( out != null ) {
					out.println( "Notice: network node with this nodeId already exists; will replace it with this entry" );
				}
				// delete first the existing one, we will use the new one
				em.remove( existingNode );
			}

			em.persist( networkNode );
			em.getTransaction().commit();
			return true;
		}
		catch ( Exception e ) {
			logger.error( "cannot persist network node {}", networkNode.getNodeId() );
			logger.error( "stacktrace is: ", e );
			em.getTransaction().rollback();
		}
		return false;
	}

	public void remove( NetworkNode node ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			NetworkNode managed = em.find( NetworkNode.class, node.getId() );
			em.remove( managed );
			em.getTransaction().commit();
		}
		catch ( Exception e ) {
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	public NetworkNode merge( NetworkNode node ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge( node );
			em.getTransaction().commit();
			return node;
		}
		catch ( Exception e ) {
			logger.error( "cannot merge changes to network node {}", node.getNodeId() );
			logger.error( "stacktrace is: ", e );
			em.getTransaction().rollback();
		}
		return null;
	}

	@Override
	public List<NetworkNode> loadLazy( SearchParameters sp ) {
		return find( sp );
	}

	public List<NetworkNode> getRemoteNetworkNodesFromRegistry( String registryUUID ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em
				.createQuery( "select node from NetworkNode node where node.registry.uuid=:registryUUID and node.nodeId!=node.registry.nodeCredentials.nodeId" )
				.setParameter( "registryUUID", registryUUID ).getResultList();
	}

	public List<NetworkNode> getRemoteNetworkNodes() {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery( "select node from NetworkNode node where node.registry is null" ).getResultList();
	}

}
