package de.iai.ilcd.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.common.exception.FormatException;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import de.iai.ilcd.model.registry.DataSetRegistrationDataStatus;
import de.iai.ilcd.model.registry.DataSetRegistrationData_;
import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.registry.Registry_;
import de.iai.ilcd.persistence.PersistenceUtil;

@Repository( value = "dataSetRegistrationDataDao" )
public class DataSetRegistrationDataDaoImpl extends GenericDAOImpl<DataSetRegistrationData, Long> implements DataSetRegistrationDataDao {

	@Override
	public List<DataSetRegistrationData> getRegistered( Long registryId ) {
		return getRegisteredWithStatus( registryId, null );
	}

	@Override
	public List<DataSetRegistrationData> getRegisteredNotAccepted( Long registryId ) {
		return getRegisteredWithStatus( registryId, DataSetRegistrationDataStatus.NOT_ACCEPTED );
	}

	@Override
	public List<DataSetRegistrationData> getRegisteredAccepted( Long registryId ) {
		return getRegisteredWithStatus( registryId, DataSetRegistrationDataStatus.ACCEPTED );
	}

	@Override
	public List<DataSetRegistrationData> getRegisteredPendingDeregistration( Long registryId ) {
		return getRegisteredWithStatus( registryId, DataSetRegistrationDataStatus.PENDING_DEREGISTRATION );
	}

	private List<DataSetRegistrationData> getRegisteredWithStatus( Long registryId, DataSetRegistrationDataStatus status ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DataSetRegistrationData> c = cb.createQuery( DataSetRegistrationData.class );
			Root<DataSetRegistrationData> dsrdR = c.from( DataSetRegistrationData.class );
			Path<Registry> registryP = dsrdR.get( DataSetRegistrationData_.registry );
			Path<DataSetRegistrationDataStatus> statusP = dsrdR.get( DataSetRegistrationData_.status );
			c.where( cb.and( status == null ? new Predicate[] { cb.equal( registryP.get( Registry_.id ), registryId ) } : new Predicate[] {
					cb.equal( registryP.get( Registry_.id ), registryId ), cb.equal( statusP, status ) } ) );
			TypedQuery<DataSetRegistrationData> q = em.createQuery( c );
			return q.getResultList();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	@Override
	public List<DataSetRegistrationData> getListOfRegistrations( Process process ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DataSetRegistrationData> c = cb.createQuery( DataSetRegistrationData.class );
			Root<DataSetRegistrationData> dsrdR = c.from( DataSetRegistrationData.class );
			Path<String> uuidP = dsrdR.get( DataSetRegistrationData_.uuid );
			Path<DataSetVersion> versionP = dsrdR.get( DataSetRegistrationData_.version );
			c.where( cb.and( cb.equal( uuidP, process.getUuidAsString() ), cb.equal( versionP, process.getVersion() ) ) );
			TypedQuery<DataSetRegistrationData> q = em.createQuery( c );
			return q.getResultList();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	@Override
	public DataSetRegistrationData findByUUIDAndVersionAndRegistry( String UUID, String version, Long registryId ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DataSetRegistrationData> c = cb.createQuery( DataSetRegistrationData.class );
			Root<DataSetRegistrationData> dsrdR = c.from( DataSetRegistrationData.class );
			Path<Registry> registryP = dsrdR.get( DataSetRegistrationData_.registry );
			Path<String> uuidP = dsrdR.get( DataSetRegistrationData_.uuid );
			Path<DataSetVersion> versionP = dsrdR.get( DataSetRegistrationData_.version );
			try {
				c.where( cb.and( cb.equal( registryP.get( Registry_.id ), registryId ), cb.equal( uuidP, UUID ), cb.equal( versionP, DataSetVersion
						.parse( version ) ) ) );
			}
			catch ( FormatException e ) {
				throw new NoResultException();
			}
			TypedQuery<DataSetRegistrationData> q = em.createQuery( c );
			return q.getSingleResult();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

}
