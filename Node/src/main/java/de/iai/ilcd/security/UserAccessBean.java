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

package de.iai.ilcd.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.webgui.controller.AbstractHandler;

/**
 * User access bean
 */
@ManagedBean( name = "user" )
@ViewScoped
public class UserAccessBean extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 556220221006512832L;

	/**
	 * User object
	 */
	private User user = null;

	/**
	 * Determine if user is logged in
	 * 
	 * @return <code>true</code> if user logged in, else <code>false</code>
	 */
	public boolean isLoggedIn() {
		Subject currentUser = SecurityUtils.getSubject();
		if ( currentUser == null ) {
			return false;
		}

		if ( currentUser.isAuthenticated() ) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Determine if user shall be able to enter admin area
	 * 
	 * @return <code>true</code> if access shall be granted, <code>false</code> otherwise
	 * @see SecurityUtil#hasAdminAreaAccessRight()
	 */
	public boolean hasAdminAreaAccessRight() {
		return SecurityUtil.hasAdminAreaAccessRight();
	}

	/**
	 * Determine if user is super admin
	 * 
	 * @return <code>true</code> if super admin access shall be granted, <code>false</code> otherwise
	 * @see SecurityUtil#hasSuperAdminPermission()
	 */
	public boolean hasSuperAdminPermission() {
		return SecurityUtil.hasSuperAdminPermission();
	}

	/**
	 * Determine if the current user has full view (means {@link ProtectionType#EXPORT}) right
	 * 
	 * @param ds
	 *            data set to check for
	 * @return <code>true</code> if full view right is present, <code>false</code> otherwise
	 */
	public boolean hasFullViewRights( DataSet ds ) {
		return SecurityUtil.hasExportPermission( ds );
	}

	/**
	 * Get the user name of the current user
	 * 
	 * @return user name of the current user
	 */
	public String getUserName() {
		Subject currentUser = SecurityUtils.getSubject();
		if ( currentUser == null ) {
			return null;
		}
		else {
			return (String) currentUser.getPrincipal();
		}
	}

	/**
	 * Get the user object for the current user.
	 * 
	 * @return user object
	 */
	public User getUserObject() {
		if ( this.user == null ) {
			String userName = this.getUserName();
			if ( userName != null ) {
				UserDao uDao = new UserDao();
				User u = uDao.getUser( userName );
				if ( u != null ) {
					this.user = u;
				}
			}
		}
		return this.user;

	}

}
