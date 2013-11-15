package eu.europa.ec.jrc.lca.commons.domain;

import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(eu.europa.ec.jrc.lca.commons.domain.NodeCredentials.class)
public class NodeCredentials_ {
	public static volatile SingularAttribute<NodeCredentials, Long> id;
	public static volatile SingularAttribute<NodeCredentials, String> accessAccount;
	public static volatile SingularAttribute<NodeCredentials, byte[]> encryptedPassword;
	public static volatile SingularAttribute<NodeCredentials, String> nodeId;
}
