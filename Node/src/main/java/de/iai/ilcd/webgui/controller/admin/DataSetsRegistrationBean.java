package de.iai.ilcd.webgui.controller.admin;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import de.iai.ilcd.services.DataSetRegistrationService;
import de.iai.ilcd.webgui.util.Consts;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationResult;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;

@Component
@Scope( "view" )
public class DataSetsRegistrationBean implements Serializable {

	private static final long serialVersionUID = -1734997832360544545L;

	@Autowired
	private DataSetRegistrationService dataSetRegistrationService;

	private final List<Process> processes;

	private final Set<String> registeredProcesses = new HashSet<String>();

	private Long registry;

	@SuppressWarnings( "unchecked" )
	public DataSetsRegistrationBean() {
		this.processes = (List<Process>) FacesContext.getCurrentInstance().getExternalContext().getFlash().get( Consts.SELECTED_DATASETS );
	}

	public List<Process> getProcesses() {
		return this.processes;
	}

	public Long getRegistry() {
		return this.registry;
	}

	public void setRegistry( Long registry ) {
		this.registry = registry;
		this.getRegisteredDataSets();
	}

	private void getRegisteredDataSets() {
		this.registeredProcesses.clear();
		if ( this.registry != null ) {
			Collection<DataSetRegistrationData> registrationData = this.dataSetRegistrationService.getRegistered( this.registry );
			for ( DataSetRegistrationData dsrd : registrationData ) {
				this.registeredProcesses.add( dsrd.getUuid() + dsrd.getVersion() );
			}
		}
	}

	public boolean isRegistered( Process process ) {
		return this.registeredProcesses.contains( process.getUuidAsString() + process.getVersion().getVersionString() );
	}

	public void register() {
		if ( this.registry == null ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "registryIsReguired", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
			return;
		}

		try {
			List<DataSetRegistrationResult> result = this.dataSetRegistrationService.register( this.processes, this.registry );
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put( Consts.REGISTRATION_RESULT, result );
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put( Consts.SELECTED_DATASETS, this.processes );
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put( Consts.SELECTED_REGISTRY, this.registry );
			this.registry = null;
			this.registeredProcesses.clear();
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
		catch ( NodeIllegalStatusException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "nodeIllegalStatusException_errorMessage", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}

		FacesUtils.redirectToPage( "datasetRegistrationSummary.xhtml" );
	}

}
