package de.iai.ilcd.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.delegate.DataSetRestServiceBD;
import de.iai.ilcd.delegate.NodeRestServiceBD;
import de.iai.ilcd.model.converter.NodeNetworkNodeConverter;
import de.iai.ilcd.model.dao.DataSetRegistrationDataDao;
import de.iai.ilcd.model.dao.NetworkNodeDao;
import de.iai.ilcd.model.nodes.NetworkNode;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationAcceptanceDecision;
import eu.europa.ec.jrc.lca.commons.security.SecurityContext;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnexpectedStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.Node;

@Component( "synchronizator" )
public class SynchronizatorImpl implements Synchronizator {

	private static final Logger LOGGER = LoggerFactory.getLogger( SynchronizatorImpl.class );

	@Autowired
	private RegistryService registryService;

	@Autowired
	private SecurityContext securityContext;

	private NetworkNodeDao networkNodeDao = new NetworkNodeDao();

	@Autowired
	private DataSetRegistrationDataDao dataSetRegistrationDataDao;

	@Autowired
	private NodeRegistrationService nodeRegistrationService;

	/*
	 * scheduled in spring-context.xml
	 */
	private void synchronizeAll() {
		LOGGER.info( "===========synchronizeAll===========" );
		List<Registry> regs = registryService.getNonVirtualRegistriesInWhichRegistered();
		for ( Registry reg : regs ) {
			try {
				if ( isRegistered( reg ) ) {
					synchronizeNodes( reg );
					synchronizeDataSets( reg );
				}
				else {
					nodeRegistrationService.deregisterNode( reg );
				}
			}
			catch ( AuthenticationException e ) {
				LOGGER.error( "[synchronizeAll]", e );
			}
			catch ( RestWSUnexpectedStatusException e ) {
				LOGGER.error( "[synchronizeAll]", e );
			}
			catch ( RestWSUnknownException e ) {
				LOGGER.error( "[synchronizeAll]", e );
			}
		}
	}

	@Override
	public void synchronizeNodes( Registry reg ) throws AuthenticationException, RestWSUnexpectedStatusException, RestWSUnknownException {
		if ( isRegistered( reg ) ) {
			reg.getNodeCredentials().decrypt( securityContext.getPrivateKey() );
			NodeCredentials cred = reg.getNodeCredentials().getEncryptedCopyWithNonce( NodeRestServiceBD.getInstance( reg ).getPublicKey() );
			List<Node> nodes = NodeRestServiceBD.getInstance( reg ).wake( cred );
			updateNodesList( reg, nodes );
		}
		else {
			nodeRegistrationService.deregisterNode( reg );
		}
	}

	@Override
	public void synchronizeDataSets( Registry reg ) throws AuthenticationException, RestWSUnexpectedStatusException, RestWSUnknownException {
		if ( isRegistered( reg ) ) {
			List<DataSetRegistrationData> dsRegData = dataSetRegistrationDataDao.getRegistered( reg.getId() );
			if ( dsRegData.isEmpty() ) {
				return;
			}
			reg.getNodeCredentials().decrypt( securityContext.getPrivateKey() );
			NodeCredentials cred = reg.getNodeCredentials().getEncryptedCopyWithNonce( NodeRestServiceBD.getInstance( reg ).getPublicKey() );
			List<DataSetRegistrationAcceptanceDecision> status = DataSetRestServiceBD.getInstance( reg ).checkStatus( getDsDTOs( dsRegData ), cred );
			updateRegistrationData( status, dsRegData );
		}
		else {
			nodeRegistrationService.deregisterNode( reg );
		}
	}

	private boolean isRegistered( Registry reg ) {
		try {
			return ConfigurationService.INSTANCE.isRegistryBasedNetworking()
					&& NodeRestServiceBD.getInstance( reg ).getNodeData( reg.getNodeCredentials().getNodeId() ) != null;
		}
		catch ( RestWSUnknownException e ) {
			return true;
		}
	}

	private void updateNodesList( Registry registry, List<Node> nodes ) {
		Set<NetworkNode> receivedNodes = getNodesSetFromList( nodes );

		for ( NetworkNode nn : registry.getNodes() ) {
			if ( !receivedNodes.contains( nn ) ) {
				networkNodeDao.remove( nn );
			}
		}
		registry.getNodes().retainAll( receivedNodes );
		receivedNodes.removeAll( registry.getNodes() );
		registry.getNodes().addAll( receivedNodes );

		for ( NetworkNode nn : receivedNodes ) {
			nn.setRegistry( registry );
			networkNodeDao.saveOrUpdate( nn );
		}
		registryService.saveOrUpdate( registry );
	}

	private Set<NetworkNode> getNodesSetFromList( List<Node> nodes ) {
		Set<NetworkNode> nodesSet = new HashSet<NetworkNode>();
		for ( Node n : nodes ) {
			nodesSet.add( NodeNetworkNodeConverter.getAsNetworkNode( n ) );
		}
		return nodesSet;
	}

	private List<DataSetDTO> getDsDTOs( List<DataSetRegistrationData> dsRegData ) {
		List<DataSetDTO> dtos = new ArrayList<DataSetDTO>();
		for ( DataSetRegistrationData dsr : dsRegData ) {
			dtos.add( new DataSetDTO( dsr.getUuid(), dsr.getVersion().getVersionString() ) );
		}
		return dtos;
	}

	private void updateRegistrationData( List<DataSetRegistrationAcceptanceDecision> status, List<DataSetRegistrationData> dsRegData ) {
		for ( int i = 0; i < dsRegData.size(); i++ ) {
			if ( status.get( i ) == DataSetRegistrationAcceptanceDecision.REJECTED ) {
				dataSetRegistrationDataDao.remove( dsRegData.get( i ).getId() );
			}
		}
	}

}
