package de.iai.ilcd.model.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import de.iai.ilcd.persistence.PersistenceUtil;
import eu.europa.ec.jrc.lca.commons.dao.NonceDao;
import eu.europa.ec.jrc.lca.commons.domain.Nonce;
import eu.europa.ec.jrc.lca.commons.domain.Nonce_;

@Repository( value = "nonceDao" )
public class NonceDaoImpl extends GenericDAOImpl<Nonce, Long> implements NonceDao {

	@Override
	public boolean exists( String nonceValue ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();

			CriteriaQuery<Nonce> c = cb.createQuery( Nonce.class );
			Root<Nonce> nonceR = c.from( Nonce.class );
			Path<byte[]> value = nonceR.get( Nonce_.value );
			c.where( cb.and( cb.equal( value, nonceValue.getBytes() ) ) );

			TypedQuery<Nonce> q = em.createQuery( c );
			return q.getResultList().size() != 0;
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	@Override
	public void save( String nonceValue ) {
		Nonce n = new Nonce();
		n.setUseDate( new Date() );
		n.setValue( nonceValue.getBytes() );
		saveOrUpdate( n );
	}

	@Override
	public void clearNonces( Date date ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			em.createNamedQuery( "removeOldNonces" ).setParameter( "clearDate", date ).executeUpdate();
			em.getTransaction().commit();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}
}
