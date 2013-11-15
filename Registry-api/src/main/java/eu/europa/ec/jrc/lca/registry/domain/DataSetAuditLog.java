package eu.europa.ec.jrc.lca.registry.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="T_DATA_SET_AUDIT_LOG")
public class DataSetAuditLog implements Serializable{
	private static final long serialVersionUID = 2756741199488763095L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long dataSetId;
	
	private String uuid;
	
	private String version;
	
	public DataSetAuditLog(){}
	
	public DataSetAuditLog(DataSet ds){
		this.dataSetId = ds.getId();
		this.uuid = ds.getUuid();
		this.version = ds.getVersion();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(Long dataSetId) {
		this.dataSetId = dataSetId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
