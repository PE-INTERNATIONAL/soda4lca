package de.iai.ilcd.webgui.controller.admin;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.internet.AddressException;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.fzk.iai.ilcd.service.client.impl.vo.NodeInfo;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.services.NodeRegistrationService;
import de.iai.ilcd.webgui.util.Consts;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeRegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnexpectedStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;
import eu.europa.ec.jrc.lca.registry.domain.Node;

@Component
@Scope( "view" )
public class NodeRegistrationBean implements Serializable {

	private static final long serialVersionUID = 8805276285244939010L;

	private Node node;

	private final Registry registry;

	@Autowired
	private NodeRegistrationService nodeRegistrationService;

	private final UserDao userDao = new UserDao();

	public NodeRegistrationBean() {
		this.registry = (Registry) FacesContext.getCurrentInstance().getExternalContext().getFlash().get( Consts.SELECTED_REGISTRY );
		this.node = this.prepareNodeRegistrationData();
	}

	private Node prepareNodeRegistrationData() {
		User currentUser = this.userDao.getUser( (String) SecurityUtils.getSubject().getPrincipal() );
		NodeInfo nInf = ConfigurationService.INSTANCE.getNodeInfo();

		Node n = new Node();
		n.setNodeCredentials( new NodeCredentials() );
		n.setAdminEmailAddress( currentUser.getEmail() );
		n.setAdminName( (currentUser.getFirstName() == null ? "" : currentUser.getFirstName()) + " "
				+ (currentUser.getLastName() == null ? "" : currentUser.getLastName()) );
		n.setAdminPhone( nInf.getAdminPhone() );
		n.setAdminWebAddress( n.getAdminWebAddress() );
		n.setName( nInf.getName() );
		n.setNodeId( nInf.getNodeID() );
		n.setDescription( nInf.getDescription() != null ? nInf.getDescription().getValue() : null );

		// for registering at the registry, URL must be without trailing "/resource/" path
		String nodeUrl = nInf.getBaseURL();
		if ( nodeUrl.endsWith( "/" ) )
			nodeUrl = nodeUrl.substring( 0, nodeUrl.length() - 1 );
		if ( nodeUrl.endsWith( "/resource" ) )
			nodeUrl = nodeUrl.substring( 0, nodeUrl.length() - 9 );
		n.setBaseUrl( nodeUrl );

		return n;
	}

	public Node getNode() {
		return this.node;
	}

	public void setNode( Node node ) {
		this.node = node;
	}

	public void register() {
		try {
			this.nodeRegistrationService.registerNode( this.registry, this.node );
			FacesUtils.redirectToPage( "/admin/listRegistries" );
		}
		catch ( NodeRegistrationException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "admin.registerNode.nodeRegistrationException", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
		catch ( RestWSUnexpectedStatusException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "nodeRegistration_restWSUnexpectedStatusException_errorMessage", new Object[] { e
					.getResponseStatus() } );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
		catch ( RestWSUnknownException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "restWSUnknownException_errorMessage", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
		catch ( AddressException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "addressException_errorMessage", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
	}

	public Registry getRegistry() {
		return this.registry;
	}

}
