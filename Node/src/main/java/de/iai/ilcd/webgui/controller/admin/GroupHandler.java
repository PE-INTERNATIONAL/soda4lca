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

package de.iai.ilcd.webgui.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DualListModel;

import de.iai.ilcd.model.dao.OrganizationDao;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.dao.UserGroupDao;
import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.model.security.UserGroup;
import de.iai.ilcd.security.IlcdAuthorizationInfo;
import de.iai.ilcd.security.SecurityUtil;
import de.iai.ilcd.security.UserAccessBean;
import de.iai.ilcd.webgui.controller.url.URLGeneratorBean;

/**
 * Handler for group detail view (admin area)
 */
@ManagedBean
@ViewScoped
public class GroupHandler extends AbstractAdminEntryHandler<UserGroup> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -2818858209085062926L;

	/**
	 * Dual list model for user assignment
	 */
	private DualListModel<User> dualUserAssignmentList;

	/**
	 * DAO to load user instances
	 */
	private final UserDao userDao = new UserDao();

	/**
	 * DAO to load group instances
	 */
	private final UserGroupDao groupDao = new UserGroupDao();

	/**
	 * User access bean
	 */
	@ManagedProperty( value = "#{user}" )
	private UserAccessBean userBean;

	/**
	 * List of all organizations
	 */
	private List<Organization> allOrganizations;

	/**
	 * Create a new instance of GroupHandler
	 */
	public GroupHandler() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void postConstruct() {
		// nothing to do :)
	}

	/**
	 * Initialize bean after dependency injection
	 */
	@Override
	protected void postEntrySet() {
		final UserGroup group = this.getEntry();

		if ( this.userBean.hasSuperAdminPermission() ) {
			OrganizationDao oDao = new OrganizationDao();
			this.allOrganizations = oDao.getAll();
		}
		else {
			final Organization organization = this.userBean.getUserObject().getOrganization();
			SecurityUtil.assertIsOrganizationAdmin( organization );
			group.setOrganization( organization );
		}

		if ( !this.isCreateView() ) {
			// initialize list of users
			List<User> sourceUsers = new ArrayList<User>();
			List<User> targetUsers = new ArrayList<User>();

			final Organization organization = group.getOrganization();
			if ( organization != null ) {
				for ( User user : this.userDao.getUsers( organization ) ) {
					sourceUsers.add( user );
				}
			}

			List<User> groupUsers = group.getUsers();
			for ( User user : groupUsers ) {
				targetUsers.add( user );
				sourceUsers.remove( user );
			}

			this.setDualUserAssignmentList( new DualListModel<User>( sourceUsers, targetUsers ) );
		}

	}

	/**
	 * Get the user group
	 * 
	 * @return the user group
	 */
	public UserGroup getGroup() {
		return this.getEntry();
	}

	/**
	 * Set the user group
	 * 
	 * @param group
	 *            the user group to set
	 */
	public void setGroup( UserGroup group ) {
		this.setEntry( group );
	}

	/**
	 * Create a new user group
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	public boolean createGroup() {
		final UserGroup group = this.getEntry();
		SecurityUtil.assertIsOrganizationAdmin( group.getOrganization() );

		if ( StringUtils.isBlank( group.getGroupName() ) ) {
			this.addI18NFacesMessage( "facesMsg.group.noName", FacesMessage.SEVERITY_ERROR );
			return false;
		}

		UserGroup existingGroup = this.groupDao.getGroup( group.getGroupName() );
		if ( existingGroup != null ) {
			this.addI18NFacesMessage( "facesMsg.group.alreadyExists", FacesMessage.SEVERITY_ERROR );
			return false;
		}

		if ( !this.isCreateView() ) {
			// transfer selected users to group object
			for ( User u : this.dualUserAssignmentList.getTarget() ) {
				group.addUser( this.userDao.getUser( u.getUserName() ) );
			}
		}

		try {
			this.groupDao.persist( group );
			this.addI18NFacesMessage( "facesMsg.group.createSuccess", FacesMessage.SEVERITY_INFO );
			return true;
		}
		catch ( Exception e ) {
			this.addI18NFacesMessage( "facesMsg.group.createError", FacesMessage.SEVERITY_ERROR );
			return false;
		}
	}

	/**
	 * Change an existing group
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	public boolean changeGroup() {
		final UserGroup group = this.getEntry();
		SecurityUtil.assertIsOrganizationAdmin( group.getOrganization() );

		if ( StringUtils.isBlank( group.getGroupName() ) ) {
			this.addI18NFacesMessage( "facesMsg.group.noName", FacesMessage.SEVERITY_ERROR );
			return false;
		}

		final Long currentId = group.getId();
		UserGroup existingGroup = this.groupDao.getGroup( group.getGroupName() );
		// If name is not changed, an existing will be found: the group that is being edited. But: that's no problem =>
		// Check ID, too!
		if ( existingGroup != null && !existingGroup.getId().equals( currentId ) ) {
			this.addI18NFacesMessage( "facesMsg.group.alreadyExists", FacesMessage.SEVERITY_ERROR );
			return false;
		}

		// synchronize users (list copy to avoid concurrent modification issues)
		List<User> tempList = new ArrayList<User>( group.getUsers() );
		// we need to do it in that way so that the users side is synchronized too, i.e. remove group from user object
		for ( User user : tempList ) {
			group.removeUser( user );
		}
		// now we can add the users again
		for ( User u : this.dualUserAssignmentList.getTarget() ) {
			group.addUser( this.userDao.getUser( u.getUserName() ) );
		}

		try {
			this.setEntry( this.groupDao.merge( group ) );
			this.addI18NFacesMessage( "facesMsg.group.changeSuccess", FacesMessage.SEVERITY_INFO );
			IlcdAuthorizationInfo.permissionsChanged();
			return true;
		}
		catch ( Exception e ) {
			this.addI18NFacesMessage( "facesMsg.group.changeError", FacesMessage.SEVERITY_ERROR );
			IlcdAuthorizationInfo.permissionsChanged();
			return false;
		}
	}

	/**
	 * Get a list of all organizations
	 * 
	 * @return list of all organizations
	 */
	public List<Organization> getAllOrganizations() {
		return this.allOrganizations;
	}

	/**
	 * Method for the injection of user access bean
	 * 
	 * @param userBean
	 *            user access bean to set
	 */
	public void setUserBean( UserAccessBean userBean ) {
		this.userBean = userBean;
	}

	/**
	 * Get the user access bean
	 * 
	 * @return user access bean
	 */
	public UserAccessBean getUserBean() {
		return this.userBean;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserGroup createEmptyEntryInstance() {
		return new UserGroup();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserGroup loadEntryInstance( long id ) throws Exception {
		return this.groupDao.getById( id );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createEntry() {
		return this.createGroup();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean changeAttachedEntry() {
		return this.changeGroup();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditEntryUrl( URLGeneratorBean url, Long id ) {
		return url.getGroup().getEdit( id );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCloseEntryUrl( URLGeneratorBean url ) {
		return url.getGroup().getShowList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNewEntryUrl( URLGeneratorBean url ) {
		return url.getGroup().getNew();
	}

	/**
	 * Get the dual list model for the pick list to select group users
	 * 
	 * @return dual list model for the pick list to select group users
	 */
	public DualListModel<User> getDualUserAssignmentList() {
		return this.dualUserAssignmentList;
	}

	/**
	 * Set the dual list model for the pick list to select group users
	 * 
	 * @param dualUserAssignmentList
	 *            dual list model for the pick list to select group users to set
	 */
	public void setDualUserAssignmentList( DualListModel<User> dualUserAssignmentList ) {
		this.dualUserAssignmentList = dualUserAssignmentList;
	}

}
