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

import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.UserGroup;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * 
 */
public class UserGroupDao extends AbstractLongIdObjectDao<UserGroup> {

	public UserGroupDao() {
		super( "UserGroup", UserGroup.class );
	}

	public List<UserGroup> getGroups() {
		return this.getAll();
	}

	@SuppressWarnings( "unchecked" )
	public List<Long> getGroupIds( List<Organization> orgs ) {
		if ( orgs == null || orgs.isEmpty() ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		List<Long> orgIds = new ArrayList<Long>();
		for ( Organization o : orgs ) {
			orgIds.add( o.getId() );
		}

		try {
			return (List<Long>) em.createQuery( "SELECT DISTINCT g.id FROM UserGroup g WHERE g.organization.id IN(" + StringUtils.join( orgIds, ',' ) + ")" )
					.getResultList();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	public List<UserGroup> getGroups( Organization org ) {
		if ( org == null ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		try {
			return (List<UserGroup>) em.createQuery( "SELECT DISTINCT g FROM UserGroup g WHERE g.organization.id = :orgId ORDER BY g.groupName" ).setParameter(
					"orgId", org.getId() ).getResultList();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	public List<UserGroup> getGroups( Organization org, Integer first, Integer pageSize ) {
		if ( org == null ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		try {
			Query q = em.createQuery( "SELECT DISTINCT g FROM UserGroup g WHERE g.organization.id = :orgId ORDER BY g.groupName" );
			q.setParameter( "orgId", org.getId() );
			if ( first != null ) {
				q.setFirstResult( first.intValue() );
			}
			if ( pageSize != null ) {
				q.setMaxResults( pageSize.intValue() );
			}
			return (List<UserGroup>) q.getResultList();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	public Long getGroupsCount( Organization org ) {
		if ( org == null ) {
			return null;
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		try {
			Query q = em.createQuery( "SELECT COUNT(DISTINCT g) FROM UserGroup g WHERE g.organization.id = :orgId ORDER BY g.groupName" );
			q.setParameter( "orgId", org.getId() );
			return (Long) q.getSingleResult();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	public List<UserGroup> getOrgLessGroups() {
		EntityManager em = PersistenceUtil.getEntityManager();

		try {
			return (List<UserGroup>) em.createQuery( "SELECT DISTINCT g FROM UserGroup g WHERE g.organization is NULL ORDER BY g.groupName" ).getResultList();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	public UserGroup getGroup( String groupName ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		UserGroup group = null;
		try {
			group = (UserGroup) em.createQuery( "select g from UserGroup g where g.groupName=:groupName" ).setParameter( "groupName", groupName )
					.getSingleResult();
		}
		catch ( NoResultException e ) {
			// we do nothing here; just return null
		}
		return group;
	}

	public UserGroup getGroup( long groupId ) {
		return this.getById( groupId );
	}

}
