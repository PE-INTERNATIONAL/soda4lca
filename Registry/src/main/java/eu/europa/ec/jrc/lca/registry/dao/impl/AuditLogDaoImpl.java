package eu.europa.ec.jrc.lca.registry.dao.impl;

import org.springframework.stereotype.Repository;

import eu.europa.ec.jrc.lca.registry.dao.AuditLogDao;
import eu.europa.ec.jrc.lca.registry.domain.NodeAuditLog;

@Repository(value = "auditLogDao")
public class AuditLogDaoImpl extends GenericDAOImpl<NodeAuditLog, Long> implements
		AuditLogDao {

}
