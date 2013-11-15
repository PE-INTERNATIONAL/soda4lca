package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.registry.dao.AuditLogDao;
import eu.europa.ec.jrc.lca.registry.domain.NodeAuditLog;
import eu.europa.ec.jrc.lca.registry.service.NodeHistoryService;

@Transactional
@Service("nodeHistoryService")
public class NodeHistoryServiceImpl implements NodeHistoryService{

	@Autowired
	private AuditLogDao auditLogDao;
	
	@Override
	public List<NodeAuditLog> loadLazy(SearchParameters sp) {
		return auditLogDao.find(sp);
	}

	@Override
	public Long count(SearchParameters sp) {
		return auditLogDao.count(sp);
	}

	@Override
	public NodeAuditLog getWithDetails(Long id) {
		NodeAuditLog nal = auditLogDao.findById(id);
		nal.getDatasetLog().size();
		return nal;
	}

}
