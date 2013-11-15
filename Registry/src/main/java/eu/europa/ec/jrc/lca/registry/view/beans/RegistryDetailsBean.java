package eu.europa.ec.jrc.lca.registry.view.beans;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.DataSet_;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;
import eu.europa.ec.jrc.lca.registry.domain.Node_;
import eu.europa.ec.jrc.lca.registry.service.DataSetService;
import eu.europa.ec.jrc.lca.registry.service.NodeService;

@Component
@Scope(value="request")
public class RegistryDetailsBean {
	private SearchParameters pendingSP = new SearchParameters();
	private SearchParameters approvedSP = new SearchParameters();
	private SearchParameters pendingDSSP = new SearchParameters();
	private SearchParameters pendingUpdateDSSP = new SearchParameters();
	private SearchParameters approveDSdSP = new SearchParameters();
	
	private long numberOfPending;
	private long numberOfApproved;
	private long numberOfPendingDS;
	private long numberOfApprovedDS;
	
	@Value("${registry.name}")
	private String registryName;
	
	@Value("${registry.url}")
	private String registryUrl;
	
	@Value("${registry.description}")
	private String registryDescription;
	
	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private DataSetService dataSetService;
	
	public RegistryDetailsBean(){
		pendingSP.addFilter(Node_.status.getName(), NodeStatus.NOT_APPROVED);	
		approvedSP.addFilter(Node_.status.getName(), NodeStatus.APPROVED);	
		pendingDSSP.addFilter(DataSet_.status.getName(), DataSetStatus.NEW);	
		pendingUpdateDSSP.addFilter(DataSet_.status.getName(), DataSetStatus.UPDATE);	
		approveDSdSP.addFilter(DataSet_.status.getName(), DataSetStatus.ACCEPTED);		
	}
	
	@PostConstruct
	private void init(){
		numberOfPending = nodeService.count(pendingSP);
		numberOfApproved = nodeService.count(approvedSP);
		numberOfPendingDS = dataSetService.count(pendingDSSP) + dataSetService.count(pendingUpdateDSSP) ;
		numberOfApprovedDS = dataSetService.count(approveDSdSP);
	}

	public long getNumberOfPending() {
		return numberOfPending;
	}

	public long getNumberOfRegistered() {
		return numberOfApproved;
	}

	public long getNumberOfPendingDS() {
		return numberOfPendingDS;
	}

	public long getNumberOfRegisteredDS() {
		return numberOfApprovedDS;
	}

	public String getRegistryName() {
		return registryName;
	}

	public String getRegistryUrl() {
		return registryUrl;
	}

	public String getRegistryDescription() {
		return registryDescription;
	}
	
}
