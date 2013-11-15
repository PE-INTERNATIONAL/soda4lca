package de.iai.ilcd.model.registry;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import de.iai.ilcd.model.nodes.NetworkNode;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;

@Entity
@Table( name = "registry" )
public class Registry implements Serializable {

	private static final long serialVersionUID = -986951355543686262L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	private String baseUrl;

	private String name;

	private String description;

	@Enumerated( EnumType.STRING )
	private RegistryStatus status;

	@OneToOne( cascade = { CascadeType.ALL } )
	@JoinColumn( name = "REG_DATA_ID" )
	private RegistrationData registrationData;

	@OneToOne( cascade = { CascadeType.ALL } )
	@JoinColumn( name = "NODE_CREDENTIALS_ID" )
	private NodeCredentials nodeCredentials;

	@OneToOne( cascade = { CascadeType.ALL } )
	@JoinColumn( name = "REGISTRY_CREDENTIALS_ID" )
	private RegistryCredentials registryCredentials;

	@OneToMany( cascade = { CascadeType.ALL }, mappedBy = "registry" )
	private Set<NetworkNode> nodes = new HashSet<NetworkNode>();

	private String uuid;

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl( String baseUrl ) {
		this.baseUrl = baseUrl;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public RegistryStatus getStatus() {
		return status;
	}

	public void setStatus( RegistryStatus status ) {
		this.status = status;
	}

	public Set<NetworkNode> getNodes() {
		return nodes;
	}

	public void setNodes( Set<NetworkNode> nodes ) {
		this.nodes = nodes;
	}

	public RegistrationData getRegistrationData() {
		return registrationData;
	}

	public void setRegistrationData( RegistrationData registrationData ) {
		this.registrationData = registrationData;
	}

	public RegistryCredentials getRegistryCredentials() {
		return registryCredentials;
	}

	public void setRegistryCredentials( RegistryCredentials registryCredentials ) {
		this.registryCredentials = registryCredentials;
	}

	public NodeCredentials getNodeCredentials() {
		return nodeCredentials;
	}

	public void setNodeCredentials( NodeCredentials nodeCredentials ) {
		this.nodeCredentials = nodeCredentials;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid( String uuid ) {
		this.uuid = uuid;
	}
}
