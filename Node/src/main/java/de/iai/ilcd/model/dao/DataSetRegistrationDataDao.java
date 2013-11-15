package de.iai.ilcd.model.dao;

import java.util.List;

import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.registry.DataSetRegistrationData;

public interface DataSetRegistrationDataDao extends GenericDAO<DataSetRegistrationData, Long> {

	List<DataSetRegistrationData> getRegistered( Long registryId );

	List<DataSetRegistrationData> getRegisteredNotAccepted( Long registryId );

	List<DataSetRegistrationData> getRegisteredAccepted( Long registryId );

	List<DataSetRegistrationData> getRegisteredPendingDeregistration( Long registryId );

	List<DataSetRegistrationData> getListOfRegistrations( Process process );

	DataSetRegistrationData findByUUIDAndVersionAndRegistry( String UUID, String version, Long registryId );
}
