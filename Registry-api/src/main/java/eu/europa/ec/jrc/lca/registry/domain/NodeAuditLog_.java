package eu.europa.ec.jrc.lca.registry.domain;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(eu.europa.ec.jrc.lca.registry.domain.NodeAuditLog.class)
public class NodeAuditLog_ {
	public static volatile SingularAttribute<NodeAuditLog, Long> id;
	public static volatile SingularAttribute<NodeAuditLog, Long> nodeId;
	public static volatile SingularAttribute<NodeAuditLog, Long> operationTime;
	public static volatile SingularAttribute<NodeAuditLog, NodeOperation> operationType;
	public static volatile SingularAttribute<NodeAuditLog, String> objectName;
	public static volatile ListAttribute<NodeAuditLog, NodeOperation> datasetLog;
}
