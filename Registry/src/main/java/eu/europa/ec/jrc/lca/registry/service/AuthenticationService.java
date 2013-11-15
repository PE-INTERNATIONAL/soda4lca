package eu.europa.ec.jrc.lca.registry.service;

import eu.europa.ec.jrc.lca.registry.domain.User;

public interface AuthenticationService {
	
	/**
	 * Authentication method
	 * @param login
	 * @param password
	 * @return object representing user
	 */
	User authenticate(String login, String password);
}
