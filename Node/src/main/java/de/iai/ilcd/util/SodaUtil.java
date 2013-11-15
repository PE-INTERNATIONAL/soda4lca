package de.iai.ilcd.util;

import java.util.ResourceBundle;

import org.primefaces.model.DualListModel;

import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.model.security.UserGroup;

/**
 * Common utility methods and constants
 */
public class SodaUtil {

	/**
	 * Constant for menu highlighting switch for home / index
	 */
	public final static String MENU_HOME = "home";

	/**
	 * Constant for menu highlighting switch for process
	 */
	public final static String MENU_PROCESS = "proc";

	/**
	 * Constant for menu highlighting switch for LCIA method
	 */
	public final static String MENU_LCIAMETHOD = "lciam";

	/**
	 * Constant for menu highlighting switch for flow
	 */
	public final static String MENU_FLOW = "flow";

	/**
	 * Constant for menu highlighting switch for flow property
	 */
	public final static String MENU_FLOWPROPERTY = "fp";

	/**
	 * Constant for menu highlighting switch for unit group
	 */
	public final static String MENU_UNITGROUP = "ug";

	/**
	 * Constant for menu highlighting switch for source
	 */
	public final static String MENU_SOURCE = "src";

	/**
	 * Constant for menu highlighting switch for source
	 */
	public final static String MENU_CONTACT = "con";

	/**
	 * Constant for menu highlighting switch for process search
	 */
	public final static String MENU_SEARCHPROCESS = "sproc";

	/**
	 * ID of the built-in admin user (super admin) (ID: 1)
	 */
	public final static Long ADMIN_ID = 1L;

	/**
	 * ID of the built-in default organization (ID: 1)
	 */
	public final static Long DEFAULT_ORGANIZATION_ID = 1L;

	/**
	 * ID of the built-in default root stock (ID: 1)
	 */
	public final static Long DEFAULT_ROOTSTOCK_ID = 1L;

	/**
	 * Determine if the total count of elements (count source + count target) is equal
	 * for both provided {@link DualListModel}s
	 * 
	 * @param <T>
	 *            type of list model entries
	 * @param l1
	 *            first {@link DualListModel}
	 * @param l2
	 *            second {@link DualListModel}
	 * @return <code>true</code> if total count of elements is equal, <code>false</code> otherwise
	 */
	public static <T> boolean isTotalElementCountEqual( DualListModel<T> l1, DualListModel<T> l2 ) {
		int cntL1 = 0;
		int cntL2 = 0;
		if ( l1 != null ) {
			cntL1 = l1.getSource().size() + l1.getTarget().size();
		}
		if ( l2 != null ) {
			cntL2 = l2.getSource().size() + l2.getTarget().size();
		}
		return cntL1 == cntL2;
	}

	/**
	 * Print thread info (calling method + thread name/id shown)
	 */
	public static void threadInfo() {
		final Thread t = Thread.currentThread();
		StackTraceElement[] elements = t.getStackTrace();
		for ( StackTraceElement e : elements ) {
			if ( !e.getClassName().equals( SodaUtil.class.getName() ) && !e.getClassName().equals( Thread.class.getName() ) ) {
				System.out.println( e.getClassName() + "#" + e.getMethodName() + "(..); Thread: " + t.getName() + " / id=" + t.getId() + " (" + e.getFileName()
						+ ":" + e.getLineNumber() + ")" );
				return;
			}
		}
	}

	/**
	 * Sysout with thread info and calling method
	 * 
	 * @param s
	 *            string to print
	 */
	public static void sysout( String s ) {
		final Thread t = Thread.currentThread();
		StackTraceElement[] elements = t.getStackTrace();
		for ( StackTraceElement e : elements ) {
			if ( !e.getClassName().equals( SodaUtil.class.getName() ) && !e.getClassName().equals( Thread.class.getName() ) ) {
				System.out.println( s + "\n\t" + e.getClassName() + "#" + e.getMethodName() + "(..); Thread: " + t.getName() + " / id=" + t.getId() + " ("
						+ e.getFileName() + ":" + e.getLineNumber() + ")\n" );
				return;
			}
		}
	}

	/**
	 * Helper method to print a language-aware hint, if the given group is an admin group
	 * 
	 * @param g
	 *            the group to check for admin group
	 * @param o
	 *            the organization to check with
	 * @param rb
	 *            ResourceBundle
	 * @return String containing <i>(Admin-Gruppe)</i> or <i>(Admin group)</i>
	 */
	public static String adminHint( UserGroup g, Organization o, ResourceBundle rb ) {
		if ( o.hasAdminGroup() ) {
			return (g.getId() == o.getAdminGroup().getId() ? " (" + rb.getString( "admin.org.adminGroup" ) + ")" : "");
		}
		else {
			return "";
		}
	}

	/**
	 * Helper method to print a language-aware hint, if the given user is an admin user
	 * 
	 * @param u
	 *            the user to check for admin user
	 * @param o
	 *            the organization to check with
	 * @param rb
	 *            ResourceBundle
	 * @return String containing <i>(Admin-Benutzer)</i> or <i>(Admin user)</i>
	 */
	public static String adminHint( User u, Organization o, ResourceBundle rb ) {
		if ( o.hasAdmin() ) {
			return (u.getId() == o.getAdminUser().getId() ? " (" + rb.getString( "admin.org.adminUser" ) + ")" : "");
		}
		else {
			return "";
		}
	}

	/**
	 * Helper method to print a language-aware hint, if the given group has no users
	 * 
	 * @param g
	 *            the group to check for users
	 * @param rb
	 *            ResourceBundle
	 * @return String containing <i>(0 Benutzer)</i> or <i>(0 User)</i>
	 */
	public static String noUsersHint( UserGroup g, ResourceBundle rb ) {
		return (!g.hasUsers() ? " (0 " + rb.getString( "admin.user" ) + ")" : "");
	}
}
