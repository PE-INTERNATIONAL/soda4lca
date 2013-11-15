package eu.europa.ec.jrc.lca.registry.service;

import eu.europa.ec.jrc.lca.commons.service.LazyLoader;
import eu.europa.ec.jrc.lca.registry.domain.NodeAuditLog;

public interface NodeHistoryService extends LazyLoader<NodeAuditLog> {
	/**
	 * @param id - node DB id
	 * @return List of operations performed on node
	 */
	public NodeAuditLog getWithDetails(Long id);
}
