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

package de.iai.ilcd.persistence;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See http://wiki.eclipse.org/Customizing_the_EclipseLink_Application_(ELUG) Use for clients that would like to use a
 * JTA SE pu instead of a RESOURCE_LOCAL SE pu.
 */
public class JPAEclipseLinkSessionCustomizer implements SessionCustomizer {

	public static Logger logger = LoggerFactory.getLogger( de.iai.ilcd.persistence.JPAEclipseLinkSessionCustomizer.class );

	/**
	 * Get a dataSource connection and set it on the session with lookupType=STRING_LOOKUP
	 */
	@SuppressWarnings( "unused" )
	public void customize( Session session ) throws Exception {
		JNDIConnector connector = null;
		Context context = null;
		try {
			context = new InitialContext();
			if ( context != null ) {
				connector = (JNDIConnector) session.getLogin().getConnector(); // possible
																			   // CCE
				// Change from COMPOSITE_NAME_LOOKUP to STRING_LOOKUP
				// Note: if both jta and non-jta elements exist this will only
				// change the first one - and may still result in
				// the COMPOSITE_NAME_LOOKUP being set
				// Make sure only jta-data-source is in persistence.xml with no
				// non-jta-data-source property set
				connector.setLookupType( JNDIConnector.STRING_LOOKUP );

				// Or, if you are specifying both JTA and non-JTA in your
				// persistence.xml then set both connectors to be safe
				JNDIConnector writeConnector = (JNDIConnector) session.getLogin().getConnector();
				writeConnector.setLookupType( JNDIConnector.STRING_LOOKUP );
				JNDIConnector readConnector = (JNDIConnector) ((DatabaseLogin) ((ServerSession) session).getReadConnectionPool().getLogin()).getConnector();
				readConnector.setLookupType( JNDIConnector.STRING_LOOKUP );

				logger.info( "_JPAEclipseLinkSessionCustomizer: configured " + connector.getName() );
			}
			else {
				throw new Exception( "_JPAEclipseLinkSessionCustomizer: Context is null" );
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
