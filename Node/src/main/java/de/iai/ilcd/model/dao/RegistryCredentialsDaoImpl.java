package de.iai.ilcd.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import de.iai.ilcd.persistence.PersistenceUtil;
import eu.europa.ec.jrc.lca.commons.dao.CredentialsDao;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials_;

@Repository( value = "registryCredentialsDao" )
public class RegistryCredentialsDaoImpl extends GenericDAOImpl<RegistryCredentials, Long> implements CredentialsDao, RegistryCredentialsDao {

	@Override
	public RegistryCredentials findUniqueId( String identity ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RegistryCredentials> query = builder.createQuery( RegistryCredentials.class );

			Root<RegistryCredentials> registryCredentials = query.from( RegistryCredentials.class );
			Path<String> regCredentialsAccount = registryCredentials.get( RegistryCredentials_.accessAccount );

			query.where( builder.equal( regCredentialsAccount, identity ) );

			TypedQuery<RegistryCredentials> q = em.createQuery( query );
			return q.getSingleResult();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

}
