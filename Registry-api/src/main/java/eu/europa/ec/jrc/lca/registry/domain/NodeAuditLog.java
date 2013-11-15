package eu.europa.ec.jrc.lca.registry.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="T_NODE_AUDIT_LOG")
public class NodeAuditLog implements Serializable{
	private static final long serialVersionUID = 5867867305932890037L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long nodeId;
	
	private Long operationTime;
	
	@Enumerated(EnumType.STRING)
	private NodeOperation operationType;
	
	private String objectName;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="NODE_AUDIT_LOG_ID") 
	private List<DataSetAuditLog> datasetLog = new ArrayList<DataSetAuditLog>();

	public NodeAuditLog(){
		
	}
	
	public NodeAuditLog(Node node){
		this.setNodeId(node.getId());
		this.setObjectName(node.getName());
		this.setOperationTime(new Date().getTime());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public Long getOperationTime() {
		return operationTime;
	}
	
	public Date getOperationDate() {
		return new Date(operationTime);
	}

	public void setOperationTime(Long operationTime) {
		this.operationTime = operationTime;
	}

	public NodeOperation getOperationType() {
		return operationType;
	}

	public void setOperationType(NodeOperation operationType) {
		this.operationType = operationType;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public List<DataSetAuditLog> getDatasetLog() {
		return datasetLog;
	}

	public void setDatasetLog(List<DataSetAuditLog> datasetLog) {
		this.datasetLog = datasetLog;
	}
}
