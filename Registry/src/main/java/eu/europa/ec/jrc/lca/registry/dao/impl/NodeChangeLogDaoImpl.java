package eu.europa.ec.jrc.lca.registry.dao.impl;

import org.springframework.stereotype.Repository;

import eu.europa.ec.jrc.lca.registry.dao.NodeChangeLogDao;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;

@Repository(value = "nodeChangeLogDao")
public class NodeChangeLogDaoImpl extends GenericDAOImpl<NodeChangeLog, Long> implements NodeChangeLogDao {


}
