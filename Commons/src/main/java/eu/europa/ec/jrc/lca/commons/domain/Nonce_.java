package eu.europa.ec.jrc.lca.commons.domain;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(eu.europa.ec.jrc.lca.commons.domain.NodeCredentials.class)
public class Nonce_ {
	public static volatile SingularAttribute<Nonce, Long> id;
	public static volatile SingularAttribute<Nonce, Date> useDate;
	public static volatile SingularAttribute<Nonce, byte[]> value;
}
