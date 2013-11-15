package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.Date;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeDegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.registry.dao.DataSetDao;
import eu.europa.ec.jrc.lca.registry.dao.NodeChangeLogDao;
import eu.europa.ec.jrc.lca.registry.dao.NodeDao;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;
import eu.europa.ec.jrc.lca.registry.domain.NodeOperation;
import eu.europa.ec.jrc.lca.registry.domain.OperationType;
import eu.europa.ec.jrc.lca.registry.service.AuditLogger;
import eu.europa.ec.jrc.lca.registry.service.BroadcastingService;
import eu.europa.ec.jrc.lca.registry.service.NodeDeregistrationService;
import eu.europa.ec.jrc.lca.registry.service.NotificationService;

@Transactional
@Service("nodeDeregistrationService")
public class NodeDeregistrationServiceImpl implements NodeDeregistrationService {

	@Autowired
	private NodeDao nodeDao;

	@Autowired
	private NodeChangeLogDao nodeChangeLogDao;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private BroadcastingService broadcastingService;

	@Autowired
	private DataSetDao dataSetDao;
	
	@Autowired
	private AuditLogger auditLogger;

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public NodeChangeLog deregisterNode(String nodeId) throws ResourceNotFoundException {
		try {
			Node storedNode = nodeDao.getByNodeId(nodeId);
			
			clearDatasets(storedNode);
			nodeDao.remove(storedNode.getId());
			
			notificationService
					.notifyRegistryAdminsAboutNodeDeregistrationRequest(storedNode);
			
			auditLogger.log(storedNode, NodeOperation.DEREGISTRATION);
			
			return logOperation(OperationType.DEREGISTER,
					storedNode);
		} catch (NoResultException e) {
			throw new ResourceNotFoundException("Can't find node with id="
					+ nodeId);
		}
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void broadcastNodeDeregistration(NodeChangeLog log){
		broadcastingService.broadcastNodesChangeInformation(log);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void broadcastNodesChangeInformationToDeregistered(NodeChangeLog log, Node node, RegistryCredentials rc){
		broadcastingService.broadcastNodesChangeInformationToDeregistered(log, node, rc);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public NodeChangeLog deregisterNode(Node node, String reason)
			throws NodeDegistrationException {
		validateDatasets(node);
		clearDatasets(node);
		nodeDao.remove(node.getId());
		NodeChangeLog log = logOperation(OperationType.DEREGISTER, node);
		notificationService.notifyNodeAdminAboutNodeDeregistration(node, reason);
		auditLogger.log(node, NodeOperation.DEREGISTRATION);
		return log;
	}
	
	@Override
	public void setBroadcastingService(BroadcastingService broadcastingService) {
		this.broadcastingService = broadcastingService;
	}

	private NodeChangeLog logOperation(OperationType type, Node node) {
		NodeChangeLog ncl = new NodeChangeLog();
		ncl.setNodeId(node.getNodeId());
		ncl.setOperationDate(new Date());
		ncl.setOperationType(type);
		return nodeChangeLogDao.saveOrUpdate(ncl);
	}

	private void clearDatasets(Node node){
		dataSetDao.removeByNode(node.getNodeId());
	}
	
	private void validateDatasets(Node node) throws NodeDegistrationException{
		if(dataSetDao.findAcceptedByNode(node.getNodeId()).size()!=0){
			throw new NodeDegistrationException("there are registered datasets");
		}
	}
}
