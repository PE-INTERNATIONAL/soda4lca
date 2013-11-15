package eu.europa.ec.jrc.lca.registry.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.registry.domain.DataSetAuditLog;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeAuditLog;

@Component
public class FileLogger {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(FileLogger.class);

	@Autowired
	private ILCDMessageSource messageSource;
	
	public void log(NodeAuditLog nodeAuditLog, Node node){
		if(nodeAuditLog.getDatasetLog().isEmpty()){
			logNodeOperation(nodeAuditLog, node);
		}
		else{
			logNodeWithDatasetsOperation(nodeAuditLog, node);
		}
	}
	
	private void logNodeOperation(NodeAuditLog nodeAuditLog, Node node){
		StringBuilder sb = new StringBuilder();
		sb.append(messageSource.getTranslation(nodeAuditLog.getOperationType().name()));
		sb.append(", node id: ").append(node.getNodeId());
		LOGGER.info(sb.toString());
	}
	
	private void logNodeWithDatasetsOperation(NodeAuditLog nodeAuditLog, Node node){
		StringBuilder sb = new StringBuilder();
		sb.append(messageSource.getTranslation(nodeAuditLog.getOperationType().name()));
		sb.append(", node id: ").append(node.getNodeId());
		for(DataSetAuditLog dsal: nodeAuditLog.getDatasetLog()){
			sb.append("<br>").append("Data set UUID: ").append(dsal.getUuid()).append(", version: ").append(dsal.getVersion());
		}
		LOGGER.info(sb.toString());
	}
}
