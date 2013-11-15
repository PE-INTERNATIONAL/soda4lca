package eu.europa.ec.jrc.lca.commons.domain;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.europa.ec.jrc.lca.commons.security.Credentials;

@Entity
@Table(name="t_registry_credentials")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryCredentials extends Credentials<RegistryCredentials>{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String accessAccount;
	
	@Column(name="ACCESSPASSWORD")
	private byte[] encryptedPassword;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccessAccount() {
		return accessAccount;
	}

	public void setAccessAccount(String accessAccount) {
		this.accessAccount = accessAccount;
	}

	@Override
	public byte[] getEncryptedPassword() {
		return encryptedPassword;
	}
	
	@Override
	public void setEncryptedPassword(byte[] encrypted) {
		this.encryptedPassword =  Arrays.copyOf(encrypted, encrypted.length);
		
	}
	
	@Override
	public RegistryCredentials getCopy() {
		RegistryCredentials copy = new RegistryCredentials();
		copy.setAccessAccount(this.accessAccount);
		copy.setAccessPassword(getAccessPassword());
		return copy;
	}

	@Override
	public String getUniqueId() {
		return accessAccount;
	}
}
