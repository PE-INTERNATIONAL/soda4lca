package de.iai.ilcd.webgui.controller.admin;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.registry.RegistryStatus;
import de.iai.ilcd.services.RegistryService;
import de.iai.ilcd.webgui.util.Consts;
import eu.europa.ec.jrc.lca.commons.view.model.ILCDLazyDataModel;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;

@Component
@Scope( "view" )
public class ListRegistriesBean implements Serializable {

	private static final long serialVersionUID = 3939778844586935324L;

	private LazyDataModel<Registry> lazyModel;

	private Registry selectedRegistry;

	@Autowired
	private RegistryService registryService;

	@PostConstruct
	public void init() {
		this.lazyModel = new ILCDLazyDataModel<Registry>( this.registryService );
	}

	public LazyDataModel<Registry> getRegistries() {
		return this.lazyModel;
	}

	public Registry getSelectedRegistry() {
		return this.selectedRegistry;
	}

	public void setSelectedRegistry( Registry selectedRegistry ) {
		this.selectedRegistry = selectedRegistry;
	}

	public String showDetails() {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put( Consts.SELECTED_REGISTRY, this.selectedRegistry );
		return "/admin/registryDetails";
	}

	public String registerNodeInRegistry() {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put( Consts.SELECTED_REGISTRY, this.selectedRegistry );
		return "/admin/registerNode";
	}

	public String deregisterNodeFromRegistry() {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put( Consts.SELECTED_REGISTRY, this.selectedRegistry );
		return "/admin/deregisterNode";
	}

	public void deleteRegistry() {
		if ( this.selectedRegistry.getStatus() == RegistryStatus.NOT_REGISTERED ) {
			this.registryService.removeRegistry( this.selectedRegistry.getId() );
			FacesMessage message = Messages.getMessage( "resources.lang", "admin.deleteRegistry.registryHasBeenRemoved", null );
			message.setSeverity( FacesMessage.SEVERITY_INFO );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
	}
}
