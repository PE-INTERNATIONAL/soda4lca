package de.iai.ilcd.security;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;

import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.model.security.UserGroup;

/**
 * <p>
 * Utility class to check for and work with access rights:
 * </p>
 * <ul>
 * <li>All <code>assert***(...)</code>-methods will throw an {@link AuthorizationException} if access shall not be
 * granted.</li>
 * <li>The <code>isPermitted(...)</code>-methods can be used for direct checking access rights (return boolean value)</li>
 * <li>The <code>toWildcardString(...)</code>-methods shall be used for creation of wildcard strings for shiro</li>
 * </ul>
 */
public class SecurityUtil {

	/**
	 * Assert that a user is logged in
	 */
	public static void assertIsLoggedIn() {
		Subject currentUser = SecurityUtils.getSubject();
		if ( currentUser == null || currentUser.getPrincipal() == null || StringUtils.isBlank( currentUser.getPrincipal().toString() ) ) {
			throw new AuthorizationException( "You must be logged in." );
		}
	}

	/**
	 * Assert that current user has import right on provided data stock
	 * 
	 * @param dsm
	 *            data stock meta
	 * @throws IllegalArgumentException
	 *             if invalid stock UUID
	 * @throws AuthorizationException
	 *             if access is not to be granted
	 */
	public static void assertCanImport( IDataStockMetaData dsm ) {
		if ( dsm == null ) {
			throw new IllegalArgumentException( "Invalid stock" );
		}
		SecurityUtil.assertCan( dsm, "You are not permitted to import data to this data stock.", ProtectionType.IMPORT );
	}

	/**
	 * Assert that current user has read right for provided data set,
	 * exception is being thrown otherwise
	 * 
	 * @param <T>
	 *            type of data set
	 * @param dataSet
	 *            data set to check for current user
	 * @throws AuthorizationException
	 *             if access is not to be granted
	 */
	public static <T extends DataSet> void assertCanRead( T dataSet ) throws AuthorizationException {
		SecurityUtil.assertCan( dataSet, "You are not permitted to read this data set", ProtectionType.READ );
	}

	/**
	 * Assert that current user has read right for provided data stock,
	 * exception is being thrown otherwise
	 * 
	 * @param dsm
	 *            data stock to check for current user
	 * @throws AuthorizationException
	 *             if access is not to be granted
	 */
	public static void assertCanRead( IDataStockMetaData dsm ) throws AuthorizationException {
		SecurityUtil.assertCan( dsm, "You are not permitted to read this data stock", ProtectionType.READ );
	}

	/**
	 * Assert that current user has write right for provided data set,
	 * exception is being thrown otherwise. <br />
	 * <b>Please note:</b> This only checks on permission level, no release management
	 * is being considered!
	 * 
	 * @param <T>
	 *            data set type
	 * @param dataSet
	 *            data set to check for current user
	 * @throws AuthorizationException
	 *             if access is not to be granted
	 */
	public static <T extends DataSet> void assertCanWrite( T dataSet ) throws AuthorizationException {
		SecurityUtil.assertCan( dataSet, "You are not permitted to write/delete this data set", ProtectionType.WRITE );
	}

	/**
	 * Assert that current user has export right for provided data set,
	 * exception is being thrown otherwise
	 * 
	 * @param <T>
	 *            data set type
	 * @param dataSet
	 *            data set to check for current user
	 * @throws AuthorizationException
	 *             if access is not to be granted
	 */
	public static <T extends DataSet> void assertCanExport( T dataSet ) throws AuthorizationException {
		SecurityUtil.assertCan( dataSet, "You are not permitted to export this data set", ProtectionType.EXPORT );
	}

	/**
	 * Assert that current user has provided right for provided data set,
	 * exception is being thrown otherwise
	 * 
	 * @param <T>
	 *            data set type
	 * @param dataSet
	 *            data set to check for current user
	 * @param canWhat
	 *            type of permissions to check
	 * @throws AuthorizationException
	 *             if access is not to be granted (generic message)
	 */
	public static <T extends DataSet> void assertCan( T dataSet, ProtectionType... canWhat ) throws AuthorizationException {
		SecurityUtil.assertCan( dataSet, "You are not permitted to perform this operation for this data set", canWhat );
	}

	/**
	 * Assert that current user has provided right for provided data set,
	 * exception is being thrown otherwise
	 * 
	 * @param <T>
	 *            data set type
	 * @param dataSet
	 *            data set to check for current user
	 * @param negativeMessage
	 *            message of exception if permission is not to be granted
	 * @param canWhat
	 *            type of permissions to check
	 * @throws AuthorizationException
	 *             if access is not to be granted (provided message)
	 */
	public static <T extends DataSet> void assertCan( T dataSet, String negativeMessage, ProtectionType... canWhat ) throws AuthorizationException {
		if ( !SecurityUtil.isPermitted( dataSet, canWhat ) ) {
			throw new AuthorizationException( negativeMessage );
		}
	}

