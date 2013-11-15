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

package de.iai.ilcd.model.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;

/**
 * Class that represents user group entity
 * 
 */
@Entity
@Table( name = "usergroup" )
public class UserGroup implements ISecurityEntity, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( nullable = false, unique = true )
	private String groupName;

	@ManyToMany( cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH } )
	// Does not contain CascadeType.REMOVE
	private List<User> users = new ArrayList<User>();

	/**
	 * The organization of the group
	 */
	@ManyToOne
	@JoinColumn( name = "organization" )
	private Organization organization;

	/**
	 * Create a new instance of UserGroup
	 */
	public UserGroup() {

	}

	/**
	 * Create a new instance of UserGroup and set the group name
	 * 
	 * @param groupName
	 *            the group name to set
	 */
	public UserGroup( String groupName ) {
		this.setGroupName( groupName );
	}

	/**
	 * Get the id
	 * 
	 * @return the id
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Set the id
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId( Long id ) {
		this.id = id;
	}

	/**
	 * Get the group name
	 * 
	 * @return the group name
	 */
	public String getGroupName() {
		return this.groupName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayName() {
		return this.groupName;
	}

	/**
	 * Set the group name
	 * 
	 * @param groupName
	 *            the group name to set
	 */
	public void setGroupName( String groupName ) {
		if ( groupName != null ) {
			this.groupName = groupName.trim();
		}
		else {
			throw new NullPointerException();
		}
	}

	/**
	 * Get the list of users
	 * 
	 * @return the list of users
	 */
	public List<User> getUsers() {
		return this.users;
	}

	/**
	 * Set the list of users
	 * 
	 * @param users
	 *            the list of users to set
	 */
	protected void setUsers( List<User> users ) {
		this.users = users;
	}

	/**
	 * Add the user to this group (this includes also {@linkplain User#addToGroup(UserGroup) adding} this group to the
	 * respective user's groups list)
	 * 
	 * @param user
	 *            the user to add to this group
	 */
	public void addUser( User user ) {
		if ( !this.containsUser( user ) ) {
			this.users.add( user );
			user.addToGroup( this );
		}
	}

	/**
	 * Remove the user from this group (this includes also {@linkplain User#removeFromGroup(UserGroup) removing} this
	 * group from the respective user's groups list)
	 * 
	 * @param user
	 *            the user to remove from this group
	 */
	public void removeUser( User user ) {
		if ( this.containsUser( user ) ) {
			this.users.remove( user );
			user.removeFromGroup( this );
		}
	}

	/**
	 * Remove all users from this group (this includes also {@linkplain User#removeFromGroup(UserGroup) removing} this
	 * group from the respective user's group list)
	 */
	public void removeAllUsers() {
		for ( User userToRemove : this.users ) {
			userToRemove.removeFromGroup( this );
		}
		this.users = null;
	}

	/**
	 * Get the organization of this group
	 * 
	 * @return organization of this group
	 */
	public Organization getOrganization() {
		return this.organization;
	}

	/**
	 * Set the organization of this group
	 * 
	 * @param organization
	 *            organization of this group to set
	 */
	public void setOrganization( Organization organization ) {
		if ( ObjectUtils.equals( this.organization, organization ) ) {
			return;
		}
		Organization oldOrg = this.organization;
		this.organization = organization;

		if ( oldOrg != null ) {
			oldOrg.removeGroup( this );
		}

		if ( this.organization != null ) {
			this.organization.addGroup( this );
		}
	}

	/**
	 * Convenience method to determine if group has users
	 * 
	 * @return <code>true</code> if group has users, <code>false</code> otherwise
	 */
	public boolean hasUsers() {
		return CollectionUtils.isNotEmpty( this.users );
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.groupName != null ? this.groupName.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof UserGroup) ) {
			return false;
		}
		UserGroup other = (UserGroup) object;
		if ( this.groupName != null && other.getGroupName() != null && this.groupName.equals( other.getGroupName() ) ) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.security.Group=[groupName:" + this.groupName + "]";
	}

	/**
	 * Determine if given user is in this group
	 * 
	 * @param user
	 *            user to check
	 * @return <code>true</code> if user is in group, <code>false</code> otherwise
	 */
	public boolean containsUser( User user ) {
		return this.users != null && this.users.contains( user );
	}

}
