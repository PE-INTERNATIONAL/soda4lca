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

package de.iai.ilcd.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;

import de.iai.ilcd.model.dao.OrganizationDao;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.dao.UserGroupDao;
import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.User;

/**
 * Authorization Info. If anything is changed on the permissions, call {@link #permissionsChanged()} to trigger a reload
 * of the permissions for all currently active users on their next request.
 */
@SuppressWarnings( "serial" )
public class IlcdAuthorizationInfo extends SimpleAuthorizationInfo implements AuthorizationInfo {

	/**
	 * Name of the user (<code>null</code> for guests)
	 */
	private final String userName;

	/**
	 * DAO to access users
	 */
	private final UserDao dao = new UserDao();

	/**
	 * The permissions
	 */
	private final Set<Permission> permissions = new HashSet<Permission>();

	/**
	 * Time stamp of last time permissions were loaded
	 */
	private long lastPermissionLoad = 0;

	/**
	 * Time stamp of last time permissions were changed
	 */
	private static long lastPermissionChange = System.currentTimeMillis();

	/**
	 * Create authorization info for for a user
	 * 
	 * @param userName
	 *            user name of user
	 */
	public IlcdAuthorizationInfo( String userName ) {
		this.userName = userName;
	}

	/**
	 * Create authorization info for guests
	 */
	public IlcdAuthorizationInfo() {
		this.userName = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Set<Permission> getObjectPermissions() {
		if ( this.lastPermissionLoad < IlcdAuthorizationInfo.lastPermissionChange ) {
			this.lastPermissionLoad = System.currentTimeMillis();
			this.loadPermissions();
		}
		return this.permissions;
	}

	/**
	 * Load the shiro wildcard string permissions
	 */
	private void loadPermissions() {
		this.permissions.clear();
		User user = null;
		try {
			if ( this.userName != null ) {
				user = this.dao.getUser( this.userName );
			}

			// stock access rights
			StockAccessRightDao sarDao = new StockAccessRightDao();
			List<StockAccessRight> lstSar;
			if ( user != null ) {
				// wildcard for super admin + skip the rest ;-)
				if ( user.isSuperAdminPermission() ) {
					this.permissions.add( new WildcardPermission( SecurityUtil.toWildcardString( "*", "*", "*" ) ) );
					return;
				}
				// load the access rights, there was no super admin group
				lstSar = sarDao.get( user );
			}
			else {
				lstSar = sarDao.get( 0L, null ); // guest
			}

			for ( StockAccessRight sar : lstSar ) {
				this.permissions.add( new WildcardPermission( SecurityUtil.toWildcardString( ProtectableType.STOCK.name(), ProtectionType.toTypesCSV( sar
						.getValue(), "," ), sar.getStockId() ) ) );
			}

			// rest is irrelevant to guests
			if ( user != null ) {
				// user / groups access for org admin
				OrganizationDao orgDao = new OrganizationDao();
				UserGroupDao ugDao = new UserGroupDao();

				// get all organizations that are being administrated by this user
				List<Organization> adminOrgs = orgDao.getAdministratedOrganizations( user );
				List<Long> orgUsrIds = null;
				if ( adminOrgs != null && !adminOrgs.isEmpty() ) {
					// user is org admin for at least one org, grant access to admin area
					this.permissions.add( new WildcardPermission( ProtectableType.ADMIN_AREA.name() ) );

					// user and group protection types array
					ProtectionType[] usrGrpProtectionTypes = new ProtectionType[] { ProtectionType.READ, ProtectionType.WRITE, ProtectionType.CREATE,
							ProtectionType.DELETE };

					// get IDs of all users and groups that are being assigned to the administrated organizations
					orgUsrIds = this.dao.getUserIds( adminOrgs );
					List<Long> orgGrpIds = ugDao.getGroupIds( adminOrgs );

					// and grant read/write permissions
					if ( orgUsrIds != null && !orgUsrIds.isEmpty() ) {
						this.permissions.add( new WildcardPermission( SecurityUtil.toWildcardString( ProtectableType.USER, usrGrpProtectionTypes, StringUtils
								.join( orgUsrIds, ',' ) ) ) );
					}
					if ( orgGrpIds != null && !orgGrpIds.isEmpty() ) {
						this.permissions.add( new WildcardPermission( SecurityUtil.toWildcardString( ProtectableType.GROUP, usrGrpProtectionTypes, StringUtils
								.join( orgGrpIds, ',' ) ) ) );
					}
				}

				// access to own user (profile) [if not already added by organization admin]
				if ( orgUsrIds == null || !orgUsrIds.contains( user.getId() ) ) {
					this.permissions.add( new WildcardPermission( SecurityUtil.toWildcardString( ProtectableType.USER, new ProtectionType[] {
							ProtectionType.READ, ProtectionType.WRITE }, user.getId().toString() ) ) );
				}
			}

		}
		catch ( Exception e ) {
			// Nop
		}
	}

	/**
	 * Call if anything is changed on the permissions (will trigger reload)
	 */
	public static void permissionsChanged() {
		IlcdAuthorizationInfo.lastPermissionChange = System.currentTimeMillis();
	}

}
