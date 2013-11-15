package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.service.exceptions.DatasetIllegalStatusException;
import eu.europa.ec.jrc.lca.registry.dao.DataSetDao;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.NodeOperation;
import eu.europa.ec.jrc.lca.registry.service.AuditLogger;
import eu.europa.ec.jrc.lca.registry.service.DataSetAcceptanceService;
import eu.europa.ec.jrc.lca.registry.service.DataSetDeregistrationService;
import eu.europa.ec.jrc.lca.registry.service.NotificationService;
import eu.europa.ec.jrc.lca.registry.util.ILCDMessageSource;


@Transactional
@Service("dataSetAcceptanceService")
public class DataSetAcceptanceServiceImpl implements DataSetAcceptanceService{
	
	private static final String DATA_SET_UPDATED = "datasetDeregistration.updated";
	
	@Autowired
	private DataSetDao dataSetDao;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private DataSetDeregistrationService dataSetDeregistrationService;
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Autowired
	private ILCDMessageSource messageSource;
	
	@Override
	public void acceptDataSets(List<DataSet> datasets) {
		for(DataSet ds: datasets){
			ds.setStatus(DataSetStatus.ACCEPTED);
			dataSetDao.saveOrUpdate(ds);
		}
		auditLogger.log(datasets, NodeOperation.DATASET_REGISTRATION_ACCEPTANCE);
		notificationService.notifyNodeUserAboutDataSetsAcceptance(datasets);
	}
	
	@Override
	public void acceptUpdateDataSet(DataSet toAccept) throws DatasetIllegalStatusException {
		List<DataSet> datasets = dataSetDao.findByUUID(toAccept.getUuid());
		
		List<DataSet> dsToReject = new ArrayList<DataSet>();
		DataSet toDeregister = null;
		
		for(DataSet ds: datasets){
			if(ds.getStatus()!=DataSetStatus.ACCEPTED && !ds.getId().equals(toAccept.getId())){
				dsToReject.add(ds);
			}
			if(ds.getStatus()==DataSetStatus.ACCEPTED){
				toDeregister = ds;
			}
		}
		
		rejectDataSets(dsToReject, messageSource.getTranslation(DATA_SET_UPDATED));
		if(toDeregister!=null){
			dataSetDeregistrationService.deregisterDataSets(Collections.singletonList(toDeregister));
			dataSetDeregistrationService.broadcastDataSetsDeregistration(Collections.singletonList(toDeregister), messageSource.getTranslation(DATA_SET_UPDATED));
		}
		acceptDataSets(Collections.singletonList(toAccept));
	}
	
	
	@Override
	public void rejectDataSets(List<DataSet> datasets, String reason) {
		for(DataSet ds: datasets){
			dataSetDao.remove(ds.getId());
		}
		auditLogger.log(datasets, NodeOperation.DATASET_REGISTRATION_REJECTION);
		notificationService.notifyNodeUserAboutDataSetsRejection(datasets, reason);
	}
}
