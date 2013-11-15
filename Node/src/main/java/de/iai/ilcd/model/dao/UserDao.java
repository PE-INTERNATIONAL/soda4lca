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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * 
 * 
 */
public class UserDao extends AbstractLongIdObjectDao<User> {

	private static final Logger logger = LoggerFactory.getLogger( UserDao.class );

	public UserDao() {
		super( "User", User.class );
	}

	@SuppressWarnings( "unchecked" )
	public List<User> getUsers() {
		EntityManager em = PersistenceUtil.getEntityManager();
		List<User> users = em.createQuery( "select user from User user order by user.userName" ).getResultList();
		return users;
	}

	@SuppressWarnings( "unchecked" )
	public List<Long> getUserIds( List<Organization> orgs ) {
		if ( orgs == null || orgs.isEmpty() ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		List<Long> orgIds = new ArrayList<Long>();
		for ( Organization o : orgs ) {
			orgIds.add( o.getId() );
		}

		try {
			return (List<Long>) em.createQuery( "SELECT DISTINCT u.id FROM User u WHERE u.organization.id IN(" + StringUtils.join( orgIds, ',' ) + ")" )
					.getResultList();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	public List<User> getUsers( Organization org, Integer first, Integer pageSize ) {
		if ( org == null ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		try {
			Query q = em.createQuery( "SELECT DISTINCT u FROM User u WHERE u.organization.id = :orgId ORDER BY u.userName" );
			q.setParameter( "orgId", org.getId() );
			if ( first != null ) {
				q.setFirstResult( first.intValue() );
			}
			if ( pageSize != null ) {
				q.setMaxResults( pageSize.intValue() );
			}
			return (List<User>) q.getResultList();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	public Long getUsersCount( Organization org ) {
		if ( org == null ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		try {
			Query q = em.createQuery( "SELECT COUNT(DISTINCT u) FROM User u WHERE u.organization.id = :orgId ORDER BY u.userName" );
			q.setParameter( "orgId", org.getId() );
			return (Long) q.getSingleResult();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	public List<User> getUsers( Organization org ) {
		if ( org == null ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		try {
			return (List<User>) em.createQuery( "SELECT DISTINCT u FROM User u WHERE u.organization.id = :orgId ORDER BY u.userName" ).setParameter( "orgId",
					org.getId() ).getResultList();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	public List<User> getOrgLessUsers() {
		EntityManager em = PersistenceUtil.getEntityManager();

		try {
			return (List<User>) em.createQuery( "SELECT DISTINCT u FROM User u WHERE u.organization is NULL ORDER BY u.userName" ).getResultList();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	public User getUser( String userName ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		User user = null;
		try {
			user = (User) em.createQuery( "select user from User user where user.userName=:userName" ).setParameter( "userName", userName ).getSingleResult();
		}
		catch ( NoResultException e ) {
			// we do nothing here; just return null
		}
		return user;
	}

	public String getSalt( String userName ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			return (String) em.createQuery( "select u.passwordHashSalt from User u where u.userName=:userName" ).setParameter( "userName", userName )
					.getSingleResult();
		}
		catch ( NoResultException e ) {
			return null;
		}
	}

	/**
	 * Get a user by ID
	 * 
	 * @deprecated use {@link #getById(long)}
	 * @param userId
	 *            user id
	 * @return loaded user
	 */
	@Deprecated
	public User getUser( long userId ) {
		return this.getById( userId );
	}

}
