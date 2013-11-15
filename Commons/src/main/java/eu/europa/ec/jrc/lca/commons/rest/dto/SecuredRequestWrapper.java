package eu.europa.ec.jrc.lca.commons.rest.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.europa.ec.jrc.lca.commons.security.Credentials;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SecuredRequestWrapper {
	private Credentials credentials;

	private Object entity;

	public SecuredRequestWrapper() {
	}

	public SecuredRequestWrapper(Credentials credentials) {
		this(credentials, null);
	}

	public SecuredRequestWrapper(Credentials credentials, Object entity) {
		this.credentials = credentials;
		this.entity = entity;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}
}
