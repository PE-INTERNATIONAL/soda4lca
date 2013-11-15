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

package de.iai.ilcd.model.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.lang.StringUtils;

import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * Common implementation for DAO objects
 * 
 * @param <T>
 *            Class that is being accessed
 */
public abstract class AbstractDao<T> {

	/**
	 * The name in JPA of class
	 */
	private final String jpaName;

	/**
	 * The class accessed by this DAO
	 */
	private final Class<T> accessedClass;

	/**
	 * Create a DAO
	 * 
	 * @param jpaName
	 *            the name in JPA of class
	 * @param accessedClass
	 *            the class accessed by this DAO
	 */
	public AbstractDao( String jpaName, Class<T> accessedClass ) {
		this.jpaName = jpaName;
		this.accessedClass = accessedClass;
	}

	/**
	 * Get the name in JPA of class
	 * 
	 * @return name in JPA of class
	 */
	protected String getJpaName() {
		return this.jpaName;
	}

	/**
	 * Get the class accessed by this DAO
	 * 
	 * @return class accessed by this DAO
	 */
	protected Class<T> getAccessedClass() {
		return this.accessedClass;
	}

	/**
	 * Get all data sets in the persistence unit of the represented type T
	 * 
	 * @return all data sets in the persistence unit of the represented type T
	 */
	@SuppressWarnings( "unchecked" )
	public List<T> getAll() {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery( "select a from " + this.jpaName + " a" ).getResultList();
	}

	/**
	 * Get the count of elements in the database for type T
	 * 
	 * @return count of elements in the database for type T
	 */
	public Long getAllCount() {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (Long) em.createQuery( "select count(a) from " + this.jpaName + " a" ).getSingleResult();
	}

	/**
	 * Get <code>pageSize</code> items starting on <code>startIndex</code>
	 * 
	 * @param startIndex
	 *            start index
	 * @param pageSize
	 *            page size
	 * @return list of matching elements
	 */
	@SuppressWarnings( "unchecked" )
	public List<T> get( int startIndex, int pageSize ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery( "select a from " + this.jpaName + " a" ).setFirstResult( startIndex ).setMaxResults( pageSize ).getResultList();
	}

	/**
	 * Join a list of strings via {@link StringUtils#join(java.util.Iterator, String)}
	 * 
	 * @param list
	 *            list to join
	 * @param separator
	 *            separator to use
	 * @return joined list
	 */
	protected String join( List<String> list, String separator ) {
		return StringUtils.join( list.iterator(), separator );
	}

	/**
	 * Default persist
	 * 
	 * @param obj
	 *            object to persist
	 * @throws PersistException
	 *             on errors (transaction is being rolled back)
	 */
	public void persist( T obj ) throws PersistException {
		if ( obj == null ) {
			return;
		}
		EntityManager em = PersistenceUtil.getEntityManager();
		EntityTransaction t = em.getTransaction();

		try {
			t.begin();
			em.persist( obj );
			t.commit();
		}
		catch ( Exception e ) {
			t.rollback();
			throw new PersistException( e.getMessage(), e );
		}
	}

	/**
	 * Default persist
	 * 
	 * @param objs
	 *            objects to persist
	 * @throws PersistException
	 *             on errors (transaction is being rolled back)
	 */
	public void persist( Collection<T> objs ) throws PersistException {
		if ( objs == null || objs.isEmpty() ) {
			return;
		}
		EntityManager em = PersistenceUtil.getEntityManager();
		EntityTransaction t = em.getTransaction();

		try {
			t.begin();
			for ( T obj : objs ) {
				em.persist( obj );
			}
			t.commit();
		}
		catch ( Exception e ) {
			t.rollback();
			throw new PersistException( e.getMessage(), e );
		}
	}

	/**
	 * Default remove: bring back to persistence context if required and delete
	 * 
	 * @param obj
	 *            object to remove
	 * @return remove object
	 * @throws Exception
	 *             on errors (transaction is being rolled back)
	 */
	public T remove( T obj ) throws Exception {
		if ( obj == null ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();
		EntityTransaction t = em.getTransaction();

		try {
			t.begin();
			T tmp = em.contains( obj ) ? obj : em.merge( obj );
			em.remove( tmp );
			t.commit();
			return tmp;
		}
		catch ( Exception e ) {
			t.rollback();
			throw e;
		}
	}

	/**
	 * Default remove: bring back to persistence context if required and delete
	 * 
	 * @param objs
	 *            objects to remove
	 * @return removed objects
	 * @throws Exception
	 *             on errors (transaction is being rolled back)
	 */
	public Collection<T> remove( Collection<T> objs ) throws Exception {
		if ( objs == null || objs.isEmpty() ) {
			return null;
		}
		Collection<T> res = new ArrayList<T>();
		EntityManager em = PersistenceUtil.getEntityManager();
		EntityTransaction t = em.getTransaction();

		try {
			t.begin();
			for ( T obj : objs ) {
				T tmp = em.contains( obj ) ? obj : em.merge( obj );
				em.remove( tmp );
				res.add( tmp );
			}
			t.commit();
			return res;
		}
		catch ( Exception e ) {
			t.rollback();
			throw e;
		}
	}

	/**
	 * Default merge
	 * 
	 * @param obj
	 *            object to merge
	 * @throws MergeException
	 *             on errors (transaction is being rolled back)
	 * @return managed object
	 */
	public T merge( T obj ) throws MergeException {
		if ( obj == null ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();
		EntityTransaction t = em.getTransaction();

		try {
			t.begin();
			obj = em.merge( obj );
			t.commit();
			return obj;
		}
		catch ( Exception e ) {
			t.rollback();
			throw new MergeException( "Cannot merge changes to " + String.valueOf( obj ) + " into the database", e );
		}
	}

	/**
	 * Default merge
	 * 
	 * @param objs
	 *            objects to merge
	 * @return list of merged/managed objects
	 * @throws MergeException
	 *             on errors (transaction is being rolled back)
	 */
	public Collection<T> merge( Collection<T> objs ) throws MergeException {
		if ( objs == null || objs.isEmpty() ) {
			return null;
		}
		final Collection<T> tmp = new ArrayList<T>();
		EntityManager em = PersistenceUtil.getEntityManager();
		EntityTransaction t = em.getTransaction();

		try {
			t.begin();
			for ( T obj : objs ) {
				tmp.add( em.merge( obj ) );
			}
			t.commit();
			return tmp;
		}
		catch ( Exception e ) {
			t.rollback();
			throw new MergeException( "Cannot merge changes to " + String.valueOf( objs ) + " into the database", e );
		}
	}

}
