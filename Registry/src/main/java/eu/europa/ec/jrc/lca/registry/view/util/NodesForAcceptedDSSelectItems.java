package eu.europa.ec.jrc.lca.registry.view.util;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class NodesForAcceptedDSSelectItems extends NodesSelectItems{
	@PostConstruct
	public void init(){
		setEntities(nodeService.getListOfNodesForAcceptedDatasets());
		addNotSelectedItem();
	}
}
