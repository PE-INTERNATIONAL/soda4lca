package de.iai.ilcd.model.dao;

import org.springframework.stereotype.Repository;

import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;

@Repository( value = "nodeCredentialsDao" )
public class NodeCredentialsDaoImpl extends GenericDAOImpl<NodeCredentials, Long> implements NodeCredentialsDao {

}
