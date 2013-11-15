package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationResult;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.registry.dao.DataSetDao;
import eu.europa.ec.jrc.lca.registry.dao.NodeDao;
import eu.europa.ec.jrc.lca.registry.domain.Compliance;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeOperation;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;
import eu.europa.ec.jrc.lca.registry.service.AuditLogger;
import eu.europa.ec.jrc.lca.registry.service.DataSetRegistrationService;
import eu.europa.ec.jrc.lca.registry.service.DataSetService;
import eu.europa.ec.jrc.lca.registry.service.NotificationService;
import eu.europa.ec.jrc.lca.registry.service.exceptions.DataNotChangedException;
import eu.europa.ec.jrc.lca.registry.service.exceptions.MissingSupportedComplianceException;

@Transactional
@Service("dataSetRegistrationService")
public class DataSetRegistrationServiceImpl implements DataSetRegistrationService {

	@Autowired
	private DataSetDao dataSetDao;
	
	@Autowired
	private NodeDao nodeDao;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private DataSetService dataSetService;
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Override
	public List<DataSetRegistrationResult> registerDataSets(List<DataSet> datasets, String nodeId)
			throws NodeIllegalStatusException {
		Node node = nodeDao.getByNodeId(nodeId);
		auditLogger.log(datasets, node, NodeOperation.DATASET_REGISTRATION_REQUEST);
		if(node.getStatus()==NodeStatus.NOT_APPROVED){
			throw new NodeIllegalStatusException("can't register dataset from not approved node");
		}
		
		List<DataSetRegistrationResult> registrationsStatuses = new ArrayList<DataSetRegistrationResult>();
		for(DataSet ds: datasets){
			registrationsStatuses.add(registerDataSet(ds, node));
		}
		
		List<DataSet> validRegistrations = getValidRegstrationRequests(datasets, registrationsStatuses);
		if(!validRegistrations.isEmpty()){
			notificationService.notifyRegistryAdminsAboutDataSetsRegistrationRequest(node,validRegistrations.size());
			auditLogger.log(datasets, node, NodeOperation.DATASET_REGISTRATION_REQUEST_ACCEPTANCE);
		}
		return registrationsStatuses;
	}
	
	private DataSetRegistrationResult registerDataSet(DataSet dataset, Node node){
		try{
			List<DataSet> stored = dataSetDao.findByUUID(dataset.getUuid());
			if(stored.isEmpty()){
				return handleFirstDSRegistration(dataset, node);
			}else{
				return updateDataSet(dataset, stored, node);
			}
		} catch(MissingSupportedComplianceException ex){
			return DataSetRegistrationResult.REJECTED_COMPLIANCE;
		}
		catch(DataNotChangedException ex){
			return DataSetRegistrationResult.REJECTED_NO_DIFFERENCE;
		}
	}
	
	private DataSetRegistrationResult handleFirstDSRegistration(DataSet dataset, Node node) throws MissingSupportedComplianceException{
		validateCompliance(dataset);
		dataset.setNode(node);
		dataset.setStatus(DataSetStatus.NEW);
		dataSetDao.saveOrUpdate(dataset);
		return DataSetRegistrationResult.ACCEPTED_PENDING;
	}
	
	private DataSetRegistrationResult updateDataSet(DataSet dataset, List<DataSet> storedDatasets, Node node) throws MissingSupportedComplianceException, DataNotChangedException{
		validateCompliance(dataset);
		validateData(dataset, storedDatasets);
		int updated = updateAssociatedDataSets(dataset);
		dataset.setNode(node);
		dataset.setStatus(updated == 0 ? DataSetStatus.UPDATE : DataSetStatus.NEW_ALTERNATIVE);
		dataSetDao.saveOrUpdate(dataset);
		return DataSetRegistrationResult.ACCEPTED_PENDING;
	}

	private void validateCompliance(DataSet dataset) throws MissingSupportedComplianceException{
		List<Compliance> supported = dataSetService.getSupportedCompliances();
		if(supported==null || supported.isEmpty()){
			return;
		}
		for(Compliance compliance: dataSetService.getSupportedCompliances()){
			if(dataset.getComplianceUUIDs().contains(compliance.getUuid())){
				return;
			}
		}
		throw new MissingSupportedComplianceException();
	}
	
	private void validateData(DataSet dataset, List<DataSet> storedDatasets) throws DataNotChangedException{
		for(DataSet ds: storedDatasets){
			if(Arrays.equals(ds.getHash(),dataset.getHash())){
				throw new DataNotChangedException();
			}
		}
	}
	
	private int updateAssociatedDataSets(DataSet dataset){
		return dataSetDao.updateAssociatedDatasets(dataset);
	}
	
	private List<DataSet> getValidRegstrationRequests(List<DataSet> datasets, List<DataSetRegistrationResult> statuses){
		for(Iterator iter = datasets.iterator(), statIterator = statuses.iterator(); iter.hasNext(); ){
			iter.next();
			if(!statIterator.next().equals(DataSetRegistrationResult.ACCEPTED_PENDING)){
				iter.remove();
			}
		}
		return datasets;
	}
}
