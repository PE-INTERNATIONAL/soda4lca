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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.configuration.Configuration;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.dao.MergeException;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.security.UserLoginActions;
import de.iai.ilcd.webgui.controller.AbstractHandler;

/**
 * Handler for activation
 */
@ManagedBean
@RequestScoped
public class ActivationHandler extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 4817228315945265354L;

	/**
	 * Current user name
	 */
	private String userName = null;

	/**
	 * Current activation key
	 */
	private String activationKey = null;

	/**
	 * DAO for the access of users
	 */
	private final UserDao userDao = new UserDao();

	/**
	 * Current user object
	 */
	private User user = null;

	/**
	 * Create the handler
	 */
	public ActivationHandler() {
		// check if the page was called with a request parameter specifying the current user and the activation key
		this.userName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( "user" );
		this.activationKey = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( "activationKey" );

		if ( this.userName == null ) {
			this.addI18NFacesMessage( "facesMsg.activation.invalidUser", FacesMessage.SEVERITY_ERROR );
			return;
		}
		if ( this.activationKey == null ) {
			this.addI18NFacesMessage( "facesMsg.activation.invalidKey1", FacesMessage.SEVERITY_ERROR );
			return;
		}
		this.user = this.userDao.getUser( this.userName );
		if ( this.user == null ) {
			this.addI18NFacesMessage( "facesMsg.activation.noSuchUser", FacesMessage.SEVERITY_ERROR, this.userName );
			return;
		}
		if ( this.user.getRegistrationKey() == null ) {
			this.addI18NFacesMessage( "facesMsg.activation.alreadyActivated", FacesMessage.SEVERITY_ERROR, this.userName );
			return;
		}
		if ( !this.user.getRegistrationKey().equals( this.activationKey ) ) {
			this.addI18NFacesMessage( "facesMsg.activation.invalidKey2", FacesMessage.SEVERITY_ERROR );
			return;
		}
		// clear registration key to activate user
		this.user.setRegistrationKey( null );
		try {
			this.user = this.userDao.merge( this.user );
		}
		catch ( MergeException ex ) {
			this.addI18NFacesMessage( "facesMsg.activation.stateError", FacesMessage.SEVERITY_ERROR );
			return;
		}

		Configuration properties = ConfigurationService.INSTANCE.getProperties();
		boolean selfActivation = properties.getBoolean( "user.registration.selfActivation", Boolean.FALSE );

		if ( selfActivation ) {
			UserLoginActions loginActions = new UserLoginActions();

			try {
				loginActions.login( this.user.getUserName(), this.user.getPasswordHash() );
			}
			catch ( UnknownAccountException uae ) {
				this.addI18NFacesMessage( "facesMsg.activation.accountUnknown", FacesMessage.SEVERITY_ERROR );
				return;
			}
			catch ( IncorrectCredentialsException ice ) {
				this.addI18NFacesMessage( "facesMsg.activation.incorrectCredentials.pw", FacesMessage.SEVERITY_ERROR );
				return;
			}
			catch ( LockedAccountException lae ) {
				this.addI18NFacesMessage( "facesMsg.accountLocked", FacesMessage.SEVERITY_ERROR );
				return;
			}
			catch ( Exception e ) {
				this.addI18NFacesMessage( "facesMsg.error", e );
				return;
			}

			this.addI18NFacesMessage( "facesMsg.activation.accountActivatedLoggedIn", FacesMessage.SEVERITY_INFO );
		}
		else {
			this.addI18NFacesMessage( "facesMsg.activation.accountActivated", FacesMessage.SEVERITY_INFO );
		}

	}

	/**
	 * Get the current activation key
	 * 
	 * @return current activation key
	 */
	public String getActivationKey() {
		return this.activationKey;
	}

	/**
	 * Get the user name of the current user
	 * 
	 * @return user name of the current user
	 */
	public String getUserName() {
		return this.userName;
	}
}
