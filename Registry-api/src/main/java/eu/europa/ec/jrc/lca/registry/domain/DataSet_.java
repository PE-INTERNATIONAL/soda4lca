package eu.europa.ec.jrc.lca.registry.domain;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(eu.europa.ec.jrc.lca.registry.domain.DataSet.class)
public class DataSet_ {
	public static volatile SingularAttribute<DataSet, Long> id;
	public static volatile SingularAttribute<DataSet, String> uuid;
	public static volatile SingularAttribute<DataSet, String> name;
	public static volatile SingularAttribute<DataSet, byte[]> hash;
	public static volatile ListAttribute<DataSet, String> complianceUUIDs;
	public static volatile SingularAttribute<DataSet, String> user;
	public static volatile SingularAttribute<DataSet, String> userEmail;
	public static volatile SingularAttribute<DataSet, String> owner;
	public static volatile SingularAttribute<DataSet, Node> node;
	public static volatile SingularAttribute<DataSet, DataSetStatus> status;
	public static volatile SingularAttribute<DataSet, String> version;
}
