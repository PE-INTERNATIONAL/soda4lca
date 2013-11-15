package de.iai.ilcd.model.registry;

import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel( de.iai.ilcd.model.registry.DatasetDeregistrationReason.class )
public class DatasetDeregistrationReason_ {

	public static volatile SingularAttribute<DatasetDeregistrationReason, Long> id;

	public static volatile SingularAttribute<DatasetDeregistrationReason, byte[]> reason;
}
