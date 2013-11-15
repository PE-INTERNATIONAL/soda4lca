package de.iai.ilcd.security;

import java.util.Collection;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;

/**
 * Web delegating shiro subject for soda4LCA. This is basically the
 * same as {@link WebDelegatingSubject} but omits the {@link #hasPrincipals()} check for all
 * <code>isPermitted(...)</code> methods.
 */
public class SodaWebDelegatingSubject extends WebDelegatingSubject {

	/**
	 * Create the subject
	 * 
	 * @param principals
	 *            the principals
	 * @param authenticated
	 *            authenticated during this session
	 * @param host
	 *            host
	 * @param session
	 *            session
	 * @param sessionEnabled
	 *            flag if session enabled
	 * @param request
	 *            request
	 * @param response
	 *            response
	 * @param securityManager
	 *            security manager
	 */
	public SodaWebDelegatingSubject( PrincipalCollection principals, boolean authenticated, String host, Session session, boolean sessionEnabled,
			ServletRequest request, ServletResponse response, SecurityManager securityManager ) {
		super( principals, authenticated, host, session, sessionEnabled, request, response, securityManager );
	}

	/**
	 * {@inheritDoc} <br />
	 * Removed {@link #hasPrincipals()} check.
	 */
	@Override
	public boolean isPermitted( String permission ) {
		return this.securityManager.isPermitted( this.getPrincipals(), permission );
	}

	/**
	 * {@inheritDoc} <br />
	 * Removed {@link #hasPrincipals()} check.
	 */
	@Override
	public boolean isPermitted( Permission permission ) {
		return this.securityManager.isPermitted( this.getPrincipals(), permission );
	}

	/**
	 * {@inheritDoc} <br />
	 * Removed {@link #hasPrincipals()} check.
	 */
	@Override
	public boolean[] isPermitted( String... permissions ) {
		return this.securityManager.isPermitted( this.getPrincipals(), permissions );
	}

	/**
	 * {@inheritDoc} <br />
	 * Removed {@link #hasPrincipals()} check.
	 */
	@Override
	public boolean[] isPermitted( List<Permission> permissions ) {
		return this.securityManager.isPermitted( this.getPrincipals(), permissions );
	}

	/**
	 * {@inheritDoc} <br />
	 * Removed {@link #hasPrincipals()} check.
	 */
	@Override
	public boolean isPermittedAll( String... permissions ) {
		return this.securityManager.isPermittedAll( this.getPrincipals(), permissions );
	}

	/**
	 * {@inheritDoc} <br />
	 * Removed {@link #hasPrincipals()} check.
	 */
	@Override
	public boolean isPermittedAll( Collection<Permission> permissions ) {
		return this.securityManager.isPermittedAll( this.getPrincipals(), permissions );
	}

}
