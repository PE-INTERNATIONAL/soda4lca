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

package de.iai.ilcd.webgui.controller.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.velocity.tools.generic.ValueParser;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import de.iai.ilcd.model.common.DataSetState;
import de.iai.ilcd.model.dao.MergeException;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.services.DataSetDeregistrationService;
import de.iai.ilcd.services.DataSetRegistrationService;
import de.iai.ilcd.services.RegistryService;
import de.iai.ilcd.webgui.util.Consts;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;

/**
 * 
 * @author clemens.duepmeier
 */
@ManagedBean( )
@ViewScoped
public class ProcessListHandler extends AbstractDataSetListHandler<Process> {

	private final DataSetDeregistrationService dataSetDeregistrationService;

	private final DataSetRegistrationService dataSetRegistrationService;

	private String registry;

	private String reason;

	private final RegistryService registryService;

	private List<Process> registered;

	private final Map<String, Object> paramsMap = new HashMap<String, Object>();

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -8347365494158300406L;

	public ProcessListHandler() {
		super( Process.class, new ProcessDao() );
		WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext( FacesContext.getCurrentInstance() );
		this.dataSetDeregistrationService = ctx.getBean( DataSetDeregistrationService.class );
		this.dataSetRegistrationService = ctx.getBean( DataSetRegistrationService.class );
		this.registryService = ctx.getBean( RegistryService.class );

	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @return selected items
	 * @see #getSelectedItems()
	 * @deprecated use {@link #getSelectedItems()}
	 */
	@Deprecated
	public Process[] getSelectedProcesses() {
		return this.getSelectedItems();
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @param selItems
	 *            selected items
	 * @see #setSelectedItems(Process[])
	 * @deprecated use {@link #setSelectedItems(Process[])}
	 */
	@Deprecated
	public void setSelectedProcesses( Process[] selItems ) {
		this.setSelectedItems( selItems );
	}

	public void releaseSelected() {
		this.changeReleaseState( DataSetState.RELEASED, false );
	}

	public void unreleaseSelected() {
		this.changeReleaseState( DataSetState.UNRELEASED, false );
	}

	public void unlockSelected() {
		this.changeReleaseState( DataSetState.UNRELEASED, true );
	}

	private void changeReleaseState( DataSetState state, boolean forceUnlock ) {
		final Process[] selItems = this.getSelectedItems();
		if ( selItems == null ) {
			return;
		}

		for ( Process process : selItems ) {
			try {
				if ( process.getReleaseState().equals( state ) ) {
					continue;
				}
				if ( process.getReleaseState().equals( DataSetState.LOCKED ) && !forceUnlock ) {
					continue; // Locked data sets will only change state they should be explicitly unlocked
				}
				process.setReleaseState( state );
				this.getDao().merge( process );
				this.addI18NFacesMessage( "facesMsg.proc.changeStateSuccess", FacesMessage.SEVERITY_INFO, this.getDisplayString( process ) );
			}
			catch ( MergeException e ) {
				this.addI18NFacesMessage( "facesMsg.proc.changeStateError", FacesMessage.SEVERITY_ERROR, this.getDisplayString( process ) );
			}
		}

		this.setSelectedItems( null );

		super.reloadCount();
		// DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
		// .findComponent("tableForm:processTable");
		// int page = dataTable.getPage();
		// if (dataTable != null) {
		// dataTable.loadLazyData();
		// }

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDisplayString( Process obj ) {
		try {
			return obj.getName().getValue();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	public void registerSelected() {

		if ( !this.selectionValid() ) {
			return;
		}

		FacesContext.getCurrentInstance().getExternalContext().getFlash().put( Consts.SELECTED_DATASETS, Arrays.asList( this.getSelectedItems() ) );
		FacesUtils.redirectToPage( "/admin/datasets/registerDatasets" );
	}

	public String getRegistry() {
		return this.registry;
	}

	public void setRegistry( String registry ) {
		this.registry = registry;
		this.paramsMap.put( "registeredIn", registry );
		// TODO: Oliver: not sure if this is the way to go, but it works
		this.params = new ValueParser( this.paramsMap );
		ProcessDao dao = new ProcessDao();
		// TODO: determine data stock
		int rowCount = (int) dao.searchResultCount( new ValueParser( this.paramsMap ), false, this.getStockSelection().getCurrentStockAsArray() );
		this.getLazyModel().setRowCount( rowCount );
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason( String reason ) {
		this.reason = reason;
	}

	public void deregisterSelected( ActionEvent event ) {
		if ( !this.selectionValid() ) {
			return;
		}

		try {
			this.dataSetDeregistrationService.deregisterDatasets( Arrays.asList( this.getSelectedItems() ), this.reason, this.registryService.findByUUID(
					this.registry ).getId() );
			this.setSelectedItems( null );
			this.reason = null;
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
		FacesUtils.redirectToPage( "/admin/showProcesses" );
	}

	private boolean selectionValid() {
		if ( this.getSelectedItems() == null || this.getSelectedItems().length == 0 ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "admin.deregisterDataSets.noDatasetsSelected", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			FacesContext.getCurrentInstance().addMessage( null, message );
			return false;
		}
		return true;
	}

	public List<Process> getRegistered() {
		return this.registered;
	}

}
