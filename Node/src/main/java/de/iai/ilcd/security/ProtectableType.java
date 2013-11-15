package de.iai.ilcd.security;

import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * Enum with all types that can be protected via Shiro permission system (first part for {@link WildcardPermission} )
 */
public enum ProtectableType {

	/**
	 * Data stock (Root and non root data stocks are considered equal for permissions)
	 */
	STOCK,
	/**
	 * Users
	 */
	USER,
	/**
	 * Groups
	 */
	GROUP,
	/**
	 * Admin area
	 */
	ADMIN_AREA;
}
