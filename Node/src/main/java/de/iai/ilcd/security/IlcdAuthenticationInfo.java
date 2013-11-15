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

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.security.User;

public class IlcdAuthenticationInfo implements AuthenticationInfo {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 591932215980524142L;

	/**
	 * Token for authentication
	 */
	private final AuthenticationToken authToken;

	/**
	 * DAO for the user objects
	 */
	private final UserDao userDao = new UserDao();

	/**
	 * Create the authentication info
	 * 
	 * @param at
	 *            token for the info
	 */
	public IlcdAuthenticationInfo( AuthenticationToken at ) {
		this.authToken = at;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrincipalCollection getPrincipals() {
		PrincipalCollection principles = new SimplePrincipalCollection( getUser().getUserName(), IlcdSecurityRealm.REALM_NAME );
		return principles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getCredentials() {
		return getUser().getPasswordHash();
	}

	/**
	 * Get the user for the current authentication token
	 * 
	 * @return user for the current authentication token (or <code>null</code> if none found)
	 */
	private User getUser() {
		if ( this.authToken == null ) {
			return null;
		}
		try {
			return this.userDao.getUser( (String) this.authToken.getPrincipal() );
		}
		catch ( Exception e ) {
			// any error: no user
			return null;
		}
	}

}
