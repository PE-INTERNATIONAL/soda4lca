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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.iai.ilcd.model.dao.FlowDao;
import de.iai.ilcd.model.flow.Flow;

/**
 * Admin elementary flow list handler TODO check difference to {@link ProductFlowListHandler}
 */
@ManagedBean
@ViewScoped
public class ElementaryFlowListHandler extends AbstractDataSetListHandler<Flow> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -1386485091199071001L;

	/**
	 * Create the contact list handler
	 */
	public ElementaryFlowListHandler() {
		super( Flow.class, new FlowDao() );
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @return selected items
	 * @see #getSelectedItems()
	 * @deprecated use {@link #getSelectedItems()}
	 */
	@Deprecated
	public Flow[] getSelectedFlows() {
		return this.getSelectedItems();
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @param selItems
	 *            selected items
	 * @see #setSelectedItems(Flow[])
	 * @deprecated use {@link #setSelectedItems(Flow[])}
	 */
	@Deprecated
	public void setSelectedFlows( Flow[] selItems ) {
		this.setSelectedItems( selItems );
	}

}
