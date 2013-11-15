package eu.europa.ec.jrc.lca.registry.service.impl;

import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.security.SecurityContext;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeRegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.dao.NodeChangeLogDao;
import eu.europa.ec.jrc.lca.registry.dao.NodeDao;
import eu.europa.ec.jrc.lca.registry.delegate.NodeRestServiceBD;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;
import eu.europa.ec.jrc.lca.registry.domain.NodeOperation;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;
import eu.europa.ec.jrc.lca.registry.domain.OperationType;
import eu.europa.ec.jrc.lca.registry.service.AuditLogger;
import eu.europa.ec.jrc.lca.registry.service.BroadcastingService;
import eu.europa.ec.jrc.lca.registry.service.NodeRegistrationService;
import eu.europa.ec.jrc.lca.registry.service.NotificationService;

@Transactional
@Service("nodeRegistrationService")
public class NodeRegistrationServiceImpl implements NodeRegistrationService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(NodeRegistrationServiceImpl.class);

	@Autowired
	private NodeDao nodeDao;

	@Autowired
	private NodeChangeLogDao nodeChangeLogDao;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private BroadcastingService broadcastingService;

	@Autowired
	private SecurityContext securityContext;
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Override
	@Transactional(rollbackFor = { NodeRegistrationException.class,
			RestWSUnknownException.class })
	public RegistryCredentials registerNode(Node node)
			throws NodeRegistrationException, RestWSUnknownException, AddressException {
		
		validateNodeRegistrationData(node);
		
		RSAPublicKeySpec key = getNodePublicKey(node);
		node.setStatus(NodeStatus.NOT_APPROVED);
		node.setRegistryCredentials(generateRegistryCredentials());
		Node result = nodeDao.saveOrUpdate(node);

		notificationService
				.notifyRegistryAdminsAboutNodeRegistrationRequest(result);

		auditLogger.log(result, NodeOperation.REGISTRATION);
		
		return node.getRegistryCredentials().getEncryptedCopy(key);
	}

	private void validateNodeRegistrationData(Node node) throws NodeRegistrationException, AddressException{
		Long countByNodeId = nodeDao.getCountByNodeIdAndUrl(node.getNodeId(),
				node.getBaseUrl());
		if (countByNodeId != 0) {
			throw new NodeRegistrationException("Node with id: "
					+ node.getNodeId() + " or with url: " + node.getBaseUrl()
					+ " already exists");
		}
		
		validateEmailAddress(node.getAdminEmailAddress());
	}
	
	private void validateEmailAddress(String email) throws AddressException {
		if(email==null){
			throw new AddressException();
		}
		else{
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		}
	}

	private RegistryCredentials generateRegistryCredentials() {
		RegistryCredentials cred = new RegistryCredentials();
		cred.setAccessAccount(String.valueOf(new Date().getTime()));
		cred.setAccessPassword(UUID.randomUUID().toString());
		cred.encrypt(securityContext.getPublicKey());
		return cred;
	}

	@Override
	@Transactional(rollbackFor = { NodeIllegalStatusException.class,
			RestWSUnknownException.class, AuthenticationException.class })
	public Node acceptNodeRegistration(Node node)
			throws NodeIllegalStatusException{
		Node storedNode = nodeDao.findById(node.getId());
		if (!storedNode.getStatus().equals(NodeStatus.NOT_APPROVED)) {
			throw new NodeIllegalStatusException(
					"Node is not in Not Accepted state");
		}
		storedNode.setStatus(NodeStatus.APPROVED);
		storedNode = nodeDao.saveOrUpdate(storedNode);

		notificationService.notifyNodeAdminAboutNodeAcceptance(storedNode);
		NodeChangeLog log = logOperation(OperationType.REGISTER, storedNode);
		broadcastingService.broadcastNodesChangeInformation(log);

		LOGGER.info("node accepted");
		auditLogger.log(storedNode, NodeOperation.REGISTRATION_ACCEPTANCE);
		
		return storedNode;
	}

	@Override
	public void rejectNodeRegistration(Node node, String reason)
			throws NodeIllegalStatusException {
		Node storedNode = nodeDao.findById(node.getId());
		if (storedNode == null
				|| !NodeStatus.NOT_APPROVED.equals(storedNode.getStatus())) {
			throw new NodeIllegalStatusException(
					"Node is not in Not Accepted state");
		}
		nodeDao.remove(storedNode);
		notificationService.notifyNodeAdminAboutNodeRejection(storedNode, reason);
		auditLogger.log(storedNode, NodeOperation.REGISTRATION_REJECTION);
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

	private RSAPublicKeySpec getNodePublicKey(Node node)
			throws RestWSUnknownException {
		return NodeRestServiceBD.getInstance(node).getPublicKey();
	}
	

}
