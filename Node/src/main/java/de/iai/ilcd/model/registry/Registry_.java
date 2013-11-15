package de.iai.ilcd.model.registry;

import java.util.Set;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import de.iai.ilcd.model.nodes.NetworkNode;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;

@javax.persistence.metamodel.StaticMetamodel( de.iai.ilcd.model.registry.Registry.class )
public class Registry_ {

	public static volatile SingularAttribute<Registry, Long> id;

	public static volatile SingularAttribute<Registry, String> name;

	public static volatile SingularAttribute<Registry, String> description;

	public static volatile SingularAttribute<Registry, String> baseUrl;

	public static volatile SingularAttribute<Registry, RegistryStatus> status;

	public static volatile SingularAttribute<Registry, RegistrationData> registrationData;

	public static volatile SetAttribute<Registry, Set<NetworkNode>> nodes;

	public static volatile SingularAttribute<Registry, NodeCredentials> nodeCredentials;

	public static volatile SingularAttribute<Registry, RegistryCredentials> registryCredentials;

	public static volatile SingularAttribute<Registry, String> uuid;
}
