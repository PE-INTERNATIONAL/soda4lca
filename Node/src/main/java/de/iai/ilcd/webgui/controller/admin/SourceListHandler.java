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

import de.iai.ilcd.model.dao.SourceDao;
import de.iai.ilcd.model.source.Source;

/**
 * Admin handler for source lists
 */
@ManagedBean
@ViewScoped
public class SourceListHandler extends AbstractDataSetListHandler<Source> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 4869198441468232669L;

	/**
	 * Create the source list handler
	 */
	public SourceListHandler() {
		super( Source.class, new SourceDao() );
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @return selected items
	 * @see #getSelectedItems()
	 * @deprecated use {@link #getSelectedItems()}
	 */
	@Deprecated
	public Source[] getSelectedSources() {
		return this.getSelectedItems();
	}

	/**
	 * Legacy method for selected item access
	 * 
	 * @param selItems
	 *            selected items
	 * @see #setSelectedItems(Source[])
	 * @deprecated use {@link #setSelectedItems(Source[])}
	 */
	@Deprecated
	public void setSelectedSources( Source[] selItems ) {
		this.setSelectedItems( selItems );
	}

}
