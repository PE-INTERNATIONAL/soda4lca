package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDeregistrationResult;
import eu.europa.ec.jrc.lca.commons.service.exceptions.DatasetIllegalStatusException;
import eu.europa.ec.jrc.lca.registry.dao.DataSetDao;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.NodeOperation;
import eu.europa.ec.jrc.lca.registry.service.AuditLogger;
import eu.europa.ec.jrc.lca.registry.service.BroadcastingService;
import eu.europa.ec.jrc.lca.registry.service.DataSetDeregistrationService;
import eu.europa.ec.jrc.lca.registry.service.NotificationService;

@Transactional
@Service("dataSetDeregistrationService")
public class DataSetDeregistrationServiceImpl implements DataSetDeregistrationService {

	@Autowired
	private DataSetDao dataSetDao;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private BroadcastingService broadcastingService;
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void deregisterDataSets(List<DataSet> datasets) throws DatasetIllegalStatusException {
		for(DataSet ds: datasets){
			deregisterDataSet(ds);
		}
		auditLogger.log(datasets, NodeOperation.DATASET_DEREGISTRATION_BY_REGISTRY);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void broadcastDataSetsDeregistration(List<DataSet> datasets, String reason){
		for(DataSet ds: datasets){
			broadcastingService.broadcastDatasetDeregistrationInformation(ds);
		}
		notificationService.notifyNodeUserAboutDataSetsDeregistration(datasets, reason);
	}

	
	@Override
	public List<DataSetDeregistrationResult> deregisterDataSets(List<DataSetDTO> datasets, String reason,
			String nodeId) {
		List<DataSetDeregistrationResult> result = new ArrayList<DataSetDeregistrationResult>();
		List<DataSet> toDeregister = new ArrayList<DataSet>();
		
		for(DataSetDTO dsdto: datasets){
			try{
				DataSet ds = dataSetDao.findByUUIDAndVersionAndNode(dsdto.getUuid(), dsdto.getVersion(), nodeId);
				try{
					deregisterDataSet(ds);
					toDeregister.add(ds);
					result.add(DataSetDeregistrationResult.DEREGISTERED);
				}
				catch(DatasetIllegalStatusException e){
					result.add(DataSetDeregistrationResult.REJECTED_WRONG_STATUS);
				}
			}catch(NoResultException e){
				result.add(DataSetDeregistrationResult.REJECTED_NOT_FOUND);
			}
		}
		
		notificationService.notifyNodeUserAboutDataSetsDeregistration(toDeregister, reason);
		auditLogger.log(toDeregister, NodeOperation.DATASET_DEREGISTRATION_BY_NODE);
		return result;
	}
	
	private void deregisterDataSet(DataSet dataset) throws DatasetIllegalStatusException {
		dataset = dataSetDao.findById(dataset.getId());
		if(dataset.getStatus()!=DataSetStatus.ACCEPTED){
			throw new DatasetIllegalStatusException("Dataset must have status ACCEPTED");
		}
		dataSetDao.remove(dataset);
	}


	@Override
	public void setBroadcastingService(BroadcastingService broadcastingService) {
		this.broadcastingService = broadcastingService;
	}
}
