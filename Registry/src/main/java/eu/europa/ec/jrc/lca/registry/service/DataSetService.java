package eu.europa.ec.jrc.lca.registry.service;

import java.util.List;

import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationAcceptanceDecision;
import eu.europa.ec.jrc.lca.commons.service.LazyLoader;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.registry.domain.Compliance;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

public interface DataSetService extends LazyLoader<DataSet> {
	
	/**
	 * Checking if dataset hash is equal to the registered value
	 * @param dataset
	 * @return
	 * @throws ResourceNotFoundException
	 */
	boolean verifyDataSet(DataSet dataset) throws ResourceNotFoundException;
	
	/**
	 * Checking dataset acceptance status
	 * @param dsUUIDs
	 * @param nodeId
	 * @return
	 */
	List<DataSetRegistrationAcceptanceDecision> getStatus(List<DataSetDTO> dsUUIDs, String nodeId);
	
	/**
	 * @return List of Compliances supported by registry
	 */
	List<Compliance> getSupportedCompliances();
	
	/**
	 * Finds dataset by DB id
	 * @param id
	 * @return
	 */
	DataSet findById(Long id);
}
