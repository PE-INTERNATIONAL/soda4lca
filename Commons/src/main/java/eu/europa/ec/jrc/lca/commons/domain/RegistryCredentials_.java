package eu.europa.ec.jrc.lca.commons.domain;

import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials.class)
public class RegistryCredentials_ {
	public static volatile SingularAttribute<RegistryCredentials, Long> id;
	public static volatile SingularAttribute<RegistryCredentials, String> accessAccount;
	public static volatile SingularAttribute<RegistryCredentials, byte[]> encryptedPassword;
}
