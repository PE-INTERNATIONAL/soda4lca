package eu.europa.ec.jrc.lca.registry.service;

import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;


public interface BroadcastingService {
	
	/**
	 * Broadcasts information about node change (de/registration) to all nodes
	 * @param log - object containing information about performed operation
	 */
	void broadcastNodesChangeInformation(NodeChangeLog log);
	
	/**
	 * Sends information about deregistration to deregistered node
	 * @param log
	 * @param n
	 * @param rc
	 */
	void broadcastNodesChangeInformationToDeregistered(NodeChangeLog log, Node n, RegistryCredentials rc);
	
	/**
	 * Broadcasts information about dataset deregistration to the node
	 * @param dataSet
	 */
	void broadcastDatasetDeregistrationInformation(DataSet dataSet);
}
