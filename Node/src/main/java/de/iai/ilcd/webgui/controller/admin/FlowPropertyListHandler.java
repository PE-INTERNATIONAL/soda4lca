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

import de.iai.ilcd.model.dao.FlowPropertyDao;
import de.iai.ilcd.model.flowproperty.FlowProperty;

/**
 * Admin flow property list handler
 */
@ManagedBean
@ViewScoped
public class FlowPropertyListHandler extends AbstractDataSetListHandler<FlowProperty> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -8581509638490266475L;

	/**
	 * Create the flow property list handler
	 */
	public FlowPropertyListHandler() {
		super( FlowProperty.class, new FlowPropertyDao() );
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @return selected items
	 * @see #getSelectedItems()
	 * @deprecated use {@link #getSelectedItems()}
	 */
	@Deprecated
	public FlowProperty[] getSelectedFlowProperties() {
		return this.getSelectedItems();
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @param selItems
	 *            selected items
	 * @see #setSelectedItems(FlowProperty[])
	 * @deprecated use {@link #setSelectedItems(FlowProperty[])}
	 */
	@Deprecated
	public void setSelectedFlowProperties( FlowProperty[] selItems ) {
		this.setSelectedItems( selItems );
	}

}
