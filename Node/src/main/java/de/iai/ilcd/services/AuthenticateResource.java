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

package de.iai.ilcd.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.fzk.iai.ilcd.service.client.impl.vo.AuthenticationInfo;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.security.IlcdSecurityRealm;
import de.iai.ilcd.security.UserAccessBean;

/**
 * REST Web Service
 * 
 * @author clemens.duepmeier
 */

@Component
@Path( "authenticate" )
public class AuthenticateResource {

	private static final Logger logger = LoggerFactory.getLogger( AuthenticateResource.class );

	@SuppressWarnings( "unused" )
	@Context
	private UriInfo context;

	/** Creates a new instance of AuthenticateResource */
	public AuthenticateResource() {
	}

	@GET
	@Path( "status" )
	@Produces( "application/xml" )
	public AuthenticationInfo status() {
		logger.info( "authenticate/status" );

		UserAccessBean user = new UserAccessBean();

		AuthenticationInfo authInfo = new AuthenticationInfo();

		boolean authenticated = user.isLoggedIn();

		authInfo.setAuthenticated( authenticated );
		logger.info( "user authenticated: " + authenticated );

		if ( !authenticated ) {
			return authInfo;
		}

		authInfo.setUserName( user.getUserName() );
		logger.info( "username: " + authInfo.getUserName() );

		return authInfo;
	}

	/**
	 * Retrieves representation of an instance of de.iai.ilcd.services.AuthenticateResource
	 * 
	 * @return an instance of java.lang.String
	 */
	@GET
	@Path( "login" )
	@Produces( "text/plain" )
	public String login( @QueryParam( "userName" ) String userName, @QueryParam( "password" ) String password ) {
		// TODO return proper representation object
		logger.info( "authenticate/login" );
		Subject currentUser = SecurityUtils.getSubject();
		if ( !currentUser.isAuthenticated() ) {
			// no user currently logged in
			if ( userName == null || password == null ) {
				Response.status( Status.UNAUTHORIZED );
				return "user name and password must have a value";
			}

			UserDao dao = new UserDao();
			String salt = dao.getSalt( userName );
			if ( salt == null ) {
				Response.status( Status.UNAUTHORIZED );
				return "incorrect password or user name";
			}

			UsernamePasswordToken token = new UsernamePasswordToken( userName, IlcdSecurityRealm.getEncryptedPassword( password, salt ) );
			// token.setRememberMe(true);
			try {
				currentUser.login( token );

				return "Login successful";
				// message.setSummary("Your login was successful");
			}
			catch ( UnknownAccountException uae ) {
				Response.status( Status.UNAUTHORIZED );
				return "unknown error while verifying credentials";
			}
			catch ( IncorrectCredentialsException ice ) {
				Response.status( Status.UNAUTHORIZED );
				return "incorrect password or user name";
			}
			catch ( LockedAccountException lae ) {
				Response.status( Status.UNAUTHORIZED );
				return "account is locked; contact administrator";
			}
			catch ( AuthenticationException au ) {
				Response.status( Status.UNAUTHORIZED );
				return "incorrect password or user name";
			}
			catch ( Exception e ) {
				Response.status( Status.UNAUTHORIZED );
				return "unknown error while verifying credentials";
			}
		}
		return "You are already logged in as a user";
	}

	@GET
	@Path( "logout" )
	@Produces( "text/plain" )
	public String logout() {
		logger.info( "authenticate/logout" );
		Subject currentUser = SecurityUtils.getSubject();
		if ( currentUser.isAuthenticated() ) {
			SecurityUtils.getSecurityManager().logout( currentUser );
			Response.status( Status.OK );
			return "successfully logged out";
		}
		// no user currently logged in
		return "currently not authenticated";
	}

}
