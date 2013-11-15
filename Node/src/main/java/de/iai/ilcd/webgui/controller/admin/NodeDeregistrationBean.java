package de.iai.ilcd.webgui.controller.admin;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.services.NodeRegistrationService;
import de.iai.ilcd.webgui.util.Consts;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeDegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnexpectedStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;

@Component
@Scope( "view" )
public class NodeDeregistrationBean implements Serializable {

	private static final long serialVersionUID = 8805276285244939010L;

	private NodeCredentials nodeCredentials;

	private Registry registry;

	@Autowired
	private NodeRegistrationService nodeRegistrationService;

	public NodeDeregistrationBean() {
		registry = (Registry) FacesContext.getCurrentInstance().getExternalContext().getFlash().get( Consts.SELECTED_REGISTRY );
		nodeCredentials = new NodeCredentials();
		nodeCredentials.setNodeId( registry.getRegistrationData().getNodeId() );
	}

	public NodeCredentials getNodeCredentials() {
		return nodeCredentials;
	}

	public void deregister( ActionEvent event ) {
		try {
			nodeRegistrationService.deregisterNode( registry, nodeCredentials );
			FacesUtils.redirectToPage( "/admin/listRegistries" );
		}
		catch ( RestWSUnexpectedStatusException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "restWSUnexpectedStatusException_errorMessage",
					new Object[] { e.getResponseStatus() } );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
		catch ( RestWSUnknownException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "restWSUnknownException_errorMessage", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
		catch ( AuthenticationException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "authenticationException_errorMessage", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
		catch ( NodeDegistrationException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "admin.deregisterNode.nodeDegistrationException", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
	}
}
