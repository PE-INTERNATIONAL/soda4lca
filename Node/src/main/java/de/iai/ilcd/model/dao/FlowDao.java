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

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.velocity.tools.generic.ValueParser;

import de.fzk.iai.ilcd.service.model.IFlowListVO;
import de.fzk.iai.ilcd.service.model.IFlowVO;
import de.fzk.iai.ilcd.service.model.enums.TypeOfFlowValue;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.flow.FlowPropertyDescription;
import de.iai.ilcd.model.flowproperty.FlowProperty;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * Data access object for {@link Flow flows}
 */
public class FlowDao extends DataSetDao<Flow, IFlowListVO, IFlowVO> {

	/**
	 * Create the DAO for flows
	 */
	public FlowDao() {
		super( "Flow", Flow.class, IFlowListVO.class, IFlowVO.class );
	}

	protected String getDataStockField() {
		return "flows";
	}

	// /**
	// * Get all flows (due to the enormous data amount for flows, in this case all means the first 1000). If you want
	// * more, use {@link #getFlows(int)}.
	// *
	// * @return list of first 1000 flows
	// * @see #getFlows(int)
	// */
	// @Override
	// public List<Flow> getAll() {
	// return this.getFlows(1000);
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preCheckAndPersist( Flow dataSet ) {
		this.findReferenceFlowProperty( dataSet );
	}

	/**
	 * Get the most recent name of a flow for given uuid string
	 * 
	 * @param uuid
	 *            uuid string
	 * @return most recent name of the flow
	 */
	public String getDefaultNameForUuid( String uuid ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		try {
			return (String) em
					.createQuery(
							"SELECT f.name.value FROM Flow f WHERE f.uuid.uuid=:uuid ORDER BY f.version.majorVersion DESC, f.version.minorVersion DESC, f.version.subMinorVersion DESC" )
					.setParameter( "uuid", uuid ).setMaxResults( 1 ).getSingleResult();
		}
		catch ( Exception e ) {
			return null;
		}
	}

	/**
	 * Get flows with maximum limit of result items
	 * 
	 * @param maxFlows
	 *            maximum amount of flows to get
	 * @return flows with maximum limit of result items
	 */
	@SuppressWarnings( "unchecked" )
	public List<Flow> getFlows( int maxFlows ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (List<Flow>) em.createQuery( "select a from " + this.getJpaName() + " a" ).setMaxResults( maxFlows ).getResultList();
	}

	/**
	 * Get a flow by UUID. Overrides {@link DataSetDao} implementation, because there is an additional step which tries
	 * to associate referenced flow property, if available
	 * 
	 * @return flow with provided UUID
	 */
	@Override
	public Flow getByUuid( String uuid ) {
		Flow f = super.getByUuid( uuid );
		if ( f != null ) {
			this.findReferenceFlowProperty( f ); // try to associate reference flow property if available
		}
		return f;
	}

	@SuppressWarnings( "unchecked" )
	public List<Flow> getFlowsByCategory( String mainClass ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (List<Flow>) em.createQuery( "select f from Flow f join f.categorization.classes cl where f.type=:type and cl.level=0 and cl.name=:className" )
				.setParameter( "className", mainClass ).setParameter( "type", TypeOfFlowValue.ELEMENTARY_FLOW ).getResultList();
	}

	public long getNumberOfFlowsInCategory( String mainClass ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (Long) em.createQuery( "select count(f) from Flow f join f.categorization.classes cl where f.type=:type and cl.level=0 and cl.name=:className" )
				.setParameter( "className", mainClass ).setParameter( "type", TypeOfFlowValue.ELEMENTARY_FLOW ).getSingleResult();
	}

	@SuppressWarnings( "unchecked" )
	public List<Flow> getFlowsByCategory( String mainClass, String subClass ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (List<Flow>) em
				.createQuery(
						"select f from Flow f join f.categorization.classes cl join f.categorization.classes cl2 where f.type=:type and cl.level=0 and cl.name=:mainClass and cl2.level=1 and cl2.name=:subClass" )
				.setParameter( "type", TypeOfFlowValue.ELEMENTARY_FLOW ).setParameter( "mainClass", mainClass ).setParameter( "subClass", subClass )
				.getResultList();
	}

	@SuppressWarnings( "unchecked" )
	public List<Flow> getFlowsBySubCategories( String subClass, String subClass2 ) {
		EntityManager em = PersistenceUtil.getEntityManager();

		Query q = em
				.createQuery( "select f from Flow f join f.categorization.classes cl1 join f.categorization.classes cl2 where f.type=:type and cl1.level=1 and cl1.name=:subClass and cl2.level=2 and cl2.name=:subClass2" );
		q.setParameter( "type", TypeOfFlowValue.ELEMENTARY_FLOW );
		q.setParameter( "subClass", subClass );
		q.setParameter( "subClass2", subClass2 );

		return (List<Flow>) q.getResultList();
	}

	public long getNumberOfFlowsInCategory( String mainClass, String subClass ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (Long) em
				.createQuery(
						"select count(f) from Flow f join f.categorization.classes cl join f.categorization.classes cl2 where f.type=:type and cl.level=0 and cl.name=:mainClass and cl2.level=1 and cl2.name=:subClass" )
				.setParameter( "type", TypeOfFlowValue.ELEMENTARY_FLOW ).setParameter( "mainClass", mainClass ).setParameter( "subClass", subClass )
				.getSingleResult();
	}

