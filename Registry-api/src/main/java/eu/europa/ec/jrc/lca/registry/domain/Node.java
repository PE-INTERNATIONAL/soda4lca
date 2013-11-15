package eu.europa.ec.jrc.lca.registry.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;

@Entity
@Table(name = "T_NODE")
@XmlRootElement(name="node")
@XmlAccessorType(XmlAccessType.FIELD)
public class Node implements Serializable{

	private static final long serialVersionUID = -4905962456619414599L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description;
	private String nodeId;
	private String baseUrl;
	private String adminName;
	private String adminEmailAddress;
	private String adminPhone;
	private String adminWebAddress;
	
	@OneToOne(cascade = { CascadeType.ALL }, fetch=FetchType.LAZY)
	@JoinColumn(name = "NODE_CREDENTIALS_ID")
	private NodeCredentials nodeCredentials;
	
	@OneToOne(cascade = { CascadeType.ALL }, fetch=FetchType.LAZY)
	@JoinColumn(name = "REGISTRY_CREDENTIALS_ID")
	private RegistryCredentials registryCredentials;
	
	@Enumerated(EnumType.STRING)
	private NodeStatus status;
	
	public Node() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminEmailAddress() {
		return adminEmailAddress;
	}

	public void setAdminEmailAddress(String adminEmailAddress) {
		this.adminEmailAddress = adminEmailAddress;
	}

	public String getAdminPhone() {
		return adminPhone;
	}

	public void setAdminPhone(String adminPhone) {
		this.adminPhone = adminPhone;
	}

	public String getAdminWebAddress() {
		return adminWebAddress;
	}

	public void setAdminWebAddress(String adminWebAddress) {
		this.adminWebAddress = adminWebAddress;
	}

	public NodeStatus getStatus() {
		return status;
	}

	public void setStatus(NodeStatus status) {
		this.status = status;
	}

	public NodeCredentials getNodeCredentials() {
		return nodeCredentials;
	}

	public void setNodeCredentials(NodeCredentials nodeCredentials) {
		this.nodeCredentials = nodeCredentials;
	}
	
	public RegistryCredentials getRegistryCredentials() {
		return registryCredentials;
	}

	public void setRegistryCredentials(RegistryCredentials registryCredentials) {
		this.registryCredentials = registryCredentials;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
