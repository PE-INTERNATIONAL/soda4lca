package eu.europa.ec.jrc.lca.registry.dao;

import eu.europa.ec.jrc.lca.registry.domain.User;

public interface UserDao extends GenericDAO<User, Long>{
	User findByLogin(String login);
}
