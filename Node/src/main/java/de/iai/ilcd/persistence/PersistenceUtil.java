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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence utility class
 */
public class PersistenceUtil {

	/**
	 * The entity manager factory
	 */
	private static final EntityManagerFactory emf;

	/**
	 * Thread local entity manager
	 */
	private static final ThreadLocal<EntityManager> threadEntityManager = new ThreadLocal<EntityManager>();

	/**
	 * Thread local transaction
	 */
	private static final ThreadLocal<EntityTransaction> threadTransaction = new ThreadLocal<EntityTransaction>();

	/**
	 * Logger
	 */
	public static Logger logger = LoggerFactory.getLogger( de.iai.ilcd.persistence.PersistenceUtil.class );

	// Create the initial SessionFactory from the default configuration files
	static {
		try {
			logger.debug( "Initializing EntityManagerFactory" );
			emf = Persistence.createEntityManagerFactory( "soda4LCADBPU" );
		}
		catch ( Throwable ex ) {
			// We have to catch Throwable, otherwise we will miss
			// NoClassDefFoundError and other subclasses of Error
			logger.error( "error initializing EntityManagerFactory", ex );
			throw new ExceptionInInitializerError( ex );
		}
	}

	/**
	 * Get the current (thread local) entity manager
	 * 
	 * @return thread local entity manager
	 */
	public static EntityManager getEntityManager() {
		EntityManager em = (EntityManager) threadEntityManager.get();
		if ( em == null ) {
			em = emf.createEntityManager();
			threadEntityManager.set( em );
		}
		return em;
	}

	/**
	 * Close the current (thread local) entity manager
	 */
	public static void closeEntityManager() {
		EntityManager em = (EntityManager) threadEntityManager.get();
		threadEntityManager.set( null );
		if ( em != null && em.isOpen() ) {
			em.close();
		}
	}

	/**
	 * Begin transaction for current (thread local) entity manager
	 */
	static void beginTransaction() {
		EntityTransaction tx = (EntityTransaction) threadTransaction.get();
		if ( tx == null ) {
			tx = getEntityManager().getTransaction();
			threadTransaction.set( tx );
		}
		if ( !tx.isActive() ) {
			tx.begin();
		}
	}

	/**
	 * Commit current (thread local) transaction
	 */
	static void commitTransaction() {
		EntityTransaction tx = (EntityTransaction) threadTransaction.get();
		if ( tx != null && tx.isActive() ) {
			tx.commit();
		}
		threadTransaction.set( null );
	}

	/**
	 * Roll back current (thread local) transaction
	 */
	static void rollbackTransaction() {
		EntityTransaction tx = (EntityTransaction) threadTransaction.get();
		threadTransaction.set( null );
		if ( tx != null && tx.isActive() ) {
			tx.rollback();
		}
	}

}
