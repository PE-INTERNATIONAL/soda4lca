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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

/**
 * 
 * @author clemens.duepmeier
 */
public class UserLoginActions {

	public void login( String userName, String password ) {
		Subject currentUser = SecurityUtils.getSubject();
		// if (currentUser.isAuthenticated())
		// return;
		UsernamePasswordToken token = new UsernamePasswordToken( userName, password );
		currentUser.login( token );
	}

	public void logout() {
		Subject currentUser = SecurityUtils.getSubject();
		// IlcdSecurityRealm realm= new IlcdSecurityRealm();
		// realm.clearAuthorizationInfo(currentUser.getPrincipals());
		if ( currentUser.isAuthenticated() ) {
			currentUser.logout();
		}
	}

	public boolean isLoggedIn() {
		Subject currentUser = SecurityUtils.getSubject();
		if ( currentUser.isAuthenticated() ) {
			return true;
		}
		else {
			return false;
		}
	}

}
