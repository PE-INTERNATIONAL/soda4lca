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

import de.iai.ilcd.model.dao.UnitGroupDao;
import de.iai.ilcd.model.unitgroup.UnitGroup;

/**
 * Admin handler for unit group lists
 */
@ManagedBean
@ViewScoped
public class UnitGroupListHandler extends AbstractDataSetListHandler<UnitGroup> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -7573123404473521778L;

	/**
	 * Create the unit group handler
	 */
	public UnitGroupListHandler() {
		super( UnitGroup.class, new UnitGroupDao() );
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @return selected items
	 * @see #getSelectedItems()
	 * @deprecated use {@link #getSelectedItems()}
	 */
	@Deprecated
	public UnitGroup[] getSelectedUnitGroups() {
		return this.getSelectedItems();
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @param selItems
	 *            selected items
	 * @see #setSelectedItems(UnitGroup[])
	 * @deprecated use {@link #setSelectedItems(UnitGroup[])}
	 */
	@Deprecated
	public void setSelectedUnitGroups( UnitGroup[] selItems ) {
		this.setSelectedItems( selItems );
	}

}
