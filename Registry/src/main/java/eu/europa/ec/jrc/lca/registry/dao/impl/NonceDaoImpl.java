package eu.europa.ec.jrc.lca.registry.dao.impl;

import java.util.Date;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.dao.NonceDao;
import eu.europa.ec.jrc.lca.commons.domain.Nonce;
import eu.europa.ec.jrc.lca.commons.domain.Nonce_;

@Transactional
@Repository(value="nonceDao")
public class NonceDaoImpl extends GenericDAOImpl<Nonce, Long> implements NonceDao {

	@Override
	public boolean exists(String nonceValue) {
		CriteriaBuilder cb = getCriteriaBuilder();
		
		CriteriaQuery<Nonce> c = cb.createQuery(Nonce.class);
		Root<Nonce> nonceR = c.from(Nonce.class);
		Path<byte[]> value = nonceR.get(Nonce_.value);
		c.where(cb.and(cb.equal(value, nonceValue.getBytes())));

		TypedQuery<Nonce> q = getEntityManager().createQuery(c); 
		return q.getResultList().size()!=0;
	}

	@Override
	public void save(String nonceValue) {
		Nonce n = new Nonce();
		n.setUseDate(new Date());
		n.setValue(nonceValue.getBytes());
		saveOrUpdate(n);
	}

	@Override
	public void clearNonces(Date date){
		getEntityManager().createNamedQuery("removeOldNonces")
		.setParameter("clearDate", date).executeUpdate();
	}
}
