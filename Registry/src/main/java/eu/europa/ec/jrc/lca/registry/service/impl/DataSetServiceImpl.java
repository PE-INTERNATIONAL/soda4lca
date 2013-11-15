package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationAcceptanceDecision;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.registry.dao.ComplianceDao;
import eu.europa.ec.jrc.lca.registry.dao.DataSetDao;
import eu.europa.ec.jrc.lca.registry.domain.Compliance;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.service.DataSetService;

@Transactional
@Service("dataSetService")
public class DataSetServiceImpl implements DataSetService {
	
	@Autowired
	private DataSetDao dataSetDao;
	
	@Autowired
	private ComplianceDao complianceDao;
	
	private static List<Compliance> supportedCompliances;
	
	@Override
	public boolean verifyDataSet(DataSet dataset) throws ResourceNotFoundException{
		try{
			DataSet storedDS = dataSetDao.findAcceptedByUUID(dataset.getUuid());
			return Arrays.equals(storedDS.getHash(), dataset.getHash());
		}catch(NoResultException ex){
			throw new ResourceNotFoundException();
		}
	}

	@Override
	public List<DataSet> loadLazy(SearchParameters sp) {
		return dataSetDao.find(sp);
	}

	@Override
	public Long count(SearchParameters sp) {
		return dataSetDao.count(sp);
	}

	@Override
	public List<DataSetRegistrationAcceptanceDecision> getStatus(List<DataSetDTO> dsUUIDs, String nodeId) {
		List<DataSetRegistrationAcceptanceDecision> result = new ArrayList<DataSetRegistrationAcceptanceDecision>();
		for(DataSetDTO dto: dsUUIDs){
			try{
				DataSet ds = dataSetDao.findByUUIDAndVersionAndNode(dto.getUuid(), dto.getVersion(), nodeId);
				result.add(ds.isAccepted() ? DataSetRegistrationAcceptanceDecision.ACCEPTED : DataSetRegistrationAcceptanceDecision.PENDING);
			}catch(NoResultException ex){
				result.add(DataSetRegistrationAcceptanceDecision.REJECTED);
			}
		}
		return result;
	}

	@Override
	public List<Compliance> getSupportedCompliances() {
		if(supportedCompliances==null){
			supportedCompliances = complianceDao.findAll();
		}
		return supportedCompliances;
	}

	@Override
	public DataSet findById(Long id) {
		return dataSetDao.findById(id);
	}

}
