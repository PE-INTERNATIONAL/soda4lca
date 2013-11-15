package eu.europa.ec.jrc.lca.registry.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import eu.europa.ec.jrc.lca.registry.dao.DataSetDao;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.DataSet_;
import eu.europa.ec.jrc.lca.registry.domain.Node_;

@Repository("dataSetDao")
public class DataSetDaoImpl extends GenericDAOImpl<DataSet, Long> implements
		DataSetDao {

	@Override
	public DataSet findAcceptedByUUID(String uuid) {
		List<DataSet> result = findBy(uuid, Boolean.TRUE, null, null);
		if (result.size() == 0) {
			throw new NoResultException();
		}
		if (result.size() > 1) {
			throw new NonUniqueResultException();
		}
		return result.get(0);
	}

	@Override
	public DataSet findAcceptedByUUIDAndNode(String uuid, String nodeId) {
		List<DataSet> result = findBy(uuid, Boolean.TRUE, nodeId, null);
		if (result.size() == 0) {
			throw new NoResultException();
		}
		if (result.size() > 1) {
			throw new NonUniqueResultException();
		}
		return result.get(0);
	}

	@Override
	public List<DataSet> findByUUIDAndNode(String uuid, String nodeId) {
		return findBy(uuid, null, nodeId, null);
	}

	@Override
	public DataSet findByUUIDAndVersionAndNode(String uuid, String version,
			String nodeId) {
		List<DataSet> result = findBy(uuid, null, nodeId, version);
		if (result.size() == 0) {
			throw new NoResultException();
		}
		if (result.size() > 1) {
			throw new NonUniqueResultException();
		}
		return result.get(0);
	}

	@Override
	public List<DataSet> findByUUID(String uuid) {
		return findBy(uuid, null, null, null);
	}


	@Override
	public List<DataSet> findByNode(String nodeId) {
		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<DataSet> query = builder.createQuery(DataSet.class);
		Root<DataSet> dataSet = query.from(DataSet.class);
		Path<String> nodeIdPath = dataSet.get(DataSet_.node).get(
				Node_.nodeId);
		query.where(builder.equal(nodeIdPath, nodeId));
		TypedQuery<DataSet> q = getEntityManager().createQuery(query);
		return q.getResultList();
	}
	
	@Override
	public List<DataSet> findAcceptedByNode(String nodeId){
		return findBy(null, true, nodeId, null);
	}
	
	private List<DataSet> findBy(String uuid, Boolean accepted, String nodeId,
			String version) {
		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<DataSet> query = builder.createQuery(DataSet.class);

		Root<DataSet> dataSet = query.from(DataSet.class);
		Path<DataSetStatus> statusPath = dataSet.get(DataSet_.status);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if(uuid != null){
			Path<String> uuidPath = dataSet.get(DataSet_.uuid);
			predicates.add(builder.equal(uuidPath, uuid));
		}
		if (accepted != null) {
			if (accepted) {
				predicates.add(builder
						.equal(statusPath, DataSetStatus.ACCEPTED));
			} else {
				predicates.add(statusPath.in(DataSetStatus.NEW,
						DataSetStatus.UPDATE, DataSetStatus.NEW_ALTERNATIVE));
			}
		}
		if (nodeId != null) {
			Path<String> nodeIdPath = dataSet.get(DataSet_.node).get(
					Node_.nodeId);
			predicates.add(builder.equal(nodeIdPath, nodeId));
		}
		if(version != null){
			Path<String> versionPath = dataSet.get(DataSet_.version);
			predicates.add(builder.equal(versionPath, version));
		}

		query.where(builder.and(predicates.toArray(new Predicate[] {})));
		TypedQuery<DataSet> q = getEntityManager().createQuery(query);
		return q.getResultList();
	}

	@Override
	public int updateAssociatedDatasets(DataSet ds) {
		return getEntityManager().createNamedQuery("updateAssociatedDatasets")
				.setParameter("uuid", ds.getUuid()).executeUpdate();
	}

	@Override
	public int removeByNode(String nodeId) {
		return getEntityManager().createNamedQuery("removeByNode")
				.setParameter("nodeId", nodeId).executeUpdate();
	}
	
	
}