	/**
	 * Assert that current user has provided right for provided data stock,
	 * exception is being thrown otherwise
	 * 
	 * @param <T>
	 *            data set type
	 * @param dsm
	 *            data stock meta to check for current user
	 * @param canWhat
	 *            type of permissions to check
	 * @throws AuthorizationException
	 *             if access is not to be granted (generic message)
	 */
	public static <T extends DataSet> void assertCan( IDataStockMetaData dsm, ProtectionType... canWhat ) throws AuthorizationException {
		SecurityUtil.assertCan( dsm, "You are not permitted to perform this operation for this data stock", canWhat );
	}

	/**
	 * Assert that current user has provided right for provided data stock,
	 * exception is being thrown otherwise
	 * 
	 * @param <T>
	 *            data set type
	 * @param dsm
	 *            data stock meta to check for current user
	 * @param negativeMessage
	 *            message of exception if permission is not to be granted
	 * @param canWhat
	 *            type of permissions to check
	 * @throws AuthorizationException
	 *             if access is not to be granted (provided message)
	 */
	public static <T extends DataSet> void assertCan( IDataStockMetaData dsm, String negativeMessage, ProtectionType... canWhat ) throws AuthorizationException {
		if ( dsm == null ) {
			throw new AuthorizationException( negativeMessage );
		}
		SecurityUtil.assertPermission( SecurityUtil.toWildcardString( ProtectableType.STOCK, canWhat, String.valueOf( dsm.getId() ) ), negativeMessage );
	}

	/**
	 * Determine if user can edit (has {@link ProtectionType#WRITE write} permission) on the provided user
	 * 
	 * @param u
	 *            user to check edit rights for
	 * @param negativeMessage
	 *            message for exception if user lacks edit permission
	 * @throws AuthorizationException
	 *             if permission is not present
	 */
	public static void assertCanWrite( User u, String negativeMessage ) throws AuthorizationException {
		if ( u != null ) {
			SecurityUtil.assertCanWriteUser( u.getId(), negativeMessage );
		}
		else {
			throw new AuthorizationException();
		}
	}

	/**
	 * Determine if user can edit (has {@link ProtectionType#WRITE write} permission) on the provided user id
	 * 
	 * @param uid
	 *            user id to check edit rights for
	 * @param negativeMessage
	 *            message for exception if user lacks edit permission
	 * @throws AuthorizationException
	 *             if permission is not present
	 */
	public static void assertCanWriteUser( Long uid, String negativeMessage ) throws AuthorizationException {
		SecurityUtil.assertPermission( SecurityUtil.toWildcardString( ProtectableType.USER, ProtectionType.WRITE, String.valueOf( uid ) ), negativeMessage );
	}

	/**
	 * Determine if user has super admin permission
	 * 
	 * @throws AuthorizationException
	 *             if permission is not present
	 */
	public static void assertSuperAdminPermission() throws AuthorizationException {
		if ( !SecurityUtil.hasSuperAdminPermission() ) {
			throw new AuthorizationException( "Super Admin Right required" );
		}
	}

	/**
	 * Assert that the current user has admin rights (is {@link Organization#getAdminUser() admin user} or
	 * in the {@link Organization#getAdminGroup() admin group}) for the provided organization.
	 * 
	 * @param o
	 *            organization to check for
	 */
	public static void assertIsOrganizationAdmin( Organization o ) {
		final boolean sadmPermission = SecurityUtil.hasSuperAdminPermission();
		if ( sadmPermission ) {
			return;
		}
		if ( o == null ) {
			if ( !sadmPermission ) {
				throw new AuthorizationException( "Super Admin Right required" );
			}
			else {
				return;
			}
		}
		// no guests!
		SecurityUtil.assertIsLoggedIn();

		String username = SecurityUtils.getSubject().getPrincipal().toString();
		if ( o.getAdminUser() != null && username.equals( o.getAdminUser().getUserName() ) ) {
			return;
		}
		final UserGroup admGroup = o.getAdminGroup();
		if ( admGroup != null ) {
			final UserAccessBean uab = new UserAccessBean();
			final User user = uab.getUserObject();
			final List<UserGroup> userGroups = user.getGroups();
			if ( CollectionUtils.isNotEmpty( userGroups ) ) {
				if ( userGroups.contains( admGroup ) ) {
					return;
				}
			}
		}
		throw new AuthorizationException( "No admin right for this organization" );
	}

	/**
	 * Check a permission
	 * 
	 * @param permission
	 *            permission to check
	 * @param negativeMessage
	 *            message for exception if user lacks permission
	 * @throws AuthorizationException
	 *             if permission is not present
	 */
	public static void assertPermission( String permission, String negativeMessage ) throws AuthorizationException {
		Subject sub = SecurityUtils.getSubject();
		if ( sub != null ) {
			if ( !sub.isPermitted( permission ) ) {
				throw new AuthorizationException( negativeMessage );
			}
		}
		else {
			throw new AuthorizationException();
		}
	}

	/**
	 * Determine if user shall be able to enter admin area
	 * 
	 * @return <code>true</code> if access shall be granted, <code>false</code> otherwise
	 */
	public static boolean hasAdminAreaAccessRight() {
		return SecurityUtil.isPermitted( ProtectableType.ADMIN_AREA.name() );
	}

