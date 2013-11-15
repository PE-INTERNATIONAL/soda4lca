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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.generic.ValueParser;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzk.iai.ilcd.service.model.IDataSetListVO;
import de.fzk.iai.ilcd.service.model.IDataSetVO;
import de.fzk.iai.ilcd.service.model.ILCIAMethodListVO;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.model.utils.DistributedSearchLog;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * Common implementation for data set DAO objects
 * 
 * @param <T>
 *            Element type that is accessed by this DAO
 * @param <L>
 *            ListVO interface type for the access
 */
public abstract class DataSetDao<T extends DataSet, L extends IDataSetListVO, D extends IDataSetVO> extends AbstractLongIdObjectDao<T> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger( DataSetDao.class );

	/**
	 * The class accessed by this DAO
	 */
	private final Class<L> listVOClass;

	/**
	 * The class accessed by this DAO
	 */
	private final Class<D> voClass;

	/**
	 * The data set type
	 */
	private final DataSetType dataSetType;

	/**
	 * Create a data set DAO
	 * 
	 * @param jpaName
	 *            the name in JPA of class
	 * @param accessedClass
	 *            the class accessed by this DAO
	 * @param listVOClass
	 *            the interface based on {@link ILCIAMethodListVO} for the accessed class
	 */
	public DataSetDao( String jpaName, Class<T> accessedClass, Class<L> listVOClass, Class<D> voClass ) {
		super( jpaName, accessedClass );
		this.listVOClass = listVOClass;
		this.voClass = voClass;
		this.dataSetType = DataSetType.fromClass( accessedClass );
	}

	/**
	 * Get <code>pageSize</code> data sets starting on <code>startIndex</code>
	 * 
	 * @param startIndex
	 * @param pageSize
	 * @return list of matching elements
	 */
	public List<T> getDataSets( int startIndex, int pageSize ) {
		return super.get( startIndex, pageSize );
	}

	/**
	 * Get data set by UUID string and version
	 * 
	 * @param uuid
	 *            the UUID string
	 * @param version
	 *            the version of the method
	 * @return data set for the specified UUID string/version, or <code>null</code> if none found
	 */
	@SuppressWarnings( "unchecked" )
	public T getByUuidAndVersion( String uuid, DataSetVersion version ) {
		try {
			EntityManager em = PersistenceUtil.getEntityManager();
			Query q = em.createQuery( "select a from " + this.getJpaName()
					+ " a where a.uuid.uuid=:uuid and a.version.majorVersion=:major and a.version.minorVersion=:minor and a.version.subMinorVersion=:subMinor" );
			q.setParameter( "uuid", uuid );
			q.setParameter( "major", version.getMajorVersion() );
			q.setParameter( "minor", version.getMinorVersion() );
			q.setParameter( "subMinor", version.getSubMinorVersion() );
			return (T) q.getSingleResult();
		}
		catch ( NoResultException e ) {
			// none found, so let's return null
			return null;
		}
	}

	/**
	 * Get a the most recent data set of type T by UUID string
	 * 
	 * @param uuid
	 *            the UUID string
	 * @return data set of type T for the specified UUID string, or <code>null</code> if none found
	 */
	@SuppressWarnings( "unchecked" )
	public T getByUuid( String uuid ) {
		try {
			EntityManager em = PersistenceUtil.getEntityManager();
			Query q = em.createQuery( "select a from " + this.getJpaName()
					+ " a where a.uuid.uuid=:uuid order by a.version.majorVersion desc, a.version.minorVersion desc, a.version.subMinorVersion desc" );
			q.setParameter( "uuid", uuid );
			List<T> results = q.getResultList();
			if ( results.size() > 0 ) {
				return results.get( 0 );
			}
			else {
				return null;
			}
		}
		catch ( NoResultException e ) {
			// none found, so let's return null
			return null;
		}
	}

	protected abstract String getDataStockField();

	public List<T> get( IDataStockMetaData dataStock, int startIndex, int pageSize ) {
		if ( dataStock.isRoot() ) {
			return this.getDatasetsFromRootDs( dataStock, startIndex, pageSize );
		}
		else {
			return this.getDatasetsFromNonRootDs( dataStock, startIndex, pageSize );
		}
	}

	public long getCount( IDataStockMetaData dataStock ) {
		if ( dataStock.isRoot() ) {
			return this.getDatasetsFromRootDsCount( dataStock );
		}
		else {
			return this.getDatasetsFromNonRootDsCount( dataStock );
		}
	}

	private List<T> getDatasetsFromNonRootDs( IDataStockMetaData dataStock, int startIndex, int pageSize ) {
		final String dsField = this.getDataStockField();
		StringBuilder sb = new StringBuilder( "SELECT b FROM DataStock a LEFT JOIN a." ).append( dsField ).append( " b WHERE a.id=:dsId" );

		EntityManager em = PersistenceUtil.getEntityManager();
		Query query = em.createQuery( sb.toString() );

		query.setParameter( "dsId", dataStock.getId() );
		query.setFirstResult( startIndex );
		query.setMaxResults( pageSize );

		return query.getResultList();
	}

	private long getDatasetsFromNonRootDsCount( IDataStockMetaData dataStock ) {
		final String dsField = this.getDataStockField();

		StringBuilder sb = new StringBuilder( "SELECT COUNT(b) FROM DataStock a LEFT JOIN a." ).append( dsField ).append( " b WHERE a.id=:dsId" );

		EntityManager em = PersistenceUtil.getEntityManager();
		Query query = em.createQuery( sb.toString() );

		query.setParameter( "dsId", dataStock.getId() );

		Long resultCount = (Long) query.getSingleResult();

		return resultCount.longValue();
	}

	private List<T> getDatasetsFromRootDs( IDataStockMetaData dataStock, int startIndex, int pageSize ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		Query query = em.createQuery( "SELECT a FROM " + this.getJpaName() + " a WHERE a.rootDataStock.id=:dsId" );

		query.setParameter( "dsId", dataStock.getId() );
		query.setFirstResult( startIndex );
		query.setMaxResults( pageSize );

		return query.getResultList();
	}

	private long getDatasetsFromRootDsCount( IDataStockMetaData dataStock ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		Query query = em.createQuery( "SELECT COUNT(a) FROM " + this.getJpaName() + " a WHERE a.rootDataStock.id=:dsId" );

		query.setParameter( "dsId", dataStock.getId() );

		Long resultCount = (Long) query.getSingleResult();

		return resultCount.longValue();
	}

	/**
	 * Get all other versions of this data set
	 * 
	 * @return version instances of all version numbers that are available for the uuid of this data set except the
	 *         current one
	 */
	@SuppressWarnings( "unchecked" )
	public List<T> getOtherVersions( T current ) {
		if ( current == null ) {
			return null;
		}

		final String uuid = current.getUuidAsString();
		final DataSetVersion v = current.getVersion();

		if ( uuid == null || v == null ) {
			return null;
		}

		try {
			EntityManager em = PersistenceUtil.getEntityManager();
			Query q = em
					.createQuery( "select a from "
							+ this.getJpaName()
							+ " a WHERE a.uuid.uuid=:uuid AND (a.version.majorVersion<>:major OR a.version.minorVersion<>:minor OR a.version.subMinorVersion<>:subMinor) ORDER BY a.version.majorVersion desc, a.version.minorVersion desc, a.version.subMinorVersion desc" );

			q.setParameter( "uuid", uuid );
			q.setParameter( "major", v.getMajorVersion() );
			q.setParameter( "minor", v.getMinorVersion() );
			q.setParameter( "subMinor", v.getSubMinorVersion() );

			return q.getResultList();
		}
		catch ( NoResultException e ) {
			return null;
		}
	}

	/**
	 * Get the data set of type T by JPA id
	 * 
	 * @param id
	 *            id of the data set
	 * @return data set of type T by JPA id
	 */
	public T getByDataSetId( long id ) {
		return super.getById( id );
	}

	/**
	 * Get the data set of type T by JPA id
	 * 
	 * @param id
	 *            id of the data set as string
	 * @return data set of type T by JPA id
	 */
	public T getByDataSetId( String id ) {
		return super.getById( id );
	}

	/**
	 * Get the top classes for the data set type T
	 * 
	 * @return top classes for the data set type T
	 */
	public List<String> getTopClasses() {
		ClassificationDao classDao = new ClassificationDao();
		return classDao.getTopClasses( this.dataSetType );
	}

	/**
	 * Get the sub classes for provided class name and level
	 * 
	 * @param className
	 *            class name
	 * @param level
	 *            level
	 * @return sub classes for provided class name and level
	 */
	@SuppressWarnings( "unchecked" )
	public List<String> getSubClasses( String className, String level ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		Query q = em.createQuery( "select distinct cl.name from " + this.getJpaName()
				+ " a join a.classification.classes cl join a.classification.classes cl2 where cl.level=:level and cl2.name=:className order by cl.name" );
		q.setParameter( "level", Integer.parseInt( level ) );
		q.setParameter( "className", className );

		return q.getResultList();
	}

	/**
	 * Get data sets of type T by main class
	 * 
	 * @param mainClass
	 *            the main class to get data sets by
	 * @return data sets of type T by main class
	 */
	@SuppressWarnings( "unchecked" )
	public List<T> getByClass( String mainClass ) {
		return this.getByClassQuery( mainClass, false ).getResultList();
	}

	/**
	 * Get number of data sets of type T by main class
	 * 
	 * @param mainClass
	 *            the main class to get data sets by
	 * @return number of data sets of type T by main class
	 */
	public long getNumberByClass( String mainClass ) {
		return (Long) this.getByClassQuery( mainClass, true ).getSingleResult();
	}

	/**
	 * Create query (with set parameters) for {@link #getByClass(String)} and {@link #getNumberByClass(String)}
	 * 
	 * @param mainClass
	 *            the main class to get data sets by
	 * @param count
	 *            do count query or not
	 * @return query (with set parameters) for {@link #getByClass(String)} and {@link #getNumberByClass(String)}
	 */
	private Query getByClassQuery( String mainClass, boolean count ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery(
				"select " + (count ? "count(a)" : "a") + " from " + this.getJpaName()
						+ " a join a.classification.classes cl where cl.level=0 and cl.name=:className" ).setParameter( "className", mainClass );
	}

	/**
	 * Get data sets of type T by main and sub class
	 * 
	 * @param mainClass
	 *            the main class to get data sets by
	 * @param subClass
	 *            the sub class to get data sets by
	 * @return data sets of type T by main and sub class
	 */
	@SuppressWarnings( "unchecked" )
	public List<T> getByClass( String mainClass, String subClass ) {
		return this.getByClassQuery( mainClass, subClass, false ).getResultList();
	}

	/**
	 * Get data sets of type T by main and sub class
	 * 
	 * @param mainClass
	 *            the main class to get data sets by
	 * @param subClass
	 *            the sub class to get data sets by
	 * @return data sets of type T by main and sub class
	 */
	public long getNumberByClass( String mainClass, String subClass ) {
		return (Long) this.getByClassQuery( mainClass, subClass, true ).getSingleResult();
	}

	/**
	 * Create query (with set parameters) for {@link #getByClass(String,String)} and
	 * {@link #getNumberByClass(String,String)}
	 * 
	 * @param mainClass
	 *            the main class to get data sets by
	 * @param subClass
	 *            the sub class to get data sets by
	 * @param count
	 *            do count query or not
	 * @return query (with set parameters) for {@link #getByClass(String,String)} and
	 *         {@link #getNumberByClass(String,String)}
	 */
	private Query getByClassQuery( String mainClass, String subClass, boolean count ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em
				.createQuery(
						"select "
								+ (count ? "count(a)" : "a")
								+ " from "
								+ this.getJpaName()
								+ " a join a.classification.classes cl join a.classification.classes cl2 where cl.level=0 and cl.name=:mainClass and cl2.level=1 and cl2.name=:subClass" )
				.setParameter( "mainClass", mainClass ).setParameter( "subClass", subClass );
	}

	/**
	 * Search for data sets of type T
	 * 
	 * @param params
	 *            parameter
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * @return found data sets of type T
	 */
	public List<L> search( ValueParser params, boolean mostRecentVersionOnly, IDataStockMetaData[] stocks ) {
		return this.search( this.listVOClass, params, mostRecentVersionOnly, stocks );
	}

	/**
	 * Search for data sets of type T
	 * 
	 * @param params
	 *            parameter
	 * @param startIndex
	 *            start index for query
	 * @param pageSize
	 *            count of result items
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * @return found data sets of type T
	 */
	public List<L> search( ValueParser params, int startIndex, int pageSize, boolean mostRecentVersionOnly, IDataStockMetaData[] stocks ) {
		return this.search( this.listVOClass, params, startIndex, pageSize, null, mostRecentVersionOnly, stocks );
	}

	/**
	 * Invoked at beginning of {@link #checkAndPersist(DataSet, PersistType, PrintWriter)} to manipulate data prior to
	 * persisting
	 * 
	 * @param dataSet
	 *            data set to manipulate
	 * @see #checkAndPersist(DataSet, PersistType, PrintWriter)
	 */
	protected abstract void preCheckAndPersist( T dataSet );

	/**
	 * Generic persist method for data set objects.
	 * 
	 * @param dataSet
	 *            data set to persist
	 * @param pType
	 *            which type of persistence operation, new, update (i.e. overwrite existing data set), ...
	 * @param out
	 *            PrintWriter to log error messages which can be presented to the end user
	 * @return true if persist is successful, false otherwise
	 * @see #preCheckAndPersist(DataSet)
	 */
	public boolean checkAndPersist( T dataSet, PersistType pType, PrintWriter out ) {
		// TODO: check if version shall be excluded for some types

		if ( dataSet.getRootDataStock() == null ) {
			out.println( "Error: root data stock must not be null!" );
			return false;
		}

		// perform pre-persist actions
		this.preCheckAndPersist( dataSet );

		EntityManager em = PersistenceUtil.getEntityManager();

		// locate existing method by UUID
		T existingDataSet = this.getByUuidAndVersion( dataSet.getUuidAsString(), dataSet.getVersion() );

		// if existing found ...
		if ( existingDataSet != null ) {
			// ... and mode is set to only new ...
			if ( PersistType.ONLYNEW.equals( pType ) ) {
				if ( out != null ) {
					out.println( "Warning: " + this.getAccessedClass().getSimpleName() + " data set with uuid " + dataSet.getUuidAsString() + " and version "
							+ dataSet.getVersion().getVersionString() + " already exists in database; will ignore this data set." );
				}
				// ... just ignore it
				return false;
			}
		}

		EntityTransaction t = em.getTransaction();
		// now the DB interaction
		try {
			t.begin();
			// delete existing for merge operation
			if ( existingDataSet != null && PersistType.MERGE.equals( pType ) ) {
				if ( out != null ) {
					out.println( "Notice: " + this.getAccessedClass().getSimpleName() + " method data set with uuid " + existingDataSet.getUuidAsString()
							+ " and version " + existingDataSet.getVersion().getVersionString()
							+ " already exists in database; will replace it with this data set" );
				}
				em.remove( existingDataSet );
			}
			// persist the new method
			em.persist( dataSet );

			// actual write to DB
			t.commit();

			// set the most recent version flags correctly
			if ( !this.setMostRecentVersionFlags( dataSet.getUuidAsString() ) ) {
				return false;
			}

			// and return with success :)
			return true;
		}
		catch ( Exception e ) {
			DataSetDao.LOGGER.error( "Cannot persist " + this.getAccessedClass().getSimpleName() + " data set with uuid {}", dataSet.getUuidAsString() );
			DataSetDao.LOGGER.error( "Exception is: ", e );
			if ( out != null ) {
				out.println( "Error: " + this.getAccessedClass().getSimpleName() + " data set with uuid " + dataSet.getUuidAsString() + " and version "
						+ dataSet.getVersion().getVersionString() + " could not be saved into database; unknown database error; Exception message: "
						+ e.getMessage() );
			}
			t.rollback();
		}

		return false;
	}

	/**
	 * Set the {@link DataSet#setMostRecentVersion(boolean) most recent version flags} for all data sets with the
	 * specified uuid. Please note that this method
	 * <strong>does not open a separate transaction</strong>.
	 * 
	 * @param uuid
	 *            uuid of the data sets
	 */
	@SuppressWarnings( "unchecked" )
	protected boolean setMostRecentVersionFlags( String uuid ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		EntityTransaction t = em.getTransaction();
		try {
			Query q = em.createQuery( "SELECT a FROM " + this.getJpaName()
					+ " a WHERE a.uuid.uuid=:uuid ORDER BY a.version.majorVersion desc, a.version.minorVersion desc, a.version.subMinorVersion desc" );
			q.setParameter( "uuid", uuid );
			List<T> res = q.getResultList();
			if ( !res.isEmpty() ) {
				t.begin();
				// get first element and mark it as most recent version (correct order is taken care of in query!)
				T tmp = res.get( 0 );
				tmp.setMostRecentVersion( true );
				tmp = em.merge( tmp );

				// set "false" for all other elements if required
				final int size = res.size();
				if ( size > 1 ) {
					for ( int i = 1; i < size; i++ ) {
						tmp = res.get( i );
						if ( tmp.isMostRecentVersion() ) {
							tmp.setMostRecentVersion( false );
							tmp = em.merge( tmp );
						}
					}
				}
				t.commit();
				return true;
			}
			else {
				DataSetDao.LOGGER.warn( "Most recent version flag was called for non-existent UUID: " + uuid );
				return false;
			}
		}
		catch ( Exception e ) {
			DataSetDao.LOGGER.error( "Could not set most recent version flag for UUID: " + uuid );
			if ( t.isActive() ) {
				t.rollback();
			}
			return false;
		}
	}

	/**
	 * This method (local serach) works as search method for datasets only on local database entities, i.e. Process,
	 * Flow, ... To use a search method which also
	 * works distributed use the corresponding search method instead
	 * 
	 * @param <T>
	 *            Class name of &quot;type&quot; of objects to return, i.e. Process, Flow, ...
	 * @param params
	 *            search parameters as key-value pairs
	 * @param stocks
	 *            stocks
	 * @return list of datasets of type T
	 */
	public List<T> lsearch( ValueParser params, IDataStockMetaData[] stocks ) {
		return this.lsearch( params, 0, 0, null, true, stocks );
	}

	/**
	 * This method (local lsearch) works as lsearch method for datasets only on local database entities, i.e. Process,
	 * Flow, ... To use a lsearch method which
	 * also works distributed use the corresponding lsearch method
	 * 
	 * @param <T>
	 *            Class name of &quot;type&quot; of objects to return, i.e. Process, Flow, ...
	 * @param params
	 *            lsearch parameters as key-value pairs
	 * @param startPosition
	 *            start index within the whole list of lsearch results
	 * @param pageSize
	 *            pages size, i.e. maximum count of results to return
	 * @param sortCriterium
	 *            name of the objects field which should be used for sorting
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * @return list of datasets of type T matching the lsearch criteria
	 */
	public List<T> lsearch( ValueParser params, int startPosition, int pageSize, String sortCriterium, boolean mostRecentVersionOnly,
			IDataStockMetaData[] stocks ) {
		return this.lsearch( params, startPosition, pageSize, sortCriterium, true, mostRecentVersionOnly, stocks );
	}

	/**
	 * This method (local lsearch) works as lsearch method for datasets only on local database entities, i.e. Process,
	 * Flow, ... To use a lsearch method which
	 * also works distributed use the corresponding lsearch method
	 * 
	 * @param <T>
	 *            Class name of &quot;type&quot; of objects to return, i.e. Process, Flow, ...
	 * @param params
	 *            lsearch parameters as key-value pairs
	 * @param startPosition
	 *            start index within the whole list of lsearch results
	 * @param pageSize
	 *            pages size, i.e. maximum count of results to return
	 * @param sortCriterium
	 *            name of the objects field which should be used for sorting
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * 
	 * @return list of datasets of type T matching the lsearch criteria
	 */
	public List<T> lsearch( ValueParser params, int startPosition, int pageSize, String sortCriterium, boolean mostRecentVersionOnly,
			IDataStockMetaData[] stocks, IDataStockMetaData excludeStock ) {
		return this.lsearch( params, startPosition, pageSize, sortCriterium, true, mostRecentVersionOnly, stocks, excludeStock );
	}

	/**
	 * This method (local lsearch) works as lsearch method for datasets only on local database entities, i.e. Process,
	 * Flow, ... To use a lsearch method which
	 * also works distributed use the corresponding lsearch method
	 * 
	 * @param <T>
	 *            Class name of &quot;type&quot; of objects to return, i.e. Process, Flow, ...
	 * @param params
	 *            lsearch parameters as key-value pairs
	 * @param startPosition
	 *            start index within the whole list of lsearch results
	 * @param pageSize
	 *            pages size, i.e. maximum count of results to return
	 * @param sortCriterium
	 *            name of the objects field which should be used for sorting
	 * @param ascending
	 *            define ordering of result
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * @return list of datasets of type T matching the lsearch criteria
	 */
	public List<T> lsearch( ValueParser params, int startPosition, int pageSize, String sortCriterium, boolean ascending, boolean mostRecentVersionOnly,
			IDataStockMetaData[] stocks ) {
		return this.lsearch( params, startPosition, pageSize, sortCriterium, ascending, mostRecentVersionOnly, stocks, null );
	}

	/**
	 * This method (local lsearch) works as lsearch method for datasets only on local database entities, i.e. Process,
	 * Flow, ... To use a lsearch method which
	 * also works distributed use the corresponding lsearch method
	 * 
	 * @param <T>
	 *            Class name of &quot;type&quot; of objects to return, i.e. Process, Flow, ...
	 * @param params
	 *            lsearch parameters as key-value pairs
	 * @param startPosition
	 *            start index within the whole list of lsearch results
	 * @param pageSize
	 *            pages size, i.e. maximum count of results to return
	 * @param sortCriterium
	 *            name of the objects field which should be used for sorting
	 * @param ascending
	 *            define ordering of result
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * @param excludeStock
	 *            stock that shall be excluded from the search (may be <code>null</code>)
	 * @return list of datasets of type T matching the lsearch criteria
	 */
	@SuppressWarnings( "unchecked" )
	public List<T> lsearch( ValueParser params, int startPosition, int pageSize, String sortCriterium, boolean ascending, boolean mostRecentVersionOnly,
			IDataStockMetaData[] stocks, IDataStockMetaData excludeStock ) {
		List<T> dataSets = null;

		Query query = this.createQueryObject( params, sortCriterium, ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING, false, mostRecentVersionOnly,
				stocks, excludeStock );

		if ( startPosition > 0 ) {
			query.setFirstResult( startPosition );
		}
		if ( pageSize > 0 ) {
			query.setMaxResults( pageSize );
		}

		dataSets = query.getResultList();

		return dataSets;
	}

	/**
	 * 
	 * This search method can also be used for distributed search because it uses the common interface types of the
	 * ServiceAPI For a version without the
	 * dataSetClassType parameter look in the subclasses
	 * 
	 * @param <E>
	 *            interface name of &quot;type&quot; of objects to return, i.e. IProcessListVO, IFlowListVo, ...
	 * @param dataSetClassType
	 *            Class object of T, i.e. IProcessListVO.class
	 * @param params
	 *            search parameter
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * @return List of objects of the give interface type E
	 * 
	 */
	public <E extends IDataSetListVO> List<E> search( Class<E> dataSetClassType, ValueParser params, boolean mostRecentVersionOnly, IDataStockMetaData[] stocks ) {
		return this.search( dataSetClassType, params, 0, 0, null, mostRecentVersionOnly, stocks );
	}

	/**
	 * This search method can also be used for distributed search because it uses the common interface types of the
	 * ServiceAPI
	 * 
	 * @param <E>
	 *            interface name of &quot;type&quot; of objects to return, i.e. IProcessListVO, IFlowListVo, ...
	 * @param dataSetClassType
	 *            Class object of T, i.e. IProcessListVO.class
	 * @param params
	 *            search parameter as ParameterTool object
	 * @param startPosition
	 *            start index for first search result
	 * @param pageSize
	 *            size of one page of search results
	 * @param sortCriterium
	 *            field name of object which is used for ordering
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * @return list of objects of the given interface E
	 */
	public <E extends IDataSetListVO> List<E> search( Class<E> dataSetClassType, ValueParser params, int startPosition, int pageSize, String sortCriterium,
			boolean mostRecentVersionOnly, IDataStockMetaData[] stocks ) {
		return this.searchDist( dataSetClassType, params, startPosition, pageSize, sortCriterium, SortOrder.ASCENDING, mostRecentVersionOnly, stocks, null,
				null );
	}

	/**
	 * This search method can also be used for distributed search because it uses the common interface types of the
	 * ServiceAPI
	 * 
	 * @param <E>
	 *            interface name of &quot;type&quot; of objects to return, i.e. IProcessListVO, IFlowListVo, ...
	 * @param dataSetClassType
	 *            Class object of T, i.e. IProcessListVO.class
	 * @param params
	 *            search parameter as ParameterTool object
	 * @param startPosition
	 *            start index for first search result
	 * @param pageSize
	 *            size of one page of search results
	 * @param sortCriterium
	 *            field name of object which is used for ordering
	 * @param sortOrder
	 *            define the ordering of the result
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            stocks
	 * @return list of objects of the given interface E
	 */
	public <E extends IDataSetListVO> List<E> searchDist( Class<E> dataSetClassType, ValueParser params, int startPosition, int pageSize, String sortCriterium,
			SortOrder sortOrder, boolean mostRecentVersionOnly, IDataStockMetaData[] stocks, IDataStockMetaData excludeStock, DistributedSearchLog log ) {

		// avoid a lot of null checks
		if ( params == null ) {
			params = new ValueParser();
		}
		LOGGER.trace( "searching for {}", dataSetClassType.getName() );

		Query query = this.createQueryObject( params, sortCriterium, sortOrder, false, mostRecentVersionOnly, stocks, excludeStock );

		if ( startPosition > 0 ) {
			query.setFirstResult( startPosition );
		}
		if ( pageSize > 0 ) {
			query.setMaxResults( pageSize );
		}

		List<E> dataSets = query.getResultList();

		if ( LOGGER.isTraceEnabled() ) {
			for ( IDataSetListVO ds : dataSets ) {
				LOGGER.trace( "name: {}, defaultname: {}", ds.getName().getValue(), ds.getDefaultName() );
			}
		}

		// OK, search also in other systems
		if ( params.exists( "distributed" ) && params.getBoolean( "distributed" ) ) {
			LOGGER.info( "initiating distributed search" );
			ForeignDataSetsHelper foreignHelper = new ForeignDataSetsHelper();
			List<E> foreignProcesses = foreignHelper.foreignSearch( dataSetClassType, params, log );
			if ( foreignProcesses != null ) {
				LOGGER.debug( "returning {} results from foreign nodes", foreignProcesses.size() );
			}
			dataSets.addAll( foreignProcesses );
		}
		return dataSets;
	}

	/**
	 * Get result count for lsearch call
	 * 
	 * @param params
	 *            lsearch parameters
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            meta data of data stocks
	 * @return returns the count of results when a lsearch is issued with this lsearch parameters
	 */
	public long searchResultCount( ValueParser params, boolean mostRecentVersionOnly, IDataStockMetaData[] stocks ) {
		return this.searchResultCount( params, mostRecentVersionOnly, stocks, null );
	}

	/**
	 * Get result count for lsearch call
	 * 
	 * @param params
	 *            lsearch parameters
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param stocks
	 *            meta data of data stocks
	 * @param excludeStock
	 *            stock that shall be excluded from the search (may be <code>null</code>)
	 * @return returns the count of results when a lsearch is issued with this lsearch parameters
	 */
	public long searchResultCount( ValueParser params, boolean mostRecentVersionOnly, IDataStockMetaData[] stocks, IDataStockMetaData excludeStock ) {
		Query query = this.createQueryObject( params, null, SortOrder.ASCENDING, true, mostRecentVersionOnly, stocks, excludeStock );

		Long resultCount = (Long) query.getSingleResult();

		return resultCount.longValue();
	}

	// ---------------------------------------

	/**
	 * Get the ORDER BY part of the query. If no match for the sortString is being found, return default value
	 * 
	 * @param typeAlias
	 *            alias of the type
	 * @param sortString
	 *            sort string
	 * @return ORDER BY part of JPQL query text
	 */
	protected abstract String getQueryStringOrderJpql( String typeAlias, String sortString );

	/**
	 * <p>
	 * Add all where clauses. They will act in conjunction (logic AND link).
	 * </p>
	 * <p>
	 * <b><u>Please note:</u></b>
	 * <ul>
	 * <li>
	 * Name and description filter will be added by default! Do not add them manually. If name and description filter
	 * differ for a concrete class, override
	 * {@link #addNameDescWhereClauseAndNamedParamForQueryStringJpql(String, ValueParser, List, Map)}</li>
	 * <li>
	 * Most recent version condition will be added automatically as well, do not add it manually!</li>
	 * </ul>
	 * </p>
	 * 
	 * @param typeAlias
	 *            alias of the type
	 * @param params
	 *            parameters
	 * @param whereClauses
	 *            add parts for the where clause, please use named parameters for user inputs!
	 * @param whereParamValues
	 *            map to add values for named parameters in
	 */
	protected abstract void addWhereClausesAndNamedParamesForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues );

	/**
	 * Add default name and description filter. Override this method if name and description filter shall differ for a
	 * concrete type.
	 * 
	 * @param typeAlias
	 *            type alias
	 * @param params
	 *            parameters
	 * @param whereClauses
	 *            add parts for the where clause, please use named parameters for user inputs!
	 * @param whereParamValues
	 *            map to add values for named parameters in
	 */
	protected void addNameDescWhereClauseAndNamedParamForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues ) {
		final String namePhrase = params.getString( "name" );
		final String descriptionPhrase = params.getString( "description" );

		boolean hasNameParam = !StringUtils.isBlank( namePhrase );
		boolean hasDescParam = !StringUtils.isBlank( descriptionPhrase ) && descriptionPhrase.length() > 2;
		if ( hasNameParam || hasDescParam ) {
			StringBuilder sb = new StringBuilder( "(" );
			if ( hasNameParam ) {
				sb.append( typeAlias + ".name.value like :namePhrase OR " + typeAlias + ".synonyms.value like :namePhrase " );
				whereParamValues.put( "namePhrase", "%" + namePhrase + "%" );
			}
			if ( hasDescParam ) {
				if ( hasNameParam ) {
					sb.append( "OR " );
				}
				sb.append( typeAlias + ".description.value like :descriptionPhrase" );
				whereParamValues.put( "descriptionPhrase", "%" + descriptionPhrase + "%" );
			}
			sb.append( ")" );
			whereClauses.add( sb.toString() );
		}
	}

	/**
	 * Get the joins for the query, default is <code>null</code> (which means none). Override method if joins are
	 * required.
	 * 
	 * @param params
	 *            parameter map
	 * @param typeAlias
	 *            type alias (to prevent collisions)
	 * @return joins string
	 */
	protected String getQueryStringJoinPart( ValueParser params, String typeAlias ) {
		return null;
	}

	/**
	 * <p>
	 * Create a query in order to search for data sets. The following methods affect the created query:
	 * <ul>
	 * <li>{@link #getQueryStringJoinPart(ValueParser, String)} (additional joins)</li>
	 * <li>
	 * {@link #addNameDescWhereClauseAndNamedParamForQueryStringJpql(String, ValueParser, List, Map)} (name and
	 * description filter)</li>
	 * <li>
	 * {@link #addWhereClausesAndNamedParamesForQueryStringJpql(String, ValueParser, List, Map)} (other filters)</li>
	 * </ul>
	 * Please see their documentation for details. Additionally, the filter for <i>most recent version</i> is being
	 * added here, do not add it in a subclass via one of the methods above (you won't get the boolean flag indicating
	 * it anyway)!
	 * </p>
	 * <p>
	 * This method is being used by the other generic lsearch functions to issue the special query.
	 * </p>
	 * 
	 * @param params
	 *            lsearch parameter
	 * @param sortCriterium
	 *            field of result object which will be used for ordering of lsearch results
	 * @param sortOrder
	 *            enum: ASCENDING, DESCENDING, UNSORTED
	 * @param returnCount
	 *            if true return count of lsearch result instead of lsearch results
	 * @param mostRecentVersionOnly
	 *            flag to indicate if only the most recent version of a data set shall be returned if multiple versions
	 *            exist
	 * @param searchStocks
	 *            meta data of data stocks
	 * @param excludeStock
	 *            stock that shall be excluded from the search (may be <code>null</code>)
	 * @return JPA Query objects for doing the lsearch
	 * 
	 */
	protected final Query createQueryObject( ValueParser params, String sortCriterium, SortOrder sortOrder, boolean returnCount, boolean mostRecentVersionOnly,
			IDataStockMetaData[] stocks, IDataStockMetaData excludeStock ) {

		// filter out exclude stock
		final String excludeStockName = excludeStock != null ? excludeStock.getName() : null;
		List<IDataStockMetaData> searchStocks = new ArrayList<IDataStockMetaData>();
		for ( IDataStockMetaData dsm : stocks ) {
			if ( !dsm.getName().equals( excludeStockName ) ) {
				searchStocks.add( dsm );
			}
		}

		EntityManager em = PersistenceUtil.getEntityManager();

		if ( params == null ) {
			params = new ValueParser(); // avoids lot's of null checks
		}

		final String typeAlias = "a";
		final String typeName = this.getJpaName();

		StringBuffer queryString = new StringBuffer();
		if ( returnCount ) {
			queryString.append( "SELECT COUNT(DISTINCT " + typeAlias + ") FROM " + typeName + " " + typeAlias + " " );
		}
		else {
			queryString.append( "SELECT DISTINCT " + typeAlias + " FROM " + typeName + " " + typeAlias + " " );
		}

		String joins = this.getQueryStringJoinPart( params, typeAlias );
		if ( joins == null ) {
			joins = "";
		}

		List<String> whereClauses = new ArrayList<String>();
		Map<String, Object> whereParamValues = new LinkedHashMap<String, Object>();

		if ( mostRecentVersionOnly ) {
			whereClauses.add( typeAlias + ".mostRecentVersion=:mostRecent" );
			whereParamValues.put( "mostRecent", Boolean.TRUE );
		}

		boolean dsJoinDone = false;
		if ( searchStocks != null ) {
			List<String> stockClauses = new ArrayList<String>();
			List<String> rootStockClauses = new ArrayList<String>();
			List<Long> dsIds = new ArrayList<Long>();

			for ( IDataStockMetaData m : searchStocks ) {
				// root data stock
				if ( m.isRoot() ) {
					String paramName = "rootDsId" + Long.toString( m.getId() );
					rootStockClauses.add( typeAlias + ".rootDataStock.id=:" + paramName );
					whereParamValues.put( paramName, m.getId() );
				}
				// non root data stock
				else {
					if ( !dsJoinDone ) {
						joins += " LEFT JOIN " + typeAlias + ".containingDataStocks " + typeAlias + "ds";
						dsJoinDone = true;
					}
					dsIds.add( m.getId() );
				}
			}
			if ( !rootStockClauses.isEmpty() ) {
				stockClauses.add( "(" + this.join( rootStockClauses, " OR " ) + ")" );
			}
			if ( dsJoinDone && !dsIds.isEmpty() ) {
				stockClauses.add( typeAlias + "ds.id IN(" + StringUtils.join( dsIds, ',' ) + ")" );
			}
			if ( !stockClauses.isEmpty() ) {
				whereClauses.add( "(" + StringUtils.join( stockClauses, " OR " ) + ")" );
			}
		}
		if ( excludeStock != null ) {
			String paramName = "exclDsId" + Long.toString( excludeStock.getId() );
			if ( excludeStock.isRoot() ) {
				whereClauses.add( typeAlias + ".rootDataStock.id!=:" + paramName );
			}
			else {
				if ( !dsJoinDone ) {
					joins += " LEFT JOIN " + typeAlias + ".containingDataStocks " + typeAlias + "ds";
					dsJoinDone = true;
				}
				whereClauses.add( "(" + typeAlias + "ds.id IS NULL OR " + typeAlias + "ds.id!=:" + paramName + ")" );
			}
			whereParamValues.put( paramName, excludeStock.getId() );
		}

		// name and description filter
		this.addNameDescWhereClauseAndNamedParamForQueryStringJpql( typeAlias, params, whereClauses, whereParamValues );

		// let's collect all where clauses beside name, description and most
		// recent version
		this.addWhereClausesAndNamedParamesForQueryStringJpql( typeAlias, params, whereClauses, whereParamValues );

		if ( !StringUtils.isBlank( joins ) ) {
			queryString.append( " " ).append( joins.trim() ).append( " " );
		}

		if ( !whereClauses.isEmpty() ) {
			queryString.append( "WHERE " );
			queryString.append( this.join( whereClauses, " AND " ) );
		}

		if ( !returnCount ) {
			final String orderJpql = this.getQueryStringOrderJpql( typeAlias, sortCriterium );
			queryString.append( " ORDER BY " + orderJpql + " " + (SortOrder.ASCENDING.equals( sortOrder ) ? "asc" : "desc") );
		}

		DataSetDao.LOGGER.debug( "search query: {}", queryString );

		Query query = em.createQuery( queryString.toString() );
		for ( Entry<String, Object> e : whereParamValues.entrySet() ) {
			query.setParameter( e.getKey(), e.getValue() );
		}

		return query;
	}

	// ---------------------------------------

	/**
	 * Remove a given data set from the database
	 * 
	 * @param dataSet
	 *            data set to be removed
	 * @throws DeleteDataSetException
	 *             on removing error from persistence layer
	 * @throws Exception
	 *             on removing error from persistence layer
	 */
	@Override
	public T remove( T dataSet ) throws DeleteDataSetException {
		if ( dataSet == null || dataSet.getId() == null ) {
			return null;
		}
		try {
			T tmp = super.remove( dataSet );
			if ( tmp != null ) {
				final String uuid = tmp.getUuidAsString();
				// if the dataset to delete is the last one of its uuid, nothing is wrong if setMostRecentVersionFlags
				// turns out false
				if ( !this.setMostRecentVersionFlags( uuid ) && getOtherVersions( tmp ).size() != 0 ) {
					throw new IllegalStateException( "Could not set most recent version flag!" );
				}
			}

			return tmp;
		}
		catch ( Exception e ) {
			DataSetDao.LOGGER.error( "Cannot remove data set {} from database", dataSet.getName().getDefaultValue(), e );
			throw new DeleteDataSetException( "Cannot remove data set " + dataSet.getName().getDefaultValue(), e );
		}
	}

	/**
	 * Get a foreign data set
	 * 
	 * @param nodeShortName
	 *            short name / id of remote node
	 * @param uuid
	 *            uuid of data set
	 * @return loaded data set
	 */
	public D getForeignDataSet( String nodeShortName, String uuid, Long registryId ) {
		ForeignDataSetsHelper helper = new ForeignDataSetsHelper();
		return helper.getForeignDataSet( this.voClass, nodeShortName, uuid, registryId );
	}

}
