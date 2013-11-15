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

import de.fzk.iai.ilcd.service.model.IUnitGroupListVO;
import de.fzk.iai.ilcd.service.model.IUnitGroupVO;
import de.iai.ilcd.model.unitgroup.UnitGroup;

/**
 * Data access object for {@link UnitGroup unit groups}
 */
public class UnitGroupDao extends DataSetDao<UnitGroup, IUnitGroupListVO, IUnitGroupVO> {

	/**
	 * Create the data access object for {@link UnitGroup unit groups}
	 */
	public UnitGroupDao() {
		super( "UnitGroup", UnitGroup.class, IUnitGroupListVO.class, IUnitGroupVO.class );
	}

	protected String getDataStockField() {
		return "unitgroups";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preCheckAndPersist( UnitGroup dataSet ) {
		// Nothing to to :)
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addWhereClausesAndNamedParamesForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues ) {
		// nothing to do beside the defaults
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryStringOrderJpql( String typeAlias, String sortString ) {
		if ( "classification.classHierarchyAsString".equals( sortString ) ) {
			return typeAlias + ".classificationCache";
		}
		else if ( "referenceUnit.name".equals( sortString ) ) {
			return typeAlias + ".referenceUnit.name";
		}
		else {
			return typeAlias + ".nameCache";
		}
	}

	/*
	 * ================================================== == DEPRECATED METHODS / BACKWARDS COMPATIBILITY ==
	 * ==================================================
	 */

	/**
	 * Get list of all unit groups
	 * 
	 * @return list of all unit groups
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getAll()}.
	 * @see #getAll()
	 */
	public List<UnitGroup> getUnitGroups() {
		return this.getAll();
	}

	/**
	 * Get a unit group by UUID
	 * 
	 * @param uuid
	 *            UUID of flow to get
	 * @return unit group with provided UUID
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByUuid(String)}.
	 * @see #getByUuid(String)
	 */
	@Deprecated
	public UnitGroup getUnitGroup( String uuid ) {
		return this.getByUuid( uuid );
	}

	/**
	 * Get the unit group with the provided JPA id
	 * 
	 * @param datasetId
	 *            JPA id
	 * @return unit group with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public UnitGroup getUnitGroup( long datasetId ) {
		return this.getByDataSetId( datasetId );
	}

	/**
	 * Get the unit group with the provided JPA id
	 * 
	 * @param id
	 *            JPA id as string
	 * @return unit group with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(String)}.
	 * @see #getByDataSetId(String)
	 */
	@Deprecated
	public UnitGroup getUnitGroupById( String id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get the unit group with the provided JPA id
	 * 
	 * @param id
	 *            JPA id
	 * @return unit group with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public UnitGroup getUnitGroupById( Long id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get unit groups by main class
	 * 
	 * @param mainClass
	 *            main class to get unit group by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getByClass(String)
	 */
	@Deprecated
	public List<UnitGroup> getUnitGroupsByClass( String mainClass ) {
		return this.getByClass( mainClass );
	}

	/**
	 * Get the number of unit groups by main class
	 * 
	 * @param mainClass
	 *            main class to get unit group by
	 * @return the number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getNumberByClass(String)
	 */
	@Deprecated
	public long getNumberOfUnitGroups( String mainClass ) {
		return this.getNumberByClass( mainClass );
	}

	/**
	 * Get unit groups by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get unit group by
	 * @param subClass
	 *            sub class to get unit group by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getByClass(String,String)
	 */
	@Deprecated
	public List<UnitGroup> getUnitGroupsByClass( String mainClass, String subClass ) {
		return this.getByClass( mainClass, subClass );
	}

	/**
	 * Get number of unit groups by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get unit group by
	 * @param subClass
	 *            sub class to get unit group by
	 * @return number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getNumberByClass(String,String)
	 */
	@Deprecated
	public long getNumberOfUnitGroups( String mainClass, String subClass ) {
		return this.getNumberByClass( mainClass, subClass );
	}

}
