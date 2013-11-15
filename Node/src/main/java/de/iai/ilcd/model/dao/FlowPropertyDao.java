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

import org.apache.velocity.tools.generic.ValueParser;

import de.fzk.iai.ilcd.service.model.IFlowPropertyListVO;
import de.fzk.iai.ilcd.service.model.IFlowPropertyVO;
import de.iai.ilcd.model.flowproperty.FlowProperty;
import de.iai.ilcd.model.unitgroup.UnitGroup;

/**
 * Data access object for {@link FlowProperty flow properties}
 */
public class FlowPropertyDao extends DataSetDao<FlowProperty, IFlowPropertyListVO, IFlowPropertyVO> {

	/**
	 * Create the data access object for {@link FlowProperty flow properties}
	 */
	public FlowPropertyDao() {
		super( "FlowProperty", FlowProperty.class, IFlowPropertyListVO.class, IFlowPropertyVO.class );
	}

	protected String getDataStockField() {
		return "flowproperties";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preCheckAndPersist( FlowProperty dataSet ) {
		this.attachUnitGroup( dataSet );
	}

	/**
	 * Get flow property by UUID. Overrides {@link DataSetDao} implementation because
	 * {@link #attachUnitGroup(FlowProperty)} is called after loading.
	 */
	@Override
	public FlowProperty getByUuid( String uuid ) {
		FlowProperty fp = super.getByUuid( uuid );
		if ( fp != null ) {
			this.attachUnitGroup( fp );
		}
		return fp;
	}

	private void attachUnitGroup( FlowProperty flowprop ) {
		UnitGroupDao unitGroupDao = new UnitGroupDao();
		if ( flowprop != null && flowprop.getReferenceToUnitGroup() != null ) {
			String uuid = flowprop.getReferenceToUnitGroup().getUuid().getUuid();
			UnitGroup unitGroup = unitGroupDao.getByUuid( uuid );
			flowprop.setUnitGroup( unitGroup );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryStringOrderJpql( String typeAlias, String sortString ) {
		if ( "classification.classHierarchyAsString".equals( sortString ) ) {
			return typeAlias + ".classificationCache";
		}
		else if ( "unitGroupName.value".equals( sortString ) ) {
			return typeAlias + ".unitGroup.name.value";
		}
		else if ( "defaultUnit".equals( sortString ) ) {
			return typeAlias + ".unitGroup.referenceUnit.name";
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
		// nothing to do beside the defaults
	}

	/*
	 * ================================================== == DEPRECATED METHODS / BACKWARDS COMPATIBILITY ==
	 * ==================================================
	 */

	/**
	 * Get list of all contacts
	 * 
	 * @return list of all contacts
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getAll()}.
	 * @see #getAll()
	 */
	public List<FlowProperty> getFlowProperties() {
		return this.getAll();
	}

	/**
	 * Get flow property by UUID
	 * 
	 * @return flow property with provided UUID
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByUuid(String)}.
	 * @see #getByUuid(String)
	 */
	@Deprecated
	public FlowProperty getFlowProperty( String uuid ) {
		return this.getByUuid( uuid );
	}

	/**
	 * Get the flow property with the provided JPA id
	 * 
	 * @param datasetId
	 *            JPA id
	 * @return flow property with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public FlowProperty getFlowProperty( long datasetId ) {
		return this.getByDataSetId( datasetId );
	}

	/**
	 * Get the flow property with the provided JPA id
	 * 
	 * @param id
	 *            JPA id as string
	 * @return flow property with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(String)}.
	 * @see #getByDataSetId(String)
	 */
	@Deprecated
	public FlowProperty getFlowPropertyById( String id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get the flow property with the provided JPA id
	 * 
	 * @param id
	 *            JPA id
	 * @return flow property with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public FlowProperty getFlowPropertyById( Long id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get flow properties by main class
	 * 
	 * @param mainClass
	 *            main class to get flow property by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getByClass(String)
	 */
	@Deprecated
	public List<FlowProperty> getFlowPropertiesByClass( String mainClass ) {
		return this.getByClass( mainClass );
	}

	/**
	 * Get the number of flow properties by main class
	 * 
	 * @param mainClass
	 *            main class to get flow property by
	 * @return the number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getNumberByClass(String)
	 */
	@Deprecated
	public long getNumberOfFlowProperties( String mainClass ) {
		return this.getNumberByClass( mainClass );
	}

	/**
	 * Get flow properties by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get flow property by
	 * @param subClass
	 *            sub class to get flow property by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getByClass(String,String)
	 */
	@Deprecated
	public List<FlowProperty> getFlowPropertiesByClass( String mainClass, String subClass ) {
		return this.getByClass( mainClass, subClass );
	}

	/**
	 * Get number of flow properties by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get flow property by
	 * @param subClass
	 *            sub class to get flow property by
	 * @return number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getNumberByClass(String,String)
	 */
	@Deprecated
	public long getNumberOfFlowProperties( String mainClass, String subClass ) {
		return this.getNumberByClass( mainClass, subClass );
	}

}
