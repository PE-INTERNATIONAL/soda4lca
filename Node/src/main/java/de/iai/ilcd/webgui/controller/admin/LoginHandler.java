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

import java.net.URLDecoder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.security.IlcdSecurityRealm;
import de.iai.ilcd.security.ProtectableType;
import de.iai.ilcd.security.UserLoginActions;
import de.iai.ilcd.webgui.controller.AbstractHandler;
import de.iai.ilcd.webgui.controller.ConfigurationBean;
import de.iai.ilcd.webgui.controller.ui.AvailableStockHandler;

/**
 * Handler for login and logout actions
 */
@ManagedBean
@SessionScoped
public class LoginHandler extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -1378066230750034948L;

	/**
	 * Source view to redirect
	 */
	private String source;

	/**
	 * Configuration (injected)
	 */
	@ManagedProperty( value = "#{conf}" )
	private ConfigurationBean conf;

	/**
	 * Current input for login name
	 */
	private String loginName;

	/**
	 * Current input for password
	 */
	private String password;

	/**
	 * Current user logged in flag
	 */
	boolean loggedIn = false;

	/**
	 * DAO for user objects
	 */
	private final UserDao userDao = new UserDao();

	/**
	 * Logger instance
	 */
	private static Logger logger = LoggerFactory.getLogger( LoginHandler.class );

	/**
	 * Handler with available stocks
	 */
	@ManagedProperty( value = "#{availableStocks}" )
	private AvailableStockHandler availableStocks;

	/**
	 * Creates a new instance of LoginHandler
	 */
	public LoginHandler() {
	}

	/**
	 * Get the current login name
	 * 
	 * @return current login name
	 */
	public String getLoginName() {
		Subject currentUser = SecurityUtils.getSubject();
		if ( currentUser.isAuthenticated() ) {
			return (String) currentUser.getPrincipal();
		}
		else {
			return this.loginName;
		}
	}

	/**
	 * Set the login name from user input
	 * 
	 * @param loginName
	 *            current input value
	 */
	public void setLoginName( String loginName ) {
		this.loginName = loginName;
	}

	/**
	 * Get the current password
	 * 
	 * @return current password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Set the password from user input
	 * 
	 * @param password
	 *            current input value
	 */
	public void setPassword( String password ) {
		this.password = password;
	}

	/**
	 * Get the source view to redirect to
	 * 
	 * @return path of the source view to redirect to
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Set the source view to redirect to
	 * 
	 * @param source
	 *            path of the source view to redirect to
	 */
	public void setSource( String source ) {
		try {
			this.source = URLDecoder.decode( source, "UTF-8" );
		}
		catch ( Exception e ) {
			this.source = source;
		}
	}

	/**
	 * Set the configuration bean (via dependency injection)
	 * 
	 * @param conf
	 *            configuration to set
	 */
	public void setConf( ConfigurationBean conf ) {
		this.conf = conf;
	}

	/**
	 * Set / inject available stocks handler
	 * 
	 * @param availableStocks
	 *            handler to set / inject
	 */
	public void setAvailableStocks( AvailableStockHandler availableStocks ) {
		this.availableStocks = availableStocks;
	}

	/**
	 * Do login by user provided credentials
	 */
	public void login() {
		// we use the Shiro framework here

		Subject currentUser = SecurityUtils.getSubject();
		if ( currentUser.isAuthenticated() ) {
			this.addI18NFacesMessage( "facesMsg.login.alreadyLoggedIn", FacesMessage.SEVERITY_ERROR );
			return;
		}

		// no user currently logged in
		if ( this.loginName == null || this.password == null ) {
			this.addI18NFacesMessage( "facesMsg.login.invalidCredentials", FacesMessage.SEVERITY_ERROR );
			return;
		}

		String salt = this.userDao.getSalt( this.loginName );
		if ( salt == null ) {
			this.addI18NFacesMessage( "facesMsg.login.incorrectCredentials", FacesMessage.SEVERITY_ERROR );
			return;
		}

		try {
			new UserLoginActions().login( this.loginName, IlcdSecurityRealm.getEncryptedPassword( this.password, salt ) );

			this.availableStocks.reloadAllDataStocks();
			// redirect to source view specified by src param (except login.xhtml) else to (admin/)index.xhtml
			final String indexXhtmlPath = (currentUser.isPermitted( ProtectableType.ADMIN_AREA.name() ) ? "/admin" : "") + "/index.xhtml";
			FacesContext.getCurrentInstance().getExternalContext().redirect(
					this.conf.getContextPath() + (!StringUtils.isBlank( this.source ) && !this.source.equals( "/login.xhtml" ) ? this.source : indexXhtmlPath) );
		}
		catch ( UnknownAccountException uae ) {
			this.addI18NFacesMessage( "facesMsg.login.incorrectCredentials", FacesMessage.SEVERITY_ERROR );
		}
		catch ( IncorrectCredentialsException ice ) {
			this.addI18NFacesMessage( "facesMsg.login.incorrectCredentials", FacesMessage.SEVERITY_ERROR );
		}
		catch ( LockedAccountException lae ) {
			this.addI18NFacesMessage( "facesMsg.accountLocked", FacesMessage.SEVERITY_ERROR );
		}
		catch ( AuthenticationException au ) {
			LoginHandler.logger.info( au.getMessage() );
			this.addI18NFacesMessage( "facesMsg.login.incorrectCredentials", FacesMessage.SEVERITY_ERROR );
		}
		catch ( Exception e ) {
			this.addI18NFacesMessage( "facesMsg.error", e );
		}

	}

	/**
	 * Logout the current user
	 */
	public void logout() {
		new UserLoginActions().logout();
		this.availableStocks.reloadAllDataStocks();
		this.addI18NFacesMessage( "facesMsg.login.logoutSuccess", FacesMessage.SEVERITY_INFO, this.loginName );
	}

	/**
	 * Determine if a user is currently logged in. Delegates to {@link UserLoginActions#isLoggedIn()}.
	 * 
	 * @return <code>true</code> if user is logged in, else <code>false</code>
	 */
	public boolean isLoggedIn() {
		return new UserLoginActions().isLoggedIn();
	}
}
