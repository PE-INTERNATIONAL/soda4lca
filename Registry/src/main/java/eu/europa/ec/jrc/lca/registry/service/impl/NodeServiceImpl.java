package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.registry.dao.NodeDao;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;
import eu.europa.ec.jrc.lca.registry.service.NodeService;

@Transactional
@Service("nodeService")
public class NodeServiceImpl implements NodeService {

	@Autowired
	private NodeDao nodeDao;

	@Override
	public List<Node> loadLazy(SearchParameters sp) {
		return nodeDao.find(sp);
	}

	@Override
	public Long count(SearchParameters sp) {
		return nodeDao.count(sp);
	}

	@Override
	public List<Node> wake(){
		return nodeDao.getListOfApprovedNodes();
	}

	@Override
	public List<Node> getListOfNotApprovedNodes() {
		return nodeDao.getListOfNotApprovedNodes();
	}

	@Override
	public Node findByNodeId(String id) {
		try {
			return nodeDao.getByNodeId(id);
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public NodeStatus checkAcceptance(Node node)
			throws ResourceNotFoundException {
		try {
			Node storedNode = nodeDao.getByNodeId(node.getNodeId());
			return storedNode.getStatus();
		} catch (NoResultException e) {
			throw new ResourceNotFoundException("Can't find node with id="
					+ node.getNodeId());
		}
	}

	@Override
	public List<Node> getListOfNodesForNotAcceptedDatasets() {
		return nodeDao.getListOfNodesForDatasetsWithStatus(new DataSetStatus[]{DataSetStatus.NEW, DataSetStatus.NEW_ALTERNATIVE, DataSetStatus.UPDATE});
	}

	@Override
	public List<Node> getListOfNodesForAcceptedDatasets() {
		return nodeDao.getListOfNodesForDatasetsWithStatus(new DataSetStatus[]{DataSetStatus.ACCEPTED});
	}

	@Override
	public Node findById(Long id) {
		return nodeDao.findById(id);
	}

}
