package eu.europa.ec.jrc.lca.registry.dao;

import java.util.List;

import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.Node;

public interface NodeDao extends GenericDAO<Node, Long> {

	Long getCountByNodeIdAndUrl(String nodeId, String url);

	Node getByNodeId(String nodeID);
	
	List<Node> getListOfNotApprovedNodes();
	
	List<Node> getListOfApprovedNodes();
	
	List<Node> getListOfNodesForDatasetsWithStatus(DataSetStatus[] status);
}
