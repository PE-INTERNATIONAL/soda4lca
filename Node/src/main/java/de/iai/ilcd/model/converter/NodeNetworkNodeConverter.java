package de.iai.ilcd.model.converter;

import de.iai.ilcd.model.nodes.NetworkNode;
import eu.europa.ec.jrc.lca.registry.domain.Node;

public class NodeNetworkNodeConverter {

	public static Node getAsNode( NetworkNode nn ) {
		Node node = new Node();
		node.setAdminEmailAddress( nn.getAdminEmailAddress() );
		node.setAdminName( nn.getAdminName() );
		node.setAdminPhone( nn.getAdminPhone() );
		node.setAdminWebAddress( nn.getAdminWwwAddress() );
		node.setBaseUrl( nn.getBaseUrl() );
		node.setDescription( nn.getDescription() );
		node.setName( nn.getName() );
		node.setNodeId( nn.getNodeId() );
		return node;
	}

	public static NetworkNode getAsNetworkNode( Node node ) {
		NetworkNode nn = new NetworkNode();
		nn.setAdminEmailAddress( node.getAdminEmailAddress() );
		nn.setAdminName( node.getAdminName() );
		nn.setAdminPhone( node.getAdminPhone() );
		nn.setAdminWwwAddress( node.getAdminWebAddress() );
		nn.setBaseUrl( node.getBaseUrl() );
		nn.setDescription( node.getDescription() );
		nn.setName( node.getName() );
		nn.setNodeId( node.getNodeId() );
		return nn;
	}
}
