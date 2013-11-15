package eu.europa.ec.jrc.lca.registry.view.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.domain.RegistryCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeDegistrationException;
import eu.europa.ec.jrc.lca.commons.view.model.ILCDLazyDataModel;
import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;
import eu.europa.ec.jrc.lca.registry.domain.Node_;
import eu.europa.ec.jrc.lca.registry.service.NodeDeregistrationService;
import eu.europa.ec.jrc.lca.registry.service.NodeService;

@Component
@Scope("view")
public class ListNodesBean implements Serializable {

	private static final long serialVersionUID = -1036320786375758293L;

	@Autowired
	private NodeService nodeService;

	@Autowired
	private NodeDeregistrationService nodeDeregistrationService;
	
	private LazyDataModel<Node> lazyModel;

	private Node selectedNode;

	private String reason;
	
	private Boolean approved = Boolean.FALSE;
	
	@PostConstruct
	public void initBean() {
		Boolean approvedParam = geApprovedParameter();
		SearchParameters sp = new SearchParameters();
		if (approvedParam) {
			sp.addFilter(Node_.status.getName(), NodeStatus.APPROVED);
		} else {
			sp.addFilter(Node_.status.getName(), NodeStatus.NOT_APPROVED);
		}
		lazyModel = new ILCDLazyDataModel<Node>(nodeService, sp);
	}

	public LazyDataModel<Node> getNodes() {
		return lazyModel;
	}

	public Node getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(Node selectedNode) {
		this.selectedNode = selectedNode;
	}

	public void showDetails() {
		FacesUtils.redirectToPage("/secured/nodeDetails?id="+selectedNode.getId());
	}

	private Boolean geApprovedParameter() {
		String notApproved = ((HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest())
				.getParameter("approved");
		if(notApproved!=null){
			approved = Boolean.valueOf(notApproved);
			return approved;
		}
		return Boolean.FALSE;
	}
	
	public void deregisterNode(){
		try {
			RegistryCredentials rc = selectedNode.getRegistryCredentials();
			NodeChangeLog log = nodeDeregistrationService.deregisterNode(selectedNode, reason);
			nodeDeregistrationService.broadcastNodesChangeInformationToDeregistered(log, selectedNode, rc);
			nodeDeregistrationService.broadcastNodeDeregistration(log);
			
			FacesMessage message = Messages.getMessage(null, "nodeHasBeenDeregistered", null);
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (NodeDegistrationException e) {
			FacesMessage message = Messages.getMessage(null, "nodeDegistrationException_errorMessage", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		reason = null;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Boolean getApproved() {
		return approved;
	}
	
	
}
