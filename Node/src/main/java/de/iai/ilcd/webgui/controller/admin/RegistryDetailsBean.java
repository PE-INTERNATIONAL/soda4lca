package de.iai.ilcd.webgui.controller.admin;

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

import de.iai.ilcd.model.dao.NetworkNodeDao;
import de.iai.ilcd.model.nodes.NetworkNode;
import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.registry.RegistryStatus;
import de.iai.ilcd.services.RegistryService;
import de.iai.ilcd.services.Synchronizator;
import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnexpectedStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.commons.view.model.ILCDLazyDataModel;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;

@Component( "registryDetailsBean" )
@Scope( "view" )
public class RegistryDetailsBean implements Serializable {

	private static final long serialVersionUID = -5858889667350732641L;

	private Registry registry;

	private LazyDataModel<NetworkNode> lazyModel;

	private final NetworkNodeDao networkNodeDao = new NetworkNodeDao();

	private boolean edit;

	private NetworkNode selectedNode;

	@Autowired
	private RegistryService registryService;

	@Autowired
	private Synchronizator synchronizator;

	public RegistryDetailsBean() {
	}

	@PostConstruct
	public void init() {
		String id = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter( "id" );
		String editParam = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter( "edit" );
		if ( id == null ) {
			this.registry = new Registry();
			this.edit = true;
		}
		else if ( "true".equals( editParam ) ) {
			this.registry = this.registryService.findById( Long.valueOf( id ) );
			this.edit = true;
		}
		else {
			this.registry = this.registryService.findById( Long.valueOf( id ) );
			SearchParameters sp = new SearchParameters();
			sp.addFilter( "registry", this.registry );
			this.lazyModel = new ILCDLazyDataModel<NetworkNode>( this.networkNodeDao, sp );
		}
	}

	public NetworkNode getSelectedNode() {
		return this.selectedNode;
	}

	public void setSelectedNode( NetworkNode selectedNode ) {
		this.selectedNode = selectedNode;
	}

	public Registry getRegistry() {
		return this.registry;
	}

	public void setRegistry( Registry registry ) {
		this.registry = registry;
	}

	public LazyDataModel<NetworkNode> getNodes() {
		return this.lazyModel;
	}

	public void synchronize( ActionEvent event ) {
		try {
			this.synchronizator.synchronizeNodes( this.registry );
			FacesMessage message = Messages.getMessage( "resources.lang", "admin.registryDetails.successfull_synchronization", null );
			message.setSeverity( FacesMessage.SEVERITY_INFO );
			FacesContext.getCurrentInstance().addMessage( null, message );
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
	}

	public String save() {
		try {
			Registry found = this.registryService.findByUUID( this.registry.getUuid() );
			if ( !found.getId().equals( this.registry.getId() ) ) {
				FacesMessage message = Messages.getMessage( "resources.lang", "registryWithSameUUIDAlreadyExists", null );
				message.setSeverity( FacesMessage.SEVERITY_ERROR );
				FacesContext.getCurrentInstance().addMessage( null, message );
				return null;
			}
		}
		catch ( Exception e ) {
		}

		try {
			Registry found = this.registryService.findByUrl( this.registry.getBaseUrl() );
			if ( !found.getId().equals( this.registry.getId() ) ) {
				FacesMessage message = Messages.getMessage( "resources.lang", "registryWithSameUrlAlreadyExists", null );
				message.setSeverity( FacesMessage.SEVERITY_ERROR );
				FacesContext.getCurrentInstance().addMessage( null, message );
				return null;
			}
		}
		catch ( Exception e ) {
		}

		if ( !this.registry.getBaseUrl().endsWith( "/" ) ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "admin.registryDetails.baseUrlValidationMessage", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
			return null;
		}

		if ( this.registry.getStatus() == null ) {
			this.registry.setStatus( RegistryStatus.NOT_REGISTERED );
		}

		this.registryService.saveOrUpdate( this.registry );

		FacesMessage message = Messages.getMessage( "resources.lang", "admin.registryDetails.registryHasBeenSaved", null );
		message.setSeverity( FacesMessage.SEVERITY_INFO );
		FacesContext.getCurrentInstance().addMessage( null, message );

		return "/admin/listRegistries";
	}

	public boolean isEdit() {
		return this.edit;
	}

}
