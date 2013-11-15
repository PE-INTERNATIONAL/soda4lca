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

package de.iai.ilcd.model.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.model.datastock.AbstractDataStock;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.model.datastock.RootDataStock;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.persistence.PersistenceUtil;
import de.iai.ilcd.security.ProtectionType;
import de.iai.ilcd.security.StockAccessRight;
import de.iai.ilcd.security.StockAccessRightDao;

/**
 * DAO for data stocks
 */
public class CommonDataStockDao extends AbstractLongIdObjectDao<AbstractDataStock> {

	/**
	 * Create the DAO
	 * 
	 * @param typeClass
	 *            Type class of data stock
	 * @param jpaName
	 *            Class name in JPA
	 */
	public CommonDataStockDao() {
		super( "AbstractDataStock", AbstractDataStock.class );
	}

	/**
	 * Get a data stock by it's database ID
	 * 
	 * @param id
	 *            id of data stock to find
	 * @return loaded data stocks
	 */
	public AbstractDataStock getDataStockById( long id ) {
		return this.getById( id );
	}

	/**
	 * Get a root data stock by it's database ID
	 * 
	 * @param id
	 *            id of data stock to find
	 * @return loaded data stock
	 */
	public RootDataStock getRootDataStockById( long id ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.find( RootDataStock.class, new Long( id ) );
	}

	/**
	 * Get a root data stock by it's database ID
	 * 
	 * @param id
	 *            id of data stock to find
	 * @return loaded data stock
	 */
	public DataStock getNonRootDataStockById( long id ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.find( DataStock.class, new Long( id ) );
	}

	/**
	 * Get a root data stock by it's database ID
	 * 
	 * @param id
	 *            id of data stock to find
	 * @return loaded data stock
	 */
	public RootDataStock getRootDataStockById( String id ) {
		try {
			return this.getRootDataStockById( Long.parseLong( id ) );
		}
		catch ( NumberFormatException e ) {
			return null;
		}
	}

	/**
	 * Get a root data stock by it's database ID
	 * 
	 * @param id
	 *            id of data stock to find
	 * @return loaded data stock
	 */
	public DataStock getNonRootDataStockById( String id ) {
		try {
			return this.getNonRootDataStockById( Long.parseLong( id ) );
		}
		catch ( NumberFormatException e ) {
			return null;
		}
	}

	/**
	 * Get a data stock by name
	 * 
	 * @param name
	 *            name of data stock to load
	 * @return
	 */
	public AbstractDataStock getDataStockByName( String name ) {
		return this.getDataStockByName( name, this.getJpaName(), this.getAccessedClass() );
	}

	/**
	 * Get a root data stock by name
	 * 
	 * @param name
	 *            name of data stock to load
	 * @return loaded root data stock
	 */
	public RootDataStock getRootDataStockByName( String name ) {
		return this.getDataStockByName( name, "RootDataStock", RootDataStock.class );
	}

	/**
	 * Get a root data stock by name
	 * 
	 * @param name
	 *            name of data stock to load
	 * @return loaded root data stock
	 */
	public DataStock getNonRootDataStockByName( String name ) {
		return this.getDataStockByName( name, "DataStock", DataStock.class );
	}

	/**
	 * Get a data stock by name
	 * 
	 * @param name
	 *            name of data stock to load
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	private <T extends AbstractDataStock> T getDataStockByName( String name, String jpaName, Class<T> type ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		T stock = null;
		try {
			stock = (T) em.createQuery( "SELECT a FROM " + jpaName + " a WHERE a.name=:name" ).setParameter( "name", name ).getSingleResult();
		}
		catch ( NoResultException e ) {
			// do nothing; we just return null
		}
		return stock;
	}

	/**
	 * Get a data stock by name
	 * 
	 * @param name
	 *            name of data stock to load
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public <T extends AbstractDataStock> T getDataStockByUuid( String uuid ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		T stock = null;
		try {
			stock = (T) em.createQuery( "SELECT a FROM " + this.getJpaName() + " a WHERE a.uuid.uuid=:uuid" ).setParameter( "uuid", uuid ).getSingleResult();
		}
		catch ( NoResultException e ) {
			// do nothing; we just return null
		}
		return stock;
	}

	/**
	 * Delete a data stock
	 * 
	 * @param stock
	 *            stock to delete
	 * @throws Exception
	 * @deprecated use {@link #remove(AbstractDataStock)}
	 */
	@Deprecated
	public void delete( AbstractDataStock stock ) throws Exception {
		super.remove( stock );
	}

	/**
	 * Get all readable data stocks
	 * 
	 * @param uid
	 *            user ID
	 * @param gids
	 *            group IDs
	 * @return list of all readable data stocks
	 */
	public List<AbstractDataStock> getAllReadable( long uid, List<Long> gids ) {
		StockAccessRightDao sarDao = new StockAccessRightDao();
		return this.getAllReadable( sarDao.get( uid, gids ) );
	}

	/**
	 * Get all readable data stocks
	 * 
	 * @param user
	 *            user to get for
	 * @return list of all readable data stocks
	 */
	public List<AbstractDataStock> getAllReadable( User u ) {
		StockAccessRightDao sarDao = new StockAccessRightDao();
		return this.getAllReadable( sarDao.get( u ) );
	}

	/**
	 * Get all readable data stocks
	 * 
	 * @param lstSar
	 *            list of stock access rights
	 * @return list of all readable data stocks
	 */
	@SuppressWarnings( "unchecked" )
	private List<AbstractDataStock> getAllReadable( List<StockAccessRight> lstSar ) {
		List<Long> readableStockIds = new ArrayList<Long>();
		for ( StockAccessRight sar : lstSar ) {
			if ( ProtectionType.READ.isContained( sar.getValue() ) ) {
				readableStockIds.add( sar.getStockId() );
			}
		}

		if ( readableStockIds.isEmpty() ) {
			return Collections.emptyList();
		}
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery( "select a from " + this.getJpaName() + " a WHERE a.id IN(" + StringUtils.join( readableStockIds, "," ) + ")" ).getResultList();
	}
}
