package de.iai.ilcd.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * Utility class for access the configuration to the
 * application wide configuration settings
 */
public class DBConfigurationUtil {

	/**
	 * No instances, just static methods
	 */
	private DBConfigurationUtil() {
	}

	/**
	 * Update configuration for default data stock
	 * 
	 * @param id
	 *            ID of the default data stock
	 * @param isRoot
	 *            root data stock flag for default data stock
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	public static boolean setDefaultDataStock( long id, boolean isRoot ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		EntityTransaction t = em.getTransaction();
		try {
			t.begin();
			em.createNativeQuery(
					"UPDATE `configuration` SET `default_datastock_id`='" + Long.toString( id ) + "', `default_datastock_is_root`=" + (isRoot ? "1" : "0")
							+ " LIMIT 1" ).executeUpdate();
			t.commit();
			return true;
		}
		catch ( Exception e ) {
			t.rollback();
			return false;
		}
	}

}
