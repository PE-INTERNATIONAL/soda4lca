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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import de.iai.ilcd.util.SodaUtil;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "user" )
public class User implements ISecurityEntity, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( unique = true )
	private String userName;

	@Column( name = "PASSWORD_HASH" )
	private String passwordHash;

	@Column( name = "PASSWORD_HASH_SALT" )
	private String passwordHashSalt;

	private String firstName;

	private String lastName;

	private String title;

	@Enumerated( EnumType.STRING )
	private Gender gender = null;

	private String email;

	private String registrationKey;
	
	private String dsPurpose; 
	
	public String getDsPurpose() {
		return dsPurpose;
	}

	public void setDsPurpose(String dspurpose) {
		this.dsPurpose = dspurpose;
	}

	private String jobPosition;
	
	public String getJobPosition() {
		return jobPosition;
	}

	public void setJobPosition( String jobPosition ) {
		this.jobPosition = jobPosition;
	}
	

	@Embedded
	private Address address = new Address();

	@ManyToMany( mappedBy = "users", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH } )
	// Does not contain CascadeType.REMOVE
	private List<UserGroup> groups;

	@Basic
	@Column( name = "super_admin_permission" )
	private boolean superAdminPermission;

	/**
	 * The organization of the user
	 */
	@ManyToOne( optional = true )
	@JoinColumn( name = "organization", nullable = true )
	private Organization organization;

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public Address getAddress() {
		return this.address;
	}

	public void setAddress( Address address ) {
		this.address = address;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail( String email ) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName( String firstName ) {
		this.firstName = firstName;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender( Gender gender ) {
		this.gender = gender;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName( String lastName ) {
		this.lastName = lastName;
	}

	public String getPasswordHash() {
		return this.passwordHash;
	}

	public void setPasswordHash( String passwordHash ) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordHashSalt() {
		return this.passwordHashSalt;
	}

	public void setPasswordHashSalt( String passwordHashSalt ) {
		this.passwordHashSalt = passwordHashSalt;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public String getUserName() {
		return this.userName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayName() {
		return this.userName;
	}

	public void setUserName( String userName ) {
		this.userName = userName;
	}

	public String getRegistrationKey() {
		return this.registrationKey;
	}

	public void setRegistrationKey( String registrationKey ) {
		this.registrationKey = registrationKey;
	}

	public List<UserGroup> getGroups() {
		return this.groups;
	}

	protected void setGroups( List<UserGroup> groups ) {
		this.groups = groups;
	}

	/**
	 * 
	 * @param group
	 */
	public void addToGroup( UserGroup group ) {
		if ( !this.groups.contains( group ) ) {
			this.groups.add( group );
			group.addUser( this );
		}
	}

	public void removeFromGroup( UserGroup group ) {
		if ( this.groups.contains( group ) ) {
			this.groups.remove( group );
		}
	}

	public void removeFromAllGroups() {
		List<UserGroup> allGroups = new ArrayList<UserGroup>();
		allGroups.addAll( this.groups );
		for ( UserGroup group : allGroups ) {
			group.removeUser( this ); // this removes the group also locally
		}
	}

	/**
	 * Get the organization of this user
	 * 
	 * @return organization of this user
	 */
	public Organization getOrganization() {
		return this.organization;
	}

	/**
	 * Set the organization of this user
	 * 
	 * @param organization
	 *            organization of this user to set
	 */
	public void setOrganization( Organization organization ) {
		if ( ObjectUtils.equals( this.organization, organization ) ) {
			return;
		}
		Organization oldOrg = this.organization;
		this.organization = organization;

		if ( oldOrg != null ) {
			oldOrg.removeUser( this );
		}

		if ( this.organization != null ) {
			this.organization.addUser( this );
		}
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.userName != null ? this.userName.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof User) ) {
			return false;
		}
		User other = (User) object;
		if ( this.userName != null && other.getUserName() != null && this.userName.equals( other.getUserName() ) ) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.security.User[userName=" + this.userName + "]";
	}

	/**
	 * Empty address means only <code>null</code> values in DB &rArr; address
	 * is null after load from DB. If address is <code>null</code>, it will
	 * be initialized with an empty address object.
	 */
	@PostLoad
	@PostUpdate
	protected void postLoad() {
		if ( this.address == null ) {
			this.address = new Address();
		}
	}

	/**
	 * Get the flag if this user has super admin permission
	 * 
	 * @return <code>true</code> if super admin permission set, <code>false</code> otherwise
	 */
	public boolean isSuperAdminPermission() {
		return this.superAdminPermission;
	}

	/**
	 * Set the flag if this user has super admin permission (ignores incoming <code>false</code> for built-in admin user
	 * (ID = {@link SodaUtil#ADMIN_ID})
	 * 
	 * @param superAdminPermission
	 *            new flag
	 */
	public void setSuperAdminPermission( boolean superAdminPermission ) {
		this.superAdminPermission = superAdminPermission || ObjectUtils.equals( this.getId(), SodaUtil.ADMIN_ID );
	}
}
