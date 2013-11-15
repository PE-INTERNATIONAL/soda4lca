package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.dao.NodeDao;
import eu.europa.ec.jrc.lca.registry.delegate.NodeRestServiceBD;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;
import eu.europa.ec.jrc.lca.registry.service.BroadcastingService;

@Service("broadcastingService")
@Transactional
public class BroadcastingServiceImpl implements BroadcastingService{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BroadcastingServiceImpl.class);
	
	@Autowired
	private NodeDao nodeDao;
	
	
	@Override
	@Async
	public void broadcastNodesChangeInformation(NodeChangeLog log){
		LOGGER.info("broadcasting nodes change info");		
		List<Node> activeNodes = nodeDao.getListOfApprovedNodes();
		for(Node n: activeNodes){
			if(n.getNodeId().equals(log.getNodeId())){
				continue;
			}
			try {
				NodeRestServiceBD.getInstance(n).informAboutChanges(log);
			} catch (RestWSUnknownException e) {
				LOGGER.error("[broadcastNodesChangeInformation]",e);
			}
		}
	}

	@Override
	public void broadcastNodesChangeInformationToDeregistered(NodeChangeLog log, Node n, RegistryCredentials rc){
		LOGGER.info("broadcasting nodes change info to deregistered");		
		try {
			NodeRestServiceBD.getInstance(n, rc).informAboutChanges(log);
		} catch (RestWSUnknownException e) {
			LOGGER.error("[broadcastNodesChangeInformationToDeregistered]",e);
		}
	}

	@Override
	public void broadcastDatasetDeregistrationInformation(DataSet dataSet) {
		LOGGER.info("broadcasting dataset deregistration info");	
		try {
			NodeRestServiceBD.getInstance(dataSet.getNode()).informAboutDatasetDeregistration(dataSet);
		} catch (RestWSUnknownException e) {
			LOGGER.error("[broadcastDatasetDeregistrationInformation]",e);
		}
	}

}
