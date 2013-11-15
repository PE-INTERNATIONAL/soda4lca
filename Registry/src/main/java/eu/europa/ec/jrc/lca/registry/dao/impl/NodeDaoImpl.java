package eu.europa.ec.jrc.lca.registry.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import eu.europa.ec.jrc.lca.registry.dao.NodeDao;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.DataSet_;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;
import eu.europa.ec.jrc.lca.registry.domain.Node_;

@Repository(value = "nodeDao")
public class NodeDaoImpl extends GenericDAOImpl<Node, Long> implements NodeDao {

	@Override
	public Long getCountByNodeIdAndUrl(String nodeId, String url) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<Node> node = query.from(Node.class);
		Path<String> nodeIdP = node.get(Node_.nodeId);
		Path<String> urlP = node.get(Node_.baseUrl);

		query.select(builder.count(node));
		query.where(builder.or(builder.equal(nodeIdP, nodeId),
				builder.equal(urlP, url)));

		return getEntityManager().createQuery(query).getSingleResult();
	}

	@Override
	public Node getByNodeId(String nodeId) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Node> query = builder.createQuery(Node.class);

		Root<Node> node = query.from(Node.class);
		Path<String> nodeIdP = node.get(Node_.nodeId);

		query.where(builder.equal(nodeIdP, nodeId));

		return getEntityManager().createQuery(query).getSingleResult();

	}

	@Override
	public List<Node> getListOfNotApprovedNodes() {
		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Node> query = builder.createQuery(Node.class);

		Root<Node> node = query.from(Node.class);
		Path<NodeStatus> status = node.get(Node_.status);
		query.where(builder.equal(status, NodeStatus.NOT_APPROVED));

		TypedQuery<Node> q = getEntityManager().createQuery(query);
		return q.getResultList();
	}

	public void updateActivityStatus(Date lastCheckDate) {
		getEntityManager().createNamedQuery("updateActivityStatus")
				.setParameter("lastCheckDate", lastCheckDate).executeUpdate();
	}

	@Override
	public List<Node> getListOfApprovedNodes() {
		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Node> query = builder.createQuery(Node.class);

		Root<Node> node = query.from(Node.class);
		Path<NodeStatus> status = node.get(Node_.status);
		query.where(builder.notEqual(status, NodeStatus.NOT_APPROVED));

		TypedQuery<Node> q = getEntityManager().createQuery(query);
		return q.getResultList();
	}
	
	@Override
	public List<Node> getListOfNodesForDatasetsWithStatus(DataSetStatus[] status){
		CriteriaBuilder builder = getCriteriaBuilder();
		
		CriteriaQuery<Node> query = builder.createQuery(Node.class);
		Root<Node> nodeR = query.from(Node.class);
		query.select(nodeR);
		
		Subquery<DataSet> subQuery = query.subquery(DataSet.class);
		Root<DataSet> datSet = subQuery.from(DataSet.class);
		subQuery.select(datSet);
		
		Predicate statusPredicate = datSet.get(DataSet_.status).in(status);
		Predicate correlatePredicate = builder.equal(datSet.get(DataSet_.node), nodeR);
		
		subQuery.where(builder.and(statusPredicate, correlatePredicate));

		query.where(builder.exists(subQuery));
		
		TypedQuery<Node> q = getEntityManager().createQuery(query);
		return q.getResultList();
	}

}
