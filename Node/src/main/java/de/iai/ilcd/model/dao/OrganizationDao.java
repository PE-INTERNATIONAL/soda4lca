package de.iai.ilcd.model.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;

import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.model.security.UserGroup;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * DAO for organizations
 */
public class OrganizationDao extends AbstractLongIdObjectDao<Organization> {

	/**
	 * Create the DAO
	 */
	public OrganizationDao() {
		super( "Organization", Organization.class );
	}

	/**
	 * Get organization by name
	 * 
	 * @param name
	 *            name
	 * @return name
	 */
	public Organization getByName( String name ) {
		try {
			EntityManager em = PersistenceUtil.getEntityManager();
			return (Organization) em.createQuery( "SELECT a FROM " + this.getJpaName() + " a WHERE a.name=:name" ).setParameter( "name", name )
					.getSingleResult();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	/**
	 * Get the organizations that are being administrated by the provided user
	 * 
	 * @param u
	 *            user to get administrated organizations for
	 * @return list of administrated organizations
	 */
	@SuppressWarnings( "unchecked" )
	public List<Organization> getAdministratedOrganizations( User u ) {
		try {
			List<Long> lstGid = new ArrayList<Long>();
			for ( UserGroup g : u.getGroups() ) {
				lstGid.add( g.getId() );
			}

			EntityManager em = PersistenceUtil.getEntityManager();

			// due to possible NULL values in either columns, OR in WHERE part can result in wrong empty lists (even
			// with IS NOT NULL checks + UNION not supported by JPQL) => 2 queries required
			List<Organization> lstUsr = (List<Organization>) em.createQuery( "SELECT DISTINCT a FROM Organization a WHERE a.adminUser.id=:uid" ).setParameter(
					"uid", u.getId() ).getResultList();

			if ( !lstGid.isEmpty() ) {
				List<Organization> lstGrp = (List<Organization>) em.createQuery(
						"SELECT DISTINCT a FROM Organization a WHERE a.adminGroup.id IN(" + StringUtils.join( lstGid, ',' ) + ")" ).getResultList();

				for ( Organization o : lstGrp ) {
					if ( !lstUsr.contains( o ) ) {
						lstUsr.add( o );
					}
				}
			}
			return lstUsr;
		}
		catch ( Exception e ) {
			return null;
		}
	}

}
