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
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.generic.ValueParser;

import de.fzk.iai.ilcd.service.model.IProcessListVO;
import de.fzk.iai.ilcd.service.model.IProcessVO;
import de.fzk.iai.ilcd.service.model.enums.TypeOfProcessValue;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.common.GeographicalArea;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.process.Exchange;
import de.iai.ilcd.model.process.ExchangeDirection;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * Data access object for {@link Process processes}
 */
public class ProcessDao extends DataSetDao<Process, IProcessListVO, IProcessVO> {

	/**
	 * Create the data access object for {@link Process processes}
	 */
	public ProcessDao() {
		super( "Process", Process.class, IProcessListVO.class, IProcessVO.class );
	}

	protected String getDataStockField() {
		return "processes";
	}

	public Process getFullProcess( String uuid ) {
		Process process = this.getByUuid( uuid );
		if ( process != null ) {
			this.addFlowsToExchanges( process );
		}
		return process;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preCheckAndPersist( Process dataSet ) {
		this.addFlowsToExchanges( dataSet );
	}

	/**
	 * Get the list of processes which have the provided direction and flow as in- or output exchange flow
	 * 
	 * @param flowUuid
	 *            uuid of flow
	 * @param direction
	 *            direction of flow
	 * @param firstResult
	 *            start index
	 * @param maxResults
	 *            maximum result items
	 * @return list of processes which have the provided direction and flow as in- or output exchange flow
	 */
	@SuppressWarnings( "unchecked" )
	public List<Process> getProcessesForExchangeFlow( String flowUuid, ExchangeDirection direction, int firstResult, int maxResults ) {
		Query q = this.getProcessesForExchangeFlowQuery( flowUuid, direction, false );
		q.setFirstResult( firstResult );
		q.setMaxResults( maxResults );
		return q.getResultList();
	}

	/**
	 * Get count of processes for provided exchange flow and direction
	 * 
	 * @param flowUuid
	 *            uuid of flow
	 * @param direction
	 *            direction of flow
	 * @return count of processes for provided exchange flow and direction
	 */
	public long getProcessesForExchangeFlowCount( String flowUuid, ExchangeDirection direction ) {
		Query q = this.getProcessesForExchangeFlowQuery( flowUuid, direction, true );
		return (Long) q.getSingleResult();
	}

	/**
	 * Get query for list or count of processes which have provided flow as in- or output exchange flow
	 * 
	 * @param flow
	 *            flow to get processes for
	 * @param direction
	 *            direction of process
	 * @param count
	 *            flag to indicate if count query shall be created
	 * @return query for list or count of processes which have provided flow as in- or output exchange flow
	 */
	private Query getProcessesForExchangeFlowQuery( String flowUuid, ExchangeDirection direction, boolean count ) {
		if ( flowUuid == null ) {
			throw new IllegalArgumentException( "Flow must not be null!" );
		}
		if ( direction == null ) {
			throw new IllegalArgumentException( "Direction must not be null!" );
		}
		EntityManager em = PersistenceUtil.getEntityManager();

		StringBuilder sb = new StringBuilder();
		sb.append( "SELECT " );

		if ( count ) {
			sb.append( "COUNT(DISTINCT p)" );
		}
		else {
			sb.append( "DISTINCT p" );
		}

		sb.append( " FROM Process p LEFT JOIN p.exchanges e WHERE e.flow.uuid.uuid=:uuid AND e.exchangeDirection=:dir" );

		Query q = em.createQuery( sb.toString() );

		q.setParameter( "uuid", flowUuid );
		q.setParameter( "dir", direction );

		return q;

	}

	@SuppressWarnings( "unchecked" )
	public List<GeographicalArea> getUsedLocations() {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery( "select distinct area from Process p, GeographicalArea area where p.geography.location=area.areaCode order by area.name" )
				.getResultList();
	}

	@SuppressWarnings( "unchecked" )
	public List<GeographicalArea> getAllLocations() {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery( "select distinct area from GeographicalArea area order by area.name" ).getResultList();
	}

	@SuppressWarnings( "unchecked" )
	public List<Integer> getReferenceYears() {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery( "select distinct p.timeInformation.referenceYear from Process p order by p.timeInformation.referenceYear asc" ).getResultList();
	}

	/**
	 * Get the reference years
	 * 
	 * @param stocks
	 *            stocks to get reference years for
	 * @return loaded years. <b>Please note:</b> no stocks (<code>null</code> or empty array) will return an empty list!
	 */
	@SuppressWarnings( "unchecked" )
	public List<Integer> getReferenceYears( IDataStockMetaData... stocks ) {
		if ( stocks == null || stocks.length == 0 ) {
			return new ArrayList<Integer>();
		}

		List<String> lstRdsIds = new ArrayList<String>();
		List<String> lstDsIds = new ArrayList<String>();
		for ( IDataStockMetaData m : stocks ) {
			if ( m.isRoot() ) {
				lstRdsIds.add( Long.toString( m.getId() ) );
			}
			else {
				lstDsIds.add( Long.toString( m.getId() ) );
			}
		}

		String join = "";
		final List<String> whereSmtnts = new ArrayList<String>();

		if ( !lstRdsIds.isEmpty() ) {
			whereSmtnts.add( "p.rootDataStock.id in (" + this.join( lstRdsIds, "," ) + ")" );
		}
		if ( !lstDsIds.isEmpty() ) {
			join = " LEFT JOIN p.containingDataStocks ds";
			whereSmtnts.add( "ds.id in (" + this.join( lstDsIds, "," ) + ")" );
		}

		EntityManager em = PersistenceUtil.getEntityManager();
		return em.createQuery(
				"select distinct p.timeInformation.referenceYear from Process p" + join + " WHERE " + StringUtils.join( whereSmtnts, " OR " )
						+ " order by p.timeInformation.referenceYear asc" ).getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryStringOrderJpql( String typeAlias, String sortString ) {
		if ( "type.value".equals( sortString ) ) {
			return typeAlias + ".type";
		}
		else if ( "classification.classHierarchyAsString".equals( sortString ) ) {
			return typeAlias + ".classificationCache";
		}
		else if ( "location".equals( sortString ) ) {
			return typeAlias + ".geography.location";
		}
		else if ( "timeInformation.referenceYear".equals( sortString ) ) {
			return typeAlias + ".timeInformation.referenceYear";
		}
		else if ( "timeInformation.validUntil".equals( sortString ) ) {
			return typeAlias + ".timeInformation.validUntil";
		}
		else if ( "LCIMethodInformation.methodPrinciple.value".equals( sortString ) ) {
			return typeAlias + ".lCIMethodInformation.methodPrinciple";
		}
		else if ( "complianceSystems".equals( sortString ) ) {
			return typeAlias + ".complianceSystemCache";
		}
		else {
			return typeAlias + ".nameCache";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryStringJoinPart( ValueParser params, String typeAlias ) {
		boolean exchangeFlowQuery = !StringUtils.isBlank( params.getString( "exchangeFlow" ) );
		if ( exchangeFlowQuery ) {
			return "LEFT JOIN " + typeAlias + ".exchanges " + typeAlias + "Ex";
		}

		boolean hasNameParam = !StringUtils.isBlank( params.getString( "name" ) );
		boolean hasDescParam = !StringUtils.isBlank( params.getString( "description" ) );

		if ( hasNameParam || hasDescParam ) {
			StringBuilder buf = new StringBuilder();
			if ( hasNameParam ) {
				buf.append( "LEFT JOIN " );
				buf.append( typeAlias );
				buf.append( ".baseName bn LEFT JOIN " );
				buf.append( typeAlias );
				buf.append( ".nameRoute nr LEFT JOIN " );
				buf.append( typeAlias );
				buf.append( ".nameLocation nl LEFT JOIN " );
				buf.append( typeAlias );
				buf.append( ".nameUnit nu LEFT JOIN " );
				buf.append( typeAlias );
				buf.append( ".synonyms syn " );
			}
			if ( hasDescParam ) {
				buf.append( "LEFT JOIN " );
				buf.append( typeAlias );
				buf.append( ".description des LEFT JOIN " );
				buf.append( typeAlias );
				buf.append( ".useAdvice ua " );
			}
			return buf.toString();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addWhereClausesAndNamedParamesForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues ) {
		// time information stuff
		final Integer lowerRefYear = this.parseInteger( params.getString( "referenceYearLower" ) );
		final Integer upperRefYear = this.parseInteger( params.getString( "referenceYearUpper" ) );
		final Integer lowerValidUntil = this.parseInteger( params.getString( "lowerValidUntil" ) );
		final Integer upperValidUntil = this.parseInteger( params.getString( "upperValidUntil" ) );

		if ( lowerRefYear != null ) {
			whereClauses.add( typeAlias + ".timeInformation.referenceYear >= :lowerRefYear" );
			whereParamValues.put( "lowerRefYear", lowerRefYear );
		}

		if ( upperRefYear != null ) {
			whereClauses.add( typeAlias + ".timeInformation.referenceYear <= :upperRefYear" );
			whereParamValues.put( "upperRefYear", upperRefYear );
		}

		if ( lowerValidUntil != null ) {
			whereClauses.add( typeAlias + ".timeInformation.validUntil >= :lowRefYear" );
			whereParamValues.put( "lowRefYear", lowerRefYear );
		}

		if ( upperValidUntil != null ) {
			whereClauses.add( typeAlias + ".timeInformation.validUntil <= :upperValidUntil" );
			whereParamValues.put( "upperValidUntil", upperValidUntil );
		}

		// parameterized
		if ( params.getString( "parameterized" ) != null ) {
			whereClauses.add( typeAlias + ".parameterized=:parameterized" );
			whereParamValues.put( "parameterized", Boolean.TRUE );
		}

		// exchange flows
		final boolean exchangeFlowQuery = params.getString( "exchangeFlow" ) != null;
		final String exAlias = typeAlias + "Ex";

		if ( exchangeFlowQuery ) {
			whereClauses.add( exAlias + ".flow.uuid.uuid=:exFlowUuid" );
			whereClauses.add( exAlias + ".exchangeDirection=:exFlowDir" );

			whereParamValues.put( "exFlowUuid", params.getString( "exchangeFlow" ) );
			whereParamValues.put( "exFlowDir", params.get( "exchangeFlowDirection" ) );
		}

		// classification
		String[] categories = params.getStrings( "classes" );
		if ( categories != null && categories.length > 0 ) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			int paramNo = 0;
			for ( String cat : categories ) {
				if ( first ) {
					first = false;
				}
				else {
					sb.append( " OR " );
				}
				String catParam = "classParam" + Integer.toString( paramNo++ );
				sb.append( typeAlias + ".classificationCache LIKE :" + catParam );
				whereParamValues.put( catParam, "%" + cat + "%" );
			}
			whereClauses.add( sb.toString() );
		}

		// type
		String type = params.getString( "type" );
		if ( type != null && (type.length() > 3) && (!type.equals( "select option" )) ) {
			TypeOfProcessValue typeValue = null;
			try {
				typeValue = TypeOfProcessValue.valueOf( type );
			}
			catch ( Exception e ) {
				// ignore it as we do not have a parsable value
			}
			if ( typeValue != null ) {
				whereClauses.add( typeAlias + ".type=:typeOfProc" );
				whereParamValues.put( "typeOfProc", typeValue );
			}
		}

		// Locations
		String[] locations = params.getStrings( "location" );
		if ( locations != null && locations.length > 0 ) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			int paramNo = 0;
			for ( String loc : locations ) {
				if ( first ) {
					first = false;
				}
				else {
					sb.append( " OR " );
				}
				String locParam = "locParam" + Integer.toString( paramNo++ );
				sb.append( typeAlias + ".geography.location=:" + locParam );
				whereParamValues.put( locParam, "%" + loc + "%" );
			}
			whereClauses.add( sb.toString() );
		}

		// registries
		String registeredIn = params.getString( "registeredIn" );
		if ( registeredIn != null ) {
			whereClauses.add( "EXISTS (SELECT dsrd FROM DataSetRegistrationData dsrd WHERE dsrd.registry.uuid = :registeredIn " + "AND dsrd.uuid = "
					+ typeAlias + ".uuid.uuid " + "AND dsrd.version = " + typeAlias + ".version "
					+ "AND dsrd.status =  de.iai.ilcd.model.registry.DataSetRegistrationDataStatus.ACCEPTED" + ")" );
			whereParamValues.put( "registeredIn", registeredIn );
		}

	}

	/**
	 * Override default behavior for name and description filter in order to use {@link Process#getProcessName()}
	 * instead of {@link Process#getName()}.
	 */
	@Override
	protected void addNameDescWhereClauseAndNamedParamForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues ) {

		// name and description
		final String namePhrase = params.getString( "name" );
		final String descriptionPhrase = params.getString( "description" );

		boolean hasNameParam = !StringUtils.isBlank( namePhrase );
		boolean hasDescParam = !StringUtils.isBlank( descriptionPhrase ) && descriptionPhrase.length() > 2;
		if ( hasNameParam || hasDescParam ) {
			StringBuilder sb = new StringBuilder( "(" );
			if ( hasNameParam ) {
				sb.append( "bn LIKE :namePhrase OR " );
				sb.append( "nr LIKE :namePhrase OR " );
				sb.append( "nl LIKE :namePhrase OR " );
				sb.append( "nu LIKE :namePhrase OR " );
				sb.append( "syn LIKE :namePhrase" );
				whereParamValues.put( "namePhrase", "%" + namePhrase + "%" );
			}
			if ( hasDescParam ) {
				if ( hasNameParam ) {
					sb.append( " OR " );
				}
				sb.append( "des LIKE :descriptionPhrase OR " );
				sb.append( "ua LIKE :descriptionPhrase" );
				whereParamValues.put( "descriptionPhrase", "%" + descriptionPhrase + "%" );
			}
			sb.append( ")" );
			whereClauses.add( sb.toString() );
		}

	}

	/**
	 * Parse integer and return <code>null</code> on arbitrary error
	 * 
	 * @param s
	 *            string to parse
	 * @return parsed integer or <code>null</code>
	 */
	private Integer parseInteger( String s ) {
		if ( s == null || s.trim().isEmpty() ) {
			return null;
		}
		try {
			return new Integer( s );
		}
		catch ( Exception e ) {
			return null;
		}
	}

	/**
	 * Get a foreign process
	 * 
	 * @param nodeShortName
	 *            short name of the node
	 * @param uuid
	 *            UUID of process
	 * @return loaded process
	 */
	public IProcessVO getForeignProcess( String nodeShortName, String uuid, Long registryId ) {
		ForeignDataSetsHelper foreignHelper = new ForeignDataSetsHelper();
		return foreignHelper.getForeignDataSet( IProcessVO.class, nodeShortName, uuid, registryId );
	}

	/**
	 * Add flows references to the exchanges. This means that references to {@link Exchange#getFlow() data base flows}
	 * shall be established where only {@link Exchange#getFlowReference() global references} are set.
	 * 
	 * @param process
	 *            process to find flows for
	 */
	private void addFlowsToExchanges( Process process ) {
		FlowDao flowDao = new FlowDao();

		for ( Exchange exchange : process.getExchanges() ) {

			if ( exchange.getFlow() != null ) {
				continue; // flow already associated
			}
			if ( exchange.getFlowReference() == null ) {
				continue;
			}
			GlobalReference flowReference = exchange.getFlowReference();
			String flowUuid = null;
			if ( flowReference.getUuid() == null ) {
				// crap, no uuid attribute in flow reference
				// but we can try to get it from the uri attribute if there
				String uri = flowReference.getUri();
				if ( uri == null ) {
					continue; // sorry we can't get uuid
				}
				String[] splittedUri = uri.split( "_" );
				if ( splittedUri.length <= 2 ) {
					continue; // uri does not contain enough parts: so no uuid
				}
				flowUuid = splittedUri[splittedUri.length - 2];
			}
			else {
				flowUuid = exchange.getFlowReference().getUuid().getUuid();
			}
			Flow flow = null;
			try {
				flow = flowDao.getByUuid( flowUuid );
				exchange.setFlow( flow );
			}
			catch ( NoResultException ex ) {
				// OK, we have no associated flow, so do nothing
			}
		}
	}

	/*
	 * ================================================== == DEPRECATED METHODS / BACKWARDS COMPATIBILITY ==
	 * ==================================================
	 */

	/**
	 * Get list of all processes
	 * 
	 * @return list of all processes
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getAll()}.
	 * @see #getAll()
	 */
	@Deprecated
	public List<Process> getProcesses() {
		return this.getAll();
	}

	/**
	 * Get a process by UUID
	 * 
	 * @param uuid
	 *            UUID of flow to get
	 * @return process with provided UUID
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByUuid(String)}.
	 * @see #getByUuid(String)
	 */
	@Deprecated
	public Process getProcess( String uuid ) {
		return this.getByUuid( uuid );
	}

	/**
	 * Get {@link Process} by UUID string and version
	 * 
	 * @param uuid
	 *            the UUID string
	 * @param version
	 *            the version of the method
	 * @return {@link Process} for the specified UUID string/version, or <code>null</code> if none found
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByUuidAndVersion(String, DataSetVersion)}.
	 */
	@Deprecated
	public Process getProcess( String uuid, DataSetVersion version ) {
		return this.getByUuidAndVersion( uuid, version );
	}

	/**
	 * Get the process with the provided JPA id
	 * 
	 * @param datasetId
	 *            JPA id
	 * @return process with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public Process getProcess( long datasetId ) {
		return this.getByDataSetId( datasetId );
	}

	/**
	 * Get the process with the provided JPA id
	 * 
	 * @param id
	 *            JPA id as string
	 * @return process with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(String)}.
	 * @see #getByDataSetId(String)
	 */
	@Deprecated
	public Process getProcessById( String id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get the process with the provided JPA id
	 * 
	 * @param id
	 *            JPA id
	 * @return process with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public Process getProcessById( Long id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get processes by main class
	 * 
	 * @param mainClass
	 *            main class to get process by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getByClass(String)
	 */
	@Deprecated
	public List<Process> getProcessesByClass( String mainClass ) {
		return this.getByClass( mainClass );
	}

	/**
	 * Get the number of processes by main class
	 * 
	 * @param mainClass
	 *            main class to get process by
	 * @return the number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getNumberByClass(String)
	 */
	@Deprecated
	public long getNumberOfProcesses( String mainClass ) {
		return this.getNumberByClass( mainClass );
	}

	/**
	 * Get processes by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get process by
	 * @param subClass
	 *            sub class to get process by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getByClass(String,String)
	 */
	@Deprecated
	public List<Process> getProcessesByClass( String mainClass, String subClass ) {
		return this.getByClass( mainClass, subClass );
	}

	/**
	 * Get number of processes by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get process by
	 * @param subClass
	 *            sub class to get process by
	 * @return number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getNumberByClass(String,String)
	 */
	@Deprecated
	public long getNumberOfProcesses( String mainClass, String subClass ) {
		return this.getNumberByClass( mainClass, subClass );
	}

}
