package eu.europa.ec.jrc.lca.registry.domain;

import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(eu.europa.ec.jrc.lca.registry.domain.DataSetAuditLog.class)
public class DataSetAuditLog_ {
	public static volatile SingularAttribute<DataSetAuditLog, Long> id;
	public static volatile SingularAttribute<DataSetAuditLog, Long> dataSetId;
	public static volatile SingularAttribute<DataSetAuditLog, String> uuid;
	public static volatile SingularAttribute<DataSetAuditLog, String> version;
}
