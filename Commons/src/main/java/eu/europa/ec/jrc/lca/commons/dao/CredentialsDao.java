package eu.europa.ec.jrc.lca.commons.dao;

import eu.europa.ec.jrc.lca.commons.security.Credentials;


public interface CredentialsDao {
	Credentials<?> findUniqueId(String uniqueId);
}