	/**
	 * Determine if user is super admin
	 * 
	 * @return <code>true</code> if super admin access shall be granted, <code>false</code> otherwise
	 */
	public static boolean hasSuperAdminPermission() {
		return SecurityUtil.isPermitted( "*:*:*" );
	}

	/**
	 * Determine if user has {@link ProtectionType#EXPORT export} permission for provided data set
	 * 
	 * @param ds
	 *            data set to check for
	 * @return <code>true</code> if export allowed, <code>false</code> otherwise
	 */
	public static boolean hasExportPermission( DataSet ds ) {
		return SecurityUtil.isPermitted( ds, ProtectionType.EXPORT );
	}

	/**
	 * Determine if user has {@link ProtectionType#IMPORT import} permission for provided data stock
	 * 
	 * @param dsm
	 *            data stock meta
	 * @return <code>true</code> if import allowed, <code>false</code> otherwise
	 */
	public static boolean hasImportPermission( IDataStockMetaData dsm ) {
		return SecurityUtil.isPermitted( SecurityUtil.toWildcardString( ProtectableType.STOCK, ProtectionType.IMPORT, dsm.getId().toString() ) );
	}

	/**
	 * Check if a permission is present to use provided protection types
	 * (checks all data stocks of provided data set).
	 * 
	 * @param dataSet
	 *            data set to check for current user
	 * @param canWhat
	 *            type(s) to check
	 * @return <code>true</code> if permission shall be granted, <code>false</code> otherwise
	 */
	private static <T extends DataSet> boolean isPermitted( T dataSet, ProtectionType... canWhat ) {
		// 1st: empty canWhan = error (therefore return false)
		if ( canWhat == null || canWhat.length == 0 ) {
			return false;
		}
		// 2nd: root data stock
		if ( SecurityUtil.isPermitted( SecurityUtil.toWildcardString( ProtectableType.STOCK, canWhat, dataSet.getRootDataStock().getId().toString() ) ) ) {
			return true;
		}
		// 3rd: all normal data stocks
		final Set<DataStock> dsList = dataSet.getContainingDataStocks();
		if ( dsList != null && !dsList.isEmpty() ) {
			for ( DataStock ds : dsList ) {
				if ( SecurityUtil.isPermitted( SecurityUtil.toWildcardString( ProtectableType.STOCK, canWhat, ds.getId().toString() ) ) ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check a permission
	 * 
	 * @param permission
	 *            permission to check (wildcard string)
	 * @return <code>true</code> if permission shall be granted, <code>false</code> otherwise
	 */
	private static boolean isPermitted( String permission ) {
		Subject sub = SecurityUtils.getSubject();
		if ( sub != null ) {
			if ( !sub.isPermitted( permission ) ) {
				return false;
			}
		}
		else {
			return false;
		}
		return true;
	}

	/**
	 * Convert to Shiro wildcard string
	 * 
	 * @param protectableType
	 *            the protectable type to grant access to
	 * @param protectionType
	 *            the protection type to grant access to as string
	 * @param vals
	 *            identifier of objects to grant access to
	 * @return Shiro wildcard string
	 */
	public static String toWildcardString( String protectableType, String protectionType, String vals ) {
		StringBuilder sb = new StringBuilder( protectableType ).append( ":" ).append( protectionType );
		if ( vals != null ) {
			sb.append( ":" ).append( vals );
		}

		String res = sb.toString();

		return res;
	}

	/**
	 * Convert to Shiro wildcard string
	 * 
	 * @param protectableType
	 *            the protectable type to grant access to
	 * @param protectionType
	 *            the protection types to grant access to
	 * @param vals
	 *            identifier of objects to grant access to
	 * @return Shiro wildcard string
	 */
	public static String toWildcardString( ProtectableType protectableType, ProtectionType[] protectionType, String vals ) {
		return SecurityUtil.toWildcardString( protectableType.name(), StringUtils.join( protectionType, "," ), vals );
	}

	/**
	 * Convert to Shiro wildcard string
	 * 
	 * @param protectableType
	 *            the protectable type to grant access to
	 * @param protectionType
	 *            the protection type to grant access to
	 * @param vals
	 *            identifier of objects to grant access to
	 * @return Shiro wildcard string
	 */
	public static String toWildcardString( ProtectableType protectableType, ProtectionType protectionType, String vals ) {
		return SecurityUtil.toWildcardString( protectableType.name(), protectionType.name(), vals );
	}

	/**
	 * Convert to Shiro wildcard string
	 * 
	 * @param protectableType
	 *            the protectable type to grant access to
	 * @param protectionType
	 *            the protection type to grant access to
	 * @param id
	 *            identifier of objects to grant access to
	 * @return Shiro wildcard string
	 */
	public static String toWildcardString( String protectableType, String protectionType, long id ) {
		return SecurityUtil.toWildcardString( protectableType, protectionType, Long.toString( id ) );
	}

}
