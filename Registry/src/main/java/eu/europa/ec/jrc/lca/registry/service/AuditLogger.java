package eu.europa.ec.jrc.lca.registry.service;

import java.util.List;

import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeOperation;

public interface AuditLogger {
	
	/**
	 * Logs operations performed on node
	 * @param node
	 * @param operation
	 */
	void log(Node node, NodeOperation operation);

	/**
	 * Logs operations performed on datasets in scope of node
	 * @param dataSets
	 * @param node
	 * @param operation
	 */
	void log(List<DataSet> dataSets, Node node, NodeOperation operation);
	 
	/**
	 * Logs operations performed on datasets in scope of node
	 * @param dataSets
	 * @param operation
	 */
	void log(List<DataSet> dataSets, NodeOperation operation);
}
