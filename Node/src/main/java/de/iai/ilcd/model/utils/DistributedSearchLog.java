package de.iai.ilcd.model.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iai.ilcd.model.nodes.NetworkNode;

public class DistributedSearchLog {

	private Map<NetworkNode, List<Exception>> log = new HashMap<NetworkNode, List<Exception>>();

	public void logException( NetworkNode node, Exception ex ) {
		if ( log.get( node ) == null ) {
			log.put( node, new ArrayList<Exception>() );
		}
		log.get( node ).add( ex );
	}

	public List<NetworkNode> getNodes() {
		return new ArrayList<NetworkNode>( log.keySet() );
	}

	public boolean isEmpty() {
		return log.isEmpty();
	}
}
