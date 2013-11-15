/*******************************************************************************
 * Copyright (c) 2011 Karlsruhe Institute of Technology (KIT) - Institute for
 * Applied Computer Science (IAI).
 * 
 * This file is part of soda4LCA - the Service-Oriented Life Cycle Data Store.
 * 
 * soda4LCA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * soda4LCA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with soda4LCA. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.iai.ilcd.webgui.controller.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import de.fzk.iai.ilcd.service.model.IProcessVO;
import de.fzk.iai.ilcd.service.model.enums.LCIMethodApproachesValue;
import de.fzk.iai.ilcd.service.model.enums.TypeOfFlowValue;
import de.fzk.iai.ilcd.service.model.process.IComplianceSystem;
import de.iai.ilcd.delegate.DataSetRestServiceBD;
import de.iai.ilcd.delegate.ValidationResult;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.process.Exchange;
import de.iai.ilcd.model.process.ExchangeDirection;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.security.UserAccessBean;
import de.iai.ilcd.services.DataSetDeregistrationService;
import de.iai.ilcd.services.DataSetRegistrationService;
import de.iai.ilcd.services.RegistryService;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

/**
 * Backing bean for process detail view
 */
@ManagedBean
@ViewScoped
public class ProcessHandler extends AbstractDataSetHandler<IProcessVO, Process, ProcessDao> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 6907352892423743765L;

	/**
	 * List with input product flows
	 */
	private List<Flow> inputProducts;

	/**
	 * List with co product flows
	 */
	private List<Flow> coProducts;

	private DataSetRegistrationService dataSetRegistrationService;

	private DataSetDeregistrationService dataSetDeregistrationService;

	private String reason;

	private DataSetRegistrationData selectedDataSetRegistrationData;

	private RegistryService registryService;

	/**
	 * Initialize the handler
	 */
	public ProcessHandler() {
		super( new ProcessDao(), "processes" );
		WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext( FacesContext.getCurrentInstance() );
		dataSetRegistrationService = ctx.getBean( DataSetRegistrationService.class );
		dataSetDeregistrationService = ctx.getBean( DataSetDeregistrationService.class );
		registryService = ctx.getBean( RegistryService.class );
	}

	/**
	 * Convenience method, delegates to {@link #getDataSet()}
	 * 
	 * @return process
	 */
	public IProcessVO getProcess() {
		return this.getDataSet();
	}

	/**
	 * We need this as workaround because the Service API delivers a set but we need a List in the template
	 * 
	 * @return
	 */
	public List<LCIMethodApproachesValue> getApproaches() {
		List<LCIMethodApproachesValue> approaches = new ArrayList<LCIMethodApproachesValue>();

		if ( this.getDataSet() != null ) {
			approaches = new ArrayList<LCIMethodApproachesValue>( this.getDataSet().getLCIMethodInformation().getApproaches() );
		}

		return approaches;
	}

	/**
	 * We need this too as workaround to geth the ComplianceSystems as List
	 * 
	 * @return
	 */
	public List<IComplianceSystem> getComplianceSystems() {
		List<IComplianceSystem> complianceSystems = new ArrayList<IComplianceSystem>();

		if ( this.getDataSet() != null ) {
			complianceSystems = new ArrayList<IComplianceSystem>( this.getDataSet().getComplianceSystems() );
		}

		return complianceSystems;
	}

	/**
	 * Get the input product flows
	 * 
	 * @return input product flows
	 */
	public List<Flow> getInputProducts() {
		return this.inputProducts;
	}

	/**
	 * Get the co-product flows
	 * 
	 * @return co-product flows
	 */
	public List<Flow> getCoProducts() {
		return this.coProducts;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void datasetLoaded( Process p ) {
		// Load input products
		List<Exchange> tmp = p.getExchanges( ExchangeDirection.INPUT.name() );
		this.inputProducts = new ArrayList<Flow>();
		for ( Exchange e : tmp ) {
			if ( e.getFlow() != null && TypeOfFlowValue.PRODUCT_FLOW.equals( e.getFlow().getType() ) ) {
				this.inputProducts.add( e.getFlow() );
			}
		}

		// Load co-products
		tmp = p.getExchanges( ExchangeDirection.OUTPUT.name() );
		this.coProducts = new ArrayList<Flow>();
		// named loop (due to continue from inner loop)
		coProdLoop: for ( Exchange e : tmp ) {
			if ( e.getFlow() != null && TypeOfFlowValue.PRODUCT_FLOW.equals( e.getFlow().getType() ) ) {
				// check if this exchange flow is contained in the reference exchange list
				Long id = e.getFlow().getId();
				for ( Exchange refEx : p.getReferenceExchanges() ) {
					if ( refEx.getFlow() != null && id.equals( refEx.getFlow().getId() ) ) {
						// trigger next loop from !! outer !! loop
						continue coProdLoop;
					}
				}
				// flow was not in reference exchange list
				this.coProducts.add( e.getFlow() );
			}
		}
	}

	public String deregisterSelected() {
		UserAccessBean user = new UserAccessBean();
		try {
			if ( user.hasAdminAreaAccessRight() ) {
				dataSetDeregistrationService.deregisterDatasets( Collections.singletonList( selectedDataSetRegistrationData ), reason,
						selectedDataSetRegistrationData.getRegistry() );
			}
		}
		catch ( RestWSUnknownException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "admin.deregisterDataSets.restWSUnknownException", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
		catch ( AuthenticationException e ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "authenticationException_errorMessage", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
		}
		return null;
	}

	public List<DataSetRegistrationData> getRegistrations() {
		return dataSetRegistrationService.getListOfRegistrations( (Process) this.getDataSet() );
	}

	public DataSetRegistrationData getSelectedDataSetRegistrationData() {
		return selectedDataSetRegistrationData;
	}

	public void setSelectedDataSetRegistrationData( DataSetRegistrationData selectedDataSetRegistrationData ) {
		this.selectedDataSetRegistrationData = selectedDataSetRegistrationData;
	}

	public String getReason() {
		return reason;
	}

	public void setReason( String reason ) {
		this.reason = reason;
	}

	public ValidationResult getDatasetValidationResult() {
		if ( getRegistryUUID() != null && !getRegistryUUID().trim().equals( "" ) ) {
			Registry reg = registryService.findByUUID( getRegistryUUID() );
			DataSet ds = getDataSetFromProcess();
			return DataSetRestServiceBD.getInstance( reg ).verify( ds );
		}
		else {
			return ValidationResult.CANT_VALIDATE_NOT_REGISTERED;
		}
	}

	private DataSet getDataSetFromProcess() {
		Process p = (Process) this.getDataSet();
		DataSet ds = new DataSet();
		ds.setUuid( p.getUuid().getUuid() );
		ds.setVersion( p.getVersion().getVersionString() );
		ds.setHash( p.getXmlFile().getContentHash() );
		return ds;
	}
}
