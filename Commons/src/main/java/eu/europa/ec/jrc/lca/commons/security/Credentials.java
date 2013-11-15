package eu.europa.ec.jrc.lca.commons.security;

import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.UUID;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.security.encryption.CipherUtil;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({RegistryCredentials.class, NodeCredentials.class})
public abstract class Credentials<T extends Credentials<T>> {
	@Transient
	@XmlTransient
	private String accessPassword;

	@Transient
	private String nonce;
	
	public abstract byte[] getEncryptedPassword();
	
	public abstract void setEncryptedPassword(byte[] encrypted);
	
	public abstract String getAccessAccount();
	
	public abstract void setAccessAccount(String accessAccount);
	
	public abstract String getUniqueId();
	
	protected abstract T getCopy();
	
	public String getAccessPassword() {
		return accessPassword;
	}

	public void setAccessPassword(String accessPassword) {
		this.accessPassword = accessPassword;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	
	
	public void encrypt(RSAPublicKeySpec key) {
		setEncryptedPassword(CipherUtil.encrypt(key, getAccessPassword().getBytes()));
	}
	
	public void encryptWithNonce(RSAPublicKeySpec key) {
		this.nonce = UUID.randomUUID().toString();
		setEncryptedPassword(CipherUtil.encrypt(key, (getAccessPassword() + getNonce()).getBytes()));
	}

	public void decrypt(RSAPrivateKeySpec key) {
		accessPassword = new String(CipherUtil.decrypt(key, getEncryptedPassword()));
	}

	public T getEncryptedCopy(RSAPublicKeySpec key) {
		T copy = getCopy();
		copy.encrypt(key);
		return copy;
	}

	public T getEncryptedCopyWithNonce(RSAPublicKeySpec key) {
		T copy = getCopy();
		copy.encryptWithNonce(key);
		return copy;
	}
}