	@SuppressWarnings( "unchecked" )
	public List<String> getTopClasses2() {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (List<String>) em.createQuery( "select distinct cl.name from Flow f join f.classification.classes cl where cl.level=:level order by cl.name" )
				.setParameter( "level", 0 ).getResultList();
	}

	@SuppressWarnings( "unchecked" )
	public List<String> getTopCategories() {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (List<String>) em.createQuery( "select distinct cl.name from Flow f join f.categorization.classes cl where cl.level=:level order by cl.name" )
				.setParameter( "level", 0 ).getResultList();
	}

	@SuppressWarnings( "unchecked" )
	public List<String> getSubCategories( String className, String level ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return (List<String>) em
				.createQuery(
						"select distinct cl.name from Flow f join f.categorization.classes cl join f.categorization.classes cl2 where cl.level=:level and cl2.name=:className order by cl.name" )
				.setParameter( "level", Integer.parseInt( level ) ).setParameter( "className", className ).getResultList();
	}

	private void findReferenceFlowProperty( Flow flow ) {
		FlowPropertyDao flowpropDao = new FlowPropertyDao();

		FlowPropertyDescription referenceProperty = flow.getReferenceProperty();
		if ( referenceProperty != null && referenceProperty.getFlowProperty() == null ) {
			// OK, let't try to find the property in the database
			// String permanentUri=referenceProperty.getFlowPropertyRef().getUri();
			String uuid = referenceProperty.getFlowPropertyRef().getUuid().getUuid();
			FlowProperty refprop = flowpropDao.getByUuid( uuid );
			referenceProperty.setFlowProperty( refprop );

		}
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
		else if ( "referenceFlowProperty.flowPropertyName.value".equals( sortString ) ) {
			return typeAlias + ".referencePropertyCache";
		}
		else if ( "referenceFlowProperty.flowPropertyUnit".equals( sortString ) ) {
			return typeAlias + ".referencePropertyUnitCache";
		}
		else {
			return typeAlias + ".nameCache";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addWhereClausesAndNamedParamesForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues ) {
		String type = params.getString( "type" );
		if ( type != null && (type.length() > 3) && (!type.equals( "select option" )) ) {
			TypeOfFlowValue typeValue = null;
			try {
				typeValue = TypeOfFlowValue.valueOf( type );
			}
			catch ( Exception e ) {
				// ignore it as we do not have a parsable value
			}
			if ( typeValue != null ) {
				whereClauses.add( typeAlias + ".type=:typeOfFlow" );
				whereParamValues.put( "typeOfFlow", typeValue );
			}
		}
	}

	/*
	 * ================================================== == DEPRECATED METHODS / BACKWARDS COMPATIBILITY ==
	 * ==================================================
	 */

	/**
	 * Get a flow by UUID
	 * 
	 * @param uuid
	 *            UUID of flow to get
	 * @return flow with provided UUID
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByUuid(String)}.
	 * @see #getByUuid(String)
	 */
	@Deprecated
	public Flow getFlow( String uuid ) {
		return this.getByUuid( uuid );
	}

	/**
	 * Get the flow with the provided JPA id
	 * 
	 * @param datasetId
	 *            JPA id
	 * @return flow with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public Flow getFlow( long datasetId ) {
		// TODO check with clemens if findReferenceFlowProperty shall be called as well
		return this.getByDataSetId( datasetId );
	}

	/**
	 * Get the flow with the provided JPA id
	 * 
	 * @param id
	 *            JPA id as string
	 * @return flow with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(String)}.
	 * @see #getByDataSetId(String)
	 */
	public Flow getFlowById( String id ) {
		// TODO check with clemens if findReferenceFlowProperty shall be called as well
		return this.getByDataSetId( id );
	}

	/**
	 * Get the flow with the provided JPA id
	 * 
	 * @param id
	 *            JPA id
	 * @return flow with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	public Flow getFlowById( Long id ) {
		// TODO check with clemens if findReferenceFlowProperty shall be called as well
		return this.getByDataSetId( id );
	}

	/**
	 * Get flows by main class
	 * 
	 * @param mainClass
	 *            main class to get flow by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getByClass(String)
	 */
	@Deprecated
	public List<Flow> getFlowsByClass( String mainClass ) {
		return this.getByClass( mainClass );
	}

	/**
	 * Get the number of flows by main class
	 * 
	 * @param mainClass
	 *            main class to get flow by
	 * @return the number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getNumberByClass(String)
	 */
	@Deprecated
	public long getNumberOfFlowsInClass( String mainClass ) {
		return this.getNumberByClass( mainClass );
	}

	/**
	 * Get flows by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get flow by
	 * @param subClass
	 *            sub class to get flow by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getByClass(String,String)
	 */
	@Deprecated
	public List<Flow> getFlowsByClass( String mainClass, String subClass ) {
		return this.getByClass( mainClass, subClass );
	}

	/**
	 * Get number of flows by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get flow by
	 * @param subClass
	 *            sub class to get flow by
	 * @return number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getNumberByClass(String,String)
	 */
	@Deprecated
	public long getNumberOfFlowsInClass( String mainClass, String subClass ) {
		return this.getNumberByClass( mainClass, subClass );
	}

}
