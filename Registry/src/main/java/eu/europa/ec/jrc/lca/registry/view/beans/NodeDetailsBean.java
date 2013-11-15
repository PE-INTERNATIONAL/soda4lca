package eu.europa.ec.jrc.lca.registry.view.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.view.model.ILCDLazyDataModel;
import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.NodeAuditLog;
import eu.europa.ec.jrc.lca.registry.domain.NodeAuditLog_;
import eu.europa.ec.jrc.lca.registry.service.NodeHistoryService;
import eu.europa.ec.jrc.lca.registry.service.NodeRegistrationService;
import eu.europa.ec.jrc.lca.registry.service.NodeService;

@Component
@Scope("view")
public class NodeDetailsBean implements Serializable{
	
	private static final long serialVersionUID = -233582135536772900L;

	@Autowired
	private NodeRegistrationService registrationService;
	
	@Autowired
	private NodeHistoryService nodeHistoryService;
	
	private Node node;
	
	@Autowired
	private NodeService nodeService;
	
	private String reason;
	
	private LazyDataModel<NodeAuditLog> lazyModel;
	
	private NodeAuditLog selectedOperation;
	
	public NodeDetailsBean(){
	}

	@PostConstruct
	private void init(){
		String id = ((HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest())
				.getParameter("id");
		this.node = nodeService.findById(Long.valueOf(id));
		if(node!=null){
			SearchParameters sp = new SearchParameters();
			sp.addFilter(NodeAuditLog_.nodeId.getName(), node.getId());
			sp.setSortField(NodeAuditLog_.operationTime.getName());
			sp.setAscending(false);
			lazyModel = new ILCDLazyDataModel<NodeAuditLog>(nodeHistoryService, sp);
		}
	}
	
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void acceptRegistration(ActionEvent event){
		try {
			node = registrationService.acceptNodeRegistration(node);
			FacesMessage message = Messages.getMessage(null, "registrationRequestHasBeenAccepted", null);
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (NodeIllegalStatusException e) {
			FacesMessage message = Messages.getMessage(null, "nodeNotInNotApprovedState", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	public void rejectRegistration(ActionEvent event){
		try {
			registrationService.rejectNodeRegistration(node, reason);
			FacesMessage message = Messages.getMessage(null, "registrationRequestHasBeenRejected", null);
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
			FacesUtils.redirectToPage("/secured/nodes.xhtml?notApproved=true");
		} catch (NodeIllegalStatusException e) {
			FacesMessage message = Messages.getMessage(null, "nodeNotInNotApprovedState", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	public LazyDataModel<NodeAuditLog> getNodeHistory(){
		return lazyModel;
	}
	
	public void setSelectedOperation(NodeAuditLog selectedOperation){
		this.selectedOperation = nodeHistoryService.getWithDetails(selectedOperation.getId());
	}

	public NodeAuditLog getSelectedOperation() {
		return selectedOperation;
	}
	
}
