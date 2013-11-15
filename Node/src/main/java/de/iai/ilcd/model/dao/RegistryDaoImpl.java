package de.iai.ilcd.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.registry.RegistryStatus;
import de.iai.ilcd.model.registry.Registry_;
import de.iai.ilcd.persistence.PersistenceUtil;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials_;

@Repository( value = "registryDao" )
public class RegistryDaoImpl extends GenericDAOImpl<Registry, Long> implements RegistryDao {

	@Override
	public List<Registry> getRegistriesOrderedByName() {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Registry> query = builder.createQuery( Registry.class );

			Root<Registry> registry = query.from( Registry.class );
			Path<String> regName = registry.get( Registry_.name );

			query.orderBy( builder.asc( regName ) );

			TypedQuery<Registry> q = em.createQuery( query );
			return q.getResultList();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	@Override
	public Registry findByIdentity( String identity ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Registry> query = builder.createQuery( Registry.class );

			Root<Registry> registry = query.from( Registry.class );
			Path<RegistryCredentials> regCredentials = registry.get( Registry_.registryCredentials );
			Path<String> regCredentialsAccount = regCredentials.get( RegistryCredentials_.accessAccount );

			query.where( builder.equal( regCredentialsAccount, identity ) );

			TypedQuery<Registry> q = em.createQuery( query );
			return q.getSingleResult();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	@Override
	public List<Registry> getNonVirtualRegistriesInWhichRegistered() {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Registry> query = builder.createQuery( Registry.class );

			Root<Registry> registry = query.from( Registry.class );
			Path<RegistryStatus> regStatus = registry.get( Registry_.status );

			query.where( builder.equal( regStatus, RegistryStatus.REGISTERED ) );

			TypedQuery<Registry> q = em.createQuery( query );
			return q.getResultList();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	@Override
	public List<Registry> getRegistriesInWhichRegistered() {
		return findByStatus( RegistryStatus.REGISTERED );
	}

	@Override
	public List<Registry> getRegistriesToCheckAcceptance() {
		return findByStatus( RegistryStatus.PENDING_REGISTRATION );
	}

	private List<Registry> findByStatus( RegistryStatus status ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Registry> query = builder.createQuery( Registry.class );

			Root<Registry> registry = query.from( Registry.class );
			Path<RegistryStatus> regStatus = registry.get( Registry_.status );

			query.where( builder.equal( regStatus, status ) );

			TypedQuery<Registry> q = em.createQuery( query );
			return q.getResultList();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	@Override
	public Registry findByUUID( String uuid ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Registry> query = builder.createQuery( Registry.class );

			Root<Registry> registry = query.from( Registry.class );
			Path<String> regUUID = registry.get( Registry_.uuid );

			query.where( builder.equal( regUUID, uuid ) );

			TypedQuery<Registry> q = em.createQuery( query );
			return q.getSingleResult();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	@Override
	public Registry findByUrl( String url ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Registry> query = builder.createQuery( Registry.class );

			Root<Registry> registry = query.from( Registry.class );
			Path<String> regUrl = registry.get( Registry_.baseUrl );

			query.where( builder.equal( regUrl, url ) );

			TypedQuery<Registry> q = em.createQuery( query );
			return q.getSingleResult();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}
}
