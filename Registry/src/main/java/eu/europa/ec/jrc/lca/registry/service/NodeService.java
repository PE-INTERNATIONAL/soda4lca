package eu.europa.ec.jrc.lca.registry.service;

import java.util.List;

import eu.europa.ec.jrc.lca.commons.service.LazyLoader;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;

public interface NodeService extends LazyLoader<Node> {

	/**
	 * @return List of nodes which are not approved
	 */
	List<Node> getListOfNotApprovedNodes();
	
	/**
	 * @return list of approved nodes
	 */
	List<Node> wake();
	
	/**
	 * Checking node acceptance status
	 * @param node
	 * @return
	 * @throws ResourceNotFoundException - node doesn;t exist in registry
	 */
	NodeStatus checkAcceptance(Node node) throws ResourceNotFoundException ;
	
	/**
	 * @param id
	 * @return
	 */
	Node findByNodeId(String id);
	
	/**
	 * @param id
	 * @return
	 */
	Node findById(Long id);
	
	/**
	 * @return list of nodes for which exist not accepted dataset registration requests
	 */
	List<Node> getListOfNodesForNotAcceptedDatasets();
	
	/**
	 * @return list of nodes for which exist accepted dataset registration requests
	 */
	List<Node> getListOfNodesForAcceptedDatasets();
}
