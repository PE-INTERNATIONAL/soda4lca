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

import de.fzk.iai.ilcd.service.model.IFlowVO;
import de.fzk.iai.ilcd.service.model.enums.TypeOfFlowValue;

import javax.faces.bean.ManagedBean;

import de.iai.ilcd.model.dao.FlowDao;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.process.ExchangeDirection;

/**
 * Backing bean for flow detail view
 */
@ManagedBean
public class FlowHandler extends AbstractDataSetHandler<IFlowVO, Flow, FlowDao> {

	/**
	 * Serialization Id
	 */
	private static final long serialVersionUID = 5926993778095928321L;

	/**
	 * Flag to determine if processes with this flow as input flow exist
	 */
	private boolean processesWithInputFlowExist;

	/**
	 * Flag to determine if processes with this flow as output flow exist
	 */
	private boolean processesWithOutputFlowExist;

	/**
	 * Flag to determine if processes with this flow as in- or output flow exist
	 */
	private boolean processesWithInOrOutputFlowExist;

	/**
	 * Flag to determine if processes with this flow as in- <strong>x</strong>or output flow exist
	 */
	private boolean processesWithInXorOutputFlowExist;

	/**
	 * Initialize handler
	 */
	public FlowHandler() {
		super( new FlowDao(), "flows" );
	}

	/**
	 * Convenience method, delegates to {@link #getDataSet()}
	 * 
	 * @return represented flow
	 */
	public IFlowVO getFlow() {
		return this.getDataSet();
	}

	/**
	 * Determine if elementary flow
	 * 
	 * @return <code>true</code> if represented flow is elementary flow, else <code>false</code>
	 */
	public boolean isElementaryFlow() {
		if ( this.getFlow() != null ) {
			return TypeOfFlowValue.ELEMENTARY_FLOW.equals( this.getFlow().getType() );
		}
		else {
			return false;
		}
	}

	/**
	 * Set the input / output flow exist boolean properties
	 */
	@Override
	protected void datasetLoaded( Flow dataset ) {
		// and set the booleans
		if ( dataset != null ) {
			final ProcessDao d = new ProcessDao();
			this.processesWithInputFlowExist = d.getProcessesForExchangeFlowCount( dataset.getUuidAsString(), ExchangeDirection.INPUT ) > 0;
			this.processesWithOutputFlowExist = d.getProcessesForExchangeFlowCount( dataset.getUuidAsString(), ExchangeDirection.OUTPUT ) > 0;
		}
		else {
			this.processesWithInputFlowExist = false;
			this.processesWithOutputFlowExist = false;
		}
		this.processesWithInOrOutputFlowExist = this.processesWithInputFlowExist || this.processesWithOutputFlowExist;
		this.processesWithInXorOutputFlowExist = this.processesWithInputFlowExist ^ this.processesWithOutputFlowExist;
	}

	/**
	 * Determine if processes with this flow as in- or output flow exist
	 * 
	 * @return <code>true</code> if processes with this flow as in. or output flow exist, else <code>false</code>
	 */
	public boolean isProcessesWithInOrOutputFlowExist() {
		return this.processesWithInOrOutputFlowExist;
	}

	/**
	 * Determine if processes with this flow as input flow exist
	 * 
	 * @return <code>true</code> if processes with this flow as input flow exist, else <code>false</code>
	 */
	public boolean isProcessesWithInputFlowExist() {
		return this.processesWithInputFlowExist;
	}

	/**
	 * Determine if processes with this flow as output flow exist
	 * 
	 * @return <code>true</code> if processes with this flow as output flow exist, else <code>false</code>
	 */
	public boolean isProcessesWithOutputFlowExist() {
		return this.processesWithOutputFlowExist;
	}

	/**
	 * Determine if processes with this flow as in- <strong>x</strong>or output flow exist
	 * 
	 * @return <code>true</code> if processes with this flow as in- <strong>x</strong>or output flow exist, else
	 *         <code>false</code>
	 */
	public boolean isProcessesWithInXorOutputFlowExist() {
		return this.processesWithInXorOutputFlowExist;
	}
}
