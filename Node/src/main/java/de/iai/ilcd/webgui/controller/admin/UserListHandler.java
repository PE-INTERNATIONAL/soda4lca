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

import org.apache.commons.lang.ObjectUtils;
import org.primefaces.model.SortOrder;

import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.util.SodaUtil;

/**
 * Handler for the user list in admin area
 */
@ViewScoped
@ManagedBean( name = "userListHandler" )
public class UserListHandler extends AbstractAdminOrgDependentListHandler<User, UserDao> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 1884241468855139691L;

	/**
	 * Create the handler
	 */
	public UserListHandler() {
		super( new UserDao() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSelected() {
		final User[] selectedItems = this.getSelectedItems();
		if ( selectedItems == null ) {
			return;
		}

		for ( User item : selectedItems ) {
			// Admin user not deletable
			if ( ObjectUtils.equals( item.getId(), SodaUtil.ADMIN_ID ) ) {
				continue;	// not selectable in facelet and not deletable (double check)
			}
			try {
				this.getDao().remove( item );
				this.addI18NFacesMessage( "facesMsg.removeSuccess", FacesMessage.SEVERITY_INFO, item.getUserName() );
			}
			catch ( Exception ex ) {
				this.addI18NFacesMessage( "facesMsg.removeError", FacesMessage.SEVERITY_ERROR, item.getUserName() );
			}
		}

		this.clearSelection();
		this.reloadCount();
	}

	/**
	 * Set the selected users.
	 * <p>
	 * <b>Do not replace</b> by {@link AbstractAdminListHandler#setSelectedItems(Object[]) setSelectedItems} in Facelets
	 * (see it's documentation for the reason)
	 * </p>
	 * 
	 * @param selected
	 *            selected users
	 */
	public void setSelectedUsers( User[] selected ) {
		super.setSelectedItems( selected );
	}

	/**
	 * Get the selected users.
	 * <p>
	 * <b>Do not replace</b> by {@link AbstractAdminListHandler#getSelectedItems() getSelectedItems} in Facelets (see
	 * it's documentation for the reason)
	 * </p>
	 * 
	 * @return selected users
	 */
	public User[] getSelectedUsers() {
		return super.getSelectedItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long loadElementCount( Organization o ) {
		return this.getDao().getUsersCount( o );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<User> lazyLoad( Organization o, int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
		return this.getDao().getUsers( o, first, pageSize );
	}

}
