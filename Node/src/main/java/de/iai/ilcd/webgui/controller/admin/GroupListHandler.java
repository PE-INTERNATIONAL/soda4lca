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

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import de.iai.ilcd.model.dao.UserGroupDao;
import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.UserGroup;

/**
 * Admin handler for group list
 */
@ViewScoped
@ManagedBean
public class GroupListHandler extends AbstractAdminOrgDependentListHandler<UserGroup, UserGroupDao> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 1292889465701056930L;

	/**
	 * Create the handler
	 */
	public GroupListHandler() {
		super( new UserGroupDao() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSelected() {
		final UserGroup[] selectedItems = this.getSelectedItems();
		if ( selectedItems == null ) {
			return;
		}

		for ( UserGroup item : selectedItems ) {
			try {
				this.getDao().remove( item );
				this.addI18NFacesMessage( "facesMsg.removeSuccess", FacesMessage.SEVERITY_INFO, item.getGroupName() );
			}
			catch ( Exception ex ) {
				this.addI18NFacesMessage( "facesMsg.removeError", FacesMessage.SEVERITY_ERROR, item.getGroupName() );
			}
		}

		this.clearSelection();
		this.reloadCount();
	}

	/**
	 * Set the selected groups.
	 * <p>
	 * <b>Do not replace</b> by {@link AbstractAdminListHandler#setSelectedItems(Object[]) setSelectedItems} in Facelets
	 * (see it's documentation for the reason)
	 * </p>
	 * 
	 * @param selected
	 *            selected groups
	 */
	public void setSelectedGroups( UserGroup[] selected ) {
		super.setSelectedItems( selected );
	}

	/**
	 * Get the selected groups.
	 * <p>
	 * <b>Do not replace</b> by {@link AbstractAdminListHandler#getSelectedItems() getSelectedItems} in Facelets (see
	 * it's documentation for the reason)
	 * </p>
	 * 
	 * @return selected groups
	 */
	public UserGroup[] getSelectedGroups() {
		return super.getSelectedItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long loadElementCount( Organization o ) {
		return this.getDao().getGroupsCount( o );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<UserGroup> lazyLoad( Organization o, int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
		return this.getDao().getGroups( o, first, pageSize );
	}

}
