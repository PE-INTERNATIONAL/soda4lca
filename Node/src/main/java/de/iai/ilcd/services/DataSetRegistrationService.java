package de.iai.ilcd.services;

import java.util.List;

import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationResult;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;

public interface DataSetRegistrationService {

	/**
	 * Getting list of registered datasets
	 * 
	 * @param registryId
	 * @return
	 */
	public List<DataSetRegistrationData> getRegistered( Long registryId );

	/**
	 * Getting list of registered but not accepted yet datasets
	 * 
	 * @param registryId
	 * @return
	 */
	public List<DataSetRegistrationData> getRegisteredNotAccepted( Long registryId );

	/**
	 * Getting list of registered and accepted
	 * 
	 * @param registryId
	 * @return
	 */
	public List<DataSetRegistrationData> getRegisteredAccepted( Long registryId );

	/**
	 * @param process
	 * @return
	 */
	public List<DataSetRegistrationData> getListOfRegistrations( Process process );

	/**
	 * Degistering datasets
	 * 
	 * @param processes
	 * @param registryId
	 * @return
	 * @throws NodeIllegalStatusException
	 * @throws RestWSUnknownException
	 * @throws AuthenticationException
	 */
	public List<DataSetRegistrationResult> register( List<Process> processes, Long registryId ) throws NodeIllegalStatusException, RestWSUnknownException,
			AuthenticationException;

}
