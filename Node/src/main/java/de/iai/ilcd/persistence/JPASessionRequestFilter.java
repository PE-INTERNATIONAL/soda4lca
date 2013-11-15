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

/*
 * HibernateSessionRequestFilter.java
 * Created on 15. Mai 2006, 10:18
 */

package de.iai.ilcd.persistence;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author clemens.duepmeier
 */

public class JPASessionRequestFilter implements javax.servlet.Filter {

	final static Logger log = LoggerFactory.getLogger( JPASessionRequestFilter.class );

	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {

		try {
			// sf.getCurrentSession().beginTransaction();
			// we don't need beginTransaction, on every request. Let the dao objects handle this

			// Call the next filter (continue request processing)
			chain.doFilter( request, response );

			// Commit and cleanup
			log.trace( "Committing the database transaction" );
			PersistenceUtil.commitTransaction();
			PersistenceUtil.closeEntityManager();

		}
		catch ( Throwable ex ) {
			// Rollback only
			// log.error("Could not commit transaction",ex);
			try {
				PersistenceUtil.rollbackTransaction();
				PersistenceUtil.closeEntityManager();
			}
			catch ( Throwable rbEx ) {
				log.error( "Could not rollback transaction after exception!", rbEx );
			}

			// Let others handle it... maybe another interceptor for exceptions?
			throw new ServletException( ex );
		}
	}

	@Override
	public void init( FilterConfig filterConfig ) throws ServletException {
		// log.debug("Initializing filter...");
	}

	@Override
	public void destroy() {
	}

}
