package eu.europa.ec.jrc.lca.registry.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import eu.europa.ec.jrc.lca.registry.dao.UserDao;
import eu.europa.ec.jrc.lca.registry.domain.User;
import eu.europa.ec.jrc.lca.registry.domain.User_;

@Repository(value = "userDao")
public class UserDaoImpl extends GenericDAOImpl<User, Long> implements UserDao {

	@Override
	public User findByLogin(String login) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<User> c = cb.createQuery(User.class);
		Root<User> user = c.from(User.class);
		Path<String> loginP = user.get(User_.login);
		c.where(cb.and(cb.equal(loginP, login)));
		
		TypedQuery<User> q = getEntityManager().createQuery(c); 
		return q.getSingleResult();
	}

}
