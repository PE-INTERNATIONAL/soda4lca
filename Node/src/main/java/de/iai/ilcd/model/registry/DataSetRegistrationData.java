package de.iai.ilcd.model.registry;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.iai.ilcd.model.common.DataSetVersion;

@Entity
@Table( name = "dataset_registration_data" )
public class DataSetRegistrationData implements Serializable {

	private static final long serialVersionUID = -1377761863979565265L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@ManyToOne
	@JoinColumn( name = "REGISTRY_ID" )
	private Registry registry;

	@Enumerated( EnumType.STRING )
	private DataSetRegistrationDataStatus status;

	@ManyToOne
	@JoinColumn( name = "REASON_ID" )
	private DatasetDeregistrationReason reason;

	private String uuid;

	@Embedded
	private DataSetVersion version;

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry( Registry registry ) {
		this.registry = registry;
	}

	public DataSetRegistrationDataStatus getStatus() {
		return status;
	}

	public void setStatus( DataSetRegistrationDataStatus status ) {
		this.status = status;
	}

	public DatasetDeregistrationReason getReason() {
		return reason;
	}

	public void setReason( DatasetDeregistrationReason reason ) {
		this.reason = reason;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid( String uuid ) {
		this.uuid = uuid;
	}

	public DataSetVersion getVersion() {
		return version;
	}

	public void setVersion( DataSetVersion version ) {
		this.version = version;
	}

}
