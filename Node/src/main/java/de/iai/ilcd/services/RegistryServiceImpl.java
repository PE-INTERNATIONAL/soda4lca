package de.iai.ilcd.services;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.iai.ilcd.delegate.NodeRestServiceBD;
import de.iai.ilcd.model.converter.NodeNetworkNodeConverter;
import de.iai.ilcd.model.dao.NetworkNodeDao;
import de.iai.ilcd.model.dao.RegistryDao;
import de.iai.ilcd.model.nodes.NetworkNode;
import de.iai.ilcd.model.registry.RegistrationData;
import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;
import eu.europa.ec.jrc.lca.registry.domain.OperationType;

@Service( "registryService" )
public class RegistryServiceImpl implements RegistryService {

	@Autowired
	private RegistryDao registryDao;

	private NetworkNodeDao networkNodeDao = new NetworkNodeDao();

	@Autowired
	private NodeRegistrationService nodeRegistrationService;

	@Override
	public List<Registry> loadLazy( SearchParameters sp ) {
		return registryDao.find( sp );
	}

	@Override
	public Long count( SearchParameters sp ) {
		return registryDao.count( sp );
	}

	@Override
	public Registry findByIdentity( String identity ) {
		return registryDao.findByIdentity( identity );
	}

	@Override
	public Registry saveOrUpdate( Registry registry ) {
		return registryDao.saveOrUpdate( registry );
	}

	@Override
	public List<Registry> getNonVirtualRegistriesInWhichRegistered() {
		return registryDao.getNonVirtualRegistriesInWhichRegistered();
	}

	@Override
	public List<Registry> getRegistriesToCheckAcceptance() {
		return registryDao.getRegistriesToCheckAcceptance();
	}

	@Override
	public Node getNodeForRegistry( Registry reg ) {
		Registry registry = registryDao.findById( reg.getId() );
		RegistrationData regData = registry.getRegistrationData();
		Node node = new Node();
		node.setNodeId( regData.getNodeId() );
		return node;
	}

	@Override
	public void applyChange( String registryId, NodeChangeLog changeLog ) throws RestWSUnknownException {
		Registry registry = registryDao.findByIdentity( registryId );
		Node synchronizedNode = NodeRestServiceBD.getInstance( registry ).getNodeData( changeLog.getNodeId() );
		if ( changeLog.getOperationType() == OperationType.DEREGISTER ) {
			if ( changeLog.getNodeId().equals( registry.getRegistrationData().getNodeId() ) ) {
				nodeRegistrationService.deregisterNode( registry );
			}
			else if ( synchronizedNode == null ) {
				removeNode( registry, changeLog );
			}
		}
		else if ( changeLog.getOperationType() == OperationType.REGISTER ) {
			if ( synchronizedNode != null ) {
				addNode( registry, synchronizedNode );
			}
		}
	}

	private void removeNode( Registry registry, NodeChangeLog changeLog ) {
		for ( Iterator<NetworkNode> iter = registry.getNodes().iterator(); iter.hasNext(); ) {
			NetworkNode nn = iter.next();
			if ( nn.getNodeId().equals( changeLog.getNodeId() ) ) {
				iter.remove();
				networkNodeDao.remove( nn );
				registryDao.saveOrUpdate( registry );
				break;
			}
		}
	}

	private void addNode( Registry registry, Node synchronizedNode ) {
		NetworkNode nn = NodeNetworkNodeConverter.getAsNetworkNode( synchronizedNode );
		registry.getNodes().add( nn );
		nn.setRegistry( registry );
		registryDao.saveOrUpdate( registry );
	}

	@Override
	public Registry findById( Long id ) {
		return registryDao.findById( id );
	}

	@Override
	public Registry findByUUID( String uuid ) {
		return registryDao.findByUUID( uuid );
	}

	@Override
	public Registry findByUrl( String url ) {
		return registryDao.findByUrl( url );
	}

	@Override
	public void removeRegistry( Long registryId ) {
		registryDao.remove( registryId );
	}

	@Override
	public List<Registry> getRegistriesInWhichRegistered() {
		return registryDao.getRegistriesInWhichRegistered();
	}

	@Override
	public void removeNode( Long nodeId ) {
		networkNodeDao.remove( nodeId );
	}
}
