package eu.europa.ec.jrc.lca.registry.service;

import java.util.List;

import eu.europa.ec.jrc.lca.commons.service.exceptions.DatasetIllegalStatusException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

public interface DataSetAcceptanceService {
	/**
	 * Accepting dataset registration requests
	 * @param datasets
	 */
	void acceptDataSets(List<DataSet> datasets);
	
	/**
	 * Accepting dataset update requests
	 * @param dataset
	 * @throws DatasetIllegalStatusException
	 */
	void acceptUpdateDataSet(DataSet dataset) throws DatasetIllegalStatusException;
	
	/**
	 * Rejecting dataset registration requests
	 * @param datasets
	 * @param reason
	 */
	void rejectDataSets(List<DataSet> datasets, String reason);
}
