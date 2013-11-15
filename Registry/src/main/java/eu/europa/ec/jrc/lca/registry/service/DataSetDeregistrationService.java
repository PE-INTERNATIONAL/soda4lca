package eu.europa.ec.jrc.lca.registry.service;

import java.util.List;

import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDeregistrationResult;
import eu.europa.ec.jrc.lca.commons.service.exceptions.DatasetIllegalStatusException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

public interface DataSetDeregistrationService {
	
	/**
	 * Deregistering dataset
	 * @param datasets
	 * @throws DatasetIllegalStatusException - when dataset is not in status Accepted
	 */
	void deregisterDataSets(List<DataSet> datasets)
			throws DatasetIllegalStatusException;
	
	/**
	 * Deregistering dataset
	 * @param datasets
	 * @param reason
	 * @param nodeId
	 * @return
	 */
	List<DataSetDeregistrationResult> deregisterDataSets(
			List<DataSetDTO> datasets, String reason, String nodeId);

	/**
	 * Sending information to nodes about dataset deregistration
	 * @param datasets
	 * @param reason
	 */
	void broadcastDataSetsDeregistration(List<DataSet> datasets, String reason);
	
	// For test purposes only
	void setBroadcastingService(BroadcastingService broadcastingService);
}
