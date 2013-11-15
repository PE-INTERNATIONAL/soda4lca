package de.iai.ilcd.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.mgt.DefaultSubjectFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.subject.WebSubjectContext;

/**
 * Shiro subject factory for soda4LCA application which creates {@link SodaWebDelegatingSubject} instances for
 * {@link #createSubject(SubjectContext)}
 */
public class SodaSubjectFactory extends DefaultSubjectFactory {

	/**
	 * {@inheritDoc} <br />
	 * Original code taken from {@link DefaultWebSubjectFactory} and modified
	 * in order to return {@link SodaWebDelegatingSubject} instances.
	 */
	@Override
	public Subject createSubject( SubjectContext context ) {
		if ( !(context instanceof WebSubjectContext) ) {
			return super.createSubject( context );
		}
		WebSubjectContext wsc = (WebSubjectContext) context;
		SecurityManager securityManager = wsc.resolveSecurityManager();
		Session session = wsc.resolveSession();
		boolean sessionEnabled = wsc.isSessionCreationEnabled();
		PrincipalCollection principals = wsc.resolvePrincipals();
		boolean authenticated = wsc.resolveAuthenticated();
		String host = wsc.resolveHost();
		ServletRequest request = wsc.resolveServletRequest();
		ServletResponse response = wsc.resolveServletResponse();

		return new SodaWebDelegatingSubject( principals, authenticated, host, session, sessionEnabled, request, response, securityManager );
	}

}
