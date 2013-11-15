package eu.europa.ec.jrc.lca.registry.domain;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;

@javax.persistence.metamodel.StaticMetamodel(eu.europa.ec.jrc.lca.registry.domain.Node.class)
public class Node_ {

	public static volatile SingularAttribute<Node, Long> id;
	public static volatile SingularAttribute<Node, String> nodeId;
	public static volatile SingularAttribute<Node, String> name;
	public static volatile SingularAttribute<Node, String> description;
	public static volatile SingularAttribute<Node, String> baseUrl;
	public static volatile SingularAttribute<Node, String> adminName;
	public static volatile SingularAttribute<Node, String> adminEmailAddress;
	public static volatile SingularAttribute<Node, String> adminPhone;
	public static volatile SingularAttribute<Node, String> adminWebAddress;
	public static volatile SingularAttribute<Node, NodeStatus> status;
	public static volatile SingularAttribute<Node, Date> lastActiveSignal;
	public static volatile SingularAttribute<Node, Long> identity;
	public static volatile SingularAttribute<Node, NodeCredentials> nodeCredentials;
	public static volatile SingularAttribute<Node, RegistryCredentials> registryCredentials;
}
