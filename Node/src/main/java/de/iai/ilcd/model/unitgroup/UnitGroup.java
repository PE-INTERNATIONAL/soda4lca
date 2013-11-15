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

package de.iai.ilcd.model.unitgroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;

import de.fzk.iai.ilcd.service.model.IUnitGroupVO;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.datastock.DataStock;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "unitgroup", uniqueConstraints = @UniqueConstraint( columnNames = { "UUID", "MAJORVERSION", "MINORVERSION", "SUBMINORVERSION" } ) )
@AssociationOverrides( {
		@AssociationOverride( name = "description", joinTable = @JoinTable( name = "unitgroup_description" ), joinColumns = @JoinColumn( name = "unitgroup_id" ) ),
		@AssociationOverride( name = "name", joinTable = @JoinTable( name = "unitgroup_name" ), joinColumns = @JoinColumn( name = "unitgroup_id" ) ) } )
public class UnitGroup extends DataSet implements Serializable, IUnitGroupVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7670192301518075529L;

	@ManyToOne
	Unit referenceUnit;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@OrderBy( "internalId" )
	Set<Unit> units = new HashSet<Unit>();

	/**
	 * Cache for the default unit.
	 * 20 character limit should be sufficient
	 */
	@SuppressWarnings( "unused" )
	// only for query efficiency
	@Basic
	@Column( name = "referenceUnit_cache", length = 10 )
	private String referenceUnitCache;

	/**
	 * The data stocks this unit group is contained in
	 */
	@ManyToMany( mappedBy = "unitGroups", fetch = FetchType.LAZY )
	protected Set<DataStock> containingDataStocks = new HashSet<DataStock>();

	public UnitGroup() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<DataStock> getContainingDataStocks() {
		return this.containingDataStocks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addSelfToDataStock( DataStock stock ) {
		stock.addUnitGroup( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeSelfFromDataStock( DataStock stock ) {
		stock.removeUnitGroup( this );
	}

	public UnitGroup( String name ) {
		this.getName().setValue( name );
	}

	public Set<Unit> getUnits() {
		return this.units;
	}

	/**
	 * Convenience method for returning units as List in order to user p:dataList (primefaces)
	 * 
	 * @return List of units
	 */
	public List<Unit> getUnitsAsList() {
		return new ArrayList<Unit>( this.getUnits() );
	}

	protected void setUnits( Set<Unit> units ) {
		this.units = units;
	}

	public void addUnit( Unit unit ) {
		this.units.add( unit );
	}

	public Unit getReferenceUnit() {
		return this.referenceUnit;
	}

	@Override
	public String getDefaultUnit() {
		return this.referenceUnit.getName();
	}

	public void setReferenceUnit( Unit referenceUnit ) {
		this.referenceUnit = referenceUnit;
	}

	/**
	 * Apply cache fields for unit group, those are:
	 * <ul>
	 * <li>{@link #getDefaultUnit()}</li>
	 * </ul>
	 */
	@Override
	@PrePersist
	protected void applyDataSetCache() {
		super.applyDataSetCache();
		if ( this.referenceUnit != null ) {
			this.referenceUnitCache = StringUtils.substring( this.referenceUnit.getName(), 0, 10 );
		}
		else {
			this.referenceUnitCache = null;
		}
	}
}
