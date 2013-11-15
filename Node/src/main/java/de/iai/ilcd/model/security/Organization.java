package de.iai.ilcd.model.security;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.Table;

import de.iai.ilcd.model.ILongIdObject;

/**
 * Class that represents an organization
 */
@Entity
@Table( name = "organization" )
public class Organization implements ILongIdObject {

	/**
	 * ID of the organization
	 */
	@Id
	@Column( name = "id" )
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	/**
	 * Name of the organization
	 */
	@Basic
	@Column( name = "name" )
	private String name;

	/**
	 * Address information
	 */
	@Embedded
	private Address address = new Address();

	/**
	 * Organization unit
	 */
	@Basic
	@Column( name = "ORGANISATIONUNIT" )
	private String organisationUnit;

	/**
	 * Industrial sector
	 */
	@ManyToOne( fetch = FetchType.EAGER )
	@JoinColumn( name = "SECTOR_ID" )
	private IndustrialSector sector;

	/**
	 * Admin User
	 */
	@ManyToOne( fetch = FetchType.LAZY, optional = true )
	@JoinColumn( name = "admin_user", nullable = true )
	private User adminUser;

	/**
	 * Admin Group
	 */
	@ManyToOne( fetch = FetchType.LAZY, optional = true )
	@JoinColumn( name = "admin_group", nullable = true )
	private UserGroup adminGroup;

	/**
	 * All users of this organization
	 */
	@OneToMany( mappedBy = "organization", fetch = FetchType.LAZY )
	private List<User> users = new ArrayList<User>();

	/**
	 * All groups of this organization
	 */
	@OneToMany( mappedBy = "organization", fetch = FetchType.LAZY )
	private List<UserGroup> groups = new ArrayList<UserGroup>();

	/**
	 * Get the ID of the organization
	 * 
	 * @return the ID of the organization
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Set the ID of the organization
	 * 
	 * @param id
	 *            the ID of the organization
	 */
	public void setId( Long id ) {
		this.id = id;
	}

	/**
	 * Get the name of the organization
	 * 
	 * @return the name of the organization
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of the organization
	 * 
	 * @param name
	 *            the name of the organization
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Get the address information
	 * 
	 * @return address information
	 */
	public Address getAddress() {
		return this.address;
	}

	/**
	 * Set the address information
	 * 
	 * @param address
	 *            address information to set
	 */
	public void setAddress( Address address ) {
		this.address = address;
	}

	/**
	 * Get the organization unit
	 * 
	 * @return organization unit
	 */
	public String getOrganisationUnit() {
		return this.organisationUnit;
	}

	/**
	 * Set the organization unit
	 * 
	 * @param organisationUnit
	 *            organization unit to set
	 */
	public void setOrganisationUnit( String organisationUnit ) {
		this.organisationUnit = organisationUnit;
	}

	/**
	 * Get the industrial sector
	 * 
	 * @return industrial sector
	 */
	public IndustrialSector getSector() {
		return this.sector;
	}

	/**
	 * Set the industrial sector
	 * 
	 * @param sector
	 *            industrial sector to set
	 */
	public void setSector( IndustrialSector sector ) {
		this.sector = sector;
	}

	/**
	 * Add a user to this organization
	 * 
	 * @param u
	 *            user to add
	 */
	public void addUser( User u ) {
		if ( u == null ) {
			return;
		}
		// only if user is not contained
		if ( !this.users.contains( u ) ) {
			this.users.add( u );
			if ( u.getOrganization() != null && !u.getOrganization().equals( this ) ) {
				u.getOrganization().removeUser( u );
			}
			u.setOrganization( this );
		}
	}

	/**
	 * Remove a user from this organization
	 * 
	 * @param u
	 *            user to remove
	 */
	public void removeUser( User u ) {
		if ( u == null ) {
			return;
		}
		// only if user is contained
		if ( this.users.contains( u ) ) {
			this.users.remove( u );
			if ( u.getOrganization() != null ) {
				u.setOrganization( null );
			}
		}
	}

	/**
	 * Add a group to this organization
	 * 
	 * @param g
	 *            group to add
	 */
	public void addGroup( UserGroup g ) {
		if ( g == null ) {
			return;
		}
		// only if group is not contained
		if ( !this.groups.contains( g ) ) {
			this.groups.add( g );
			if ( g.getOrganization() != null && !g.getOrganization().equals( this ) ) {
				g.getOrganization().removeGroup( g );
			}
			g.setOrganization( this );
		}
	}

	/**
	 * Remove a group from this organization
	 * 
	 * @param g
	 *            group to remove
	 */
	public void removeGroup( UserGroup g ) {
		if ( g == null ) {
			return;
		}
		// only if group is contained
		if ( this.groups.contains( g ) ) {
			this.groups.remove( g );
			if ( g.getOrganization() != null ) {
				g.setOrganization( null );
			}
		}
	}

	/**
	 * Get the users of this organization
	 * 
	 * @return users of this organization
	 */
	public List<User> getUsers() {
		return this.users;
	}

	/**
	 * Get the groups of this organization
	 * 
	 * @return groups of this organization
	 */
	public List<UserGroup> getGroups() {
		return this.groups;
	}

	/**
	 * Set the list of users
	 * 
	 * @param users
	 *            list to set
	 */
	public void setUsers( List<User> users ) {
		this.users = users;
	}

	/**
	 * Set the list of groups
	 * 
	 * @param groups
	 *            list to set
	 */
	public void setGroups( List<UserGroup> groups ) {
		this.groups = groups;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( this.getClass() != obj.getClass() ) {
			return false;
		}
		Organization other = (Organization) obj;
		if ( this.name == null ) {
			if ( other.name != null ) {
				return false;
			}
		}
		else if ( !this.name.equals( other.name ) ) {
			return false;
		}
		return true;
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
	 * Get the admin user
	 * 
	 * @return the adminUser to get
	 */
	public User getAdminUser() {
		return this.adminUser;
	}

	/**
	 * Set the admin user
	 * 
	 * @param adminUser
	 *            the adminUser to set
	 */
	public void setAdminUser( User adminUser ) {
		this.adminUser = adminUser;
	}

	/**
	 * Get the admin group
	 * 
	 * @return the adminGroup to get
	 */
	public UserGroup getAdminGroup() {
		return this.adminGroup;
	}

	/**
	 * Set the admin group
	 * 
	 * @param adminGroup
	 *            the adminGroup to set
	 */
	public void setAdminGroup( UserGroup adminGroup ) {
		this.adminGroup = adminGroup;
	}

	/**
	 * Convenience method to determine if organization has an admin user
	 * 
	 * @return <code>true</code> if organization has an admin user, <code>false</code> otherwise
	 */
	public boolean hasAdmin() {
		return this.adminUser != null;
	}

	/**
	 * Convenience method to determine if organization has an admin group
	 * 
	 * @return <code>true</code> if organization has an admin group, <code>false</code> otherwise
	 */
	public boolean hasAdminGroup() {
		return this.adminGroup != null;
	}

	/**
	 * Convenience method to determine if organization has an admin user or an admin group
	 * 
	 * @return <code>true</code> if organization has an admin user or an admin group, <code>false</code> otherwise
	 */
	public boolean hasAdminOrAdminGroup() {
		return this.hasAdmin() || this.hasAdminGroup();
	}

	/**
	 * Convenience method to determine if organization has an admin user and an admin group
	 * 
	 * @return <code>true</code> if organization has an admin user and an admin group, <code>false</code> otherwise
	 */
	public boolean hasAdminAndAdminGroup() {
		return this.hasAdmin() && this.hasAdminGroup();
	}

}
