package eu.europa.ec.jrc.lca.registry.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import eu.europa.ec.jrc.lca.commons.dao.CredentialsDao;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials_;

@Repository(value = "nodeCredentialsDao")
public class NodeCredentialsDaoImpl extends
		GenericDAOImpl<NodeCredentials, Long> implements
		CredentialsDao {
	
	@Override
	public NodeCredentials findUniqueId(String identity) {
		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<NodeCredentials> query = builder.createQuery(NodeCredentials.class);

		Root<NodeCredentials> nodeCredentials = query.from(NodeCredentials.class);
		Path<String> nodeId = nodeCredentials
				.get(NodeCredentials_.nodeId);

		query.where(builder.equal(nodeId, identity));

		TypedQuery<NodeCredentials> q = getEntityManager().createQuery(query);
		return q.getSingleResult();
	}

}
