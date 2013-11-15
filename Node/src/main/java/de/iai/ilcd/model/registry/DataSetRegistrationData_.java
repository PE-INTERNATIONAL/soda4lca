package de.iai.ilcd.model.registry;

import javax.persistence.metamodel.SingularAttribute;

import de.iai.ilcd.model.common.DataSetVersion;

@javax.persistence.metamodel.StaticMetamodel( de.iai.ilcd.model.registry.DataSetRegistrationData.class )
public class DataSetRegistrationData_ {

	public static volatile SingularAttribute<DataSetRegistrationData, Long> id;

	public static volatile SingularAttribute<DataSetRegistrationData, Registry> registry;

	public static volatile SingularAttribute<DataSetRegistrationData, DataSetRegistrationDataStatus> status;

	public static volatile SingularAttribute<DataSetRegistrationData, DatasetDeregistrationReason> reason;

	public static volatile SingularAttribute<DataSetRegistrationData, String> uuid;

	public static volatile SingularAttribute<DataSetRegistrationData, DataSetVersion> version;
}
