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

package de.iai.ilcd.model.flowproperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;

import de.fzk.iai.ilcd.service.model.IFlowPropertyVO;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.flowproperty.IUnitGroupType;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.model.unitgroup.UnitGroup;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "flowproperty", uniqueConstraints = @UniqueConstraint( columnNames = { "UUID", "MAJORVERSION", "MINORVERSION", "SUBMINORVERSION" } ) )
@AssociationOverrides( {
		@AssociationOverride( name = "description", joinTable = @JoinTable( name = "flowproperty_description" ), joinColumns = @JoinColumn( name = "flowproperty_id" ) ),
		@AssociationOverride( name = "name", joinTable = @JoinTable( name = "flowproperty_name" ), joinColumns = @JoinColumn( name = "flowproperty_id" ) ) } )
public class FlowProperty extends DataSet implements Serializable, IFlowPropertyVO {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 7473119545025737038L;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "flowproperty_synonyms", joinColumns = @JoinColumn( name = "flowproperty_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> synonyms = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter synonymsAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return FlowProperty.this.synonyms;
		}
	} );

	@ManyToOne( cascade = CascadeType.ALL )
	protected GlobalReference referenceToUnitGroup;

	@ManyToOne( fetch = FetchType.LAZY )
	protected UnitGroup unitGroup;

	/**
	 * The data stocks this flow properties is contained in
	 */
	@ManyToMany( mappedBy = "flowProperties", fetch = FetchType.LAZY )
	protected Set<DataStock> containingDataStocks = new HashSet<DataStock>();

	/**
	 * Cache for the default unit group.
	 * 20 character limit should be sufficient
	 */
	@SuppressWarnings( "unused" )
	// only for query efficiency
	@Basic
	@Column( name = "defaultUnitGroup_cache", length = 20 )
	private String defaultUnitGroupCache;

	/**
	 * Cache for the default unit.
	 * 20 character limit should be sufficient
	 */
	@SuppressWarnings( "unused" )
	// only for query efficiency
	@Basic
	@Column( name = "defaultUnit_cache", length = 10 )
	private String defaultUnitCache;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addSelfToDataStock( DataStock stock ) {
		stock.addFlowProperty( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeSelfFromDataStock( DataStock stock ) {
		stock.removeFlowProperty( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<DataStock> getContainingDataStocks() {
		return this.containingDataStocks;
	}

	@Override
	public IMultiLangString getSynonyms() {
		return this.synonymsAdapter;
	}

	public void setSynonyms( IMultiLangString synonyms ) {
		this.synonymsAdapter.overrideValues( synonyms );
	}

	public GlobalReference getReferenceToUnitGroup() {
		return this.referenceToUnitGroup;
	}

	public void setReferenceToUnitGroup( GlobalReference referenceToUnitGroup ) {
		this.referenceToUnitGroup = referenceToUnitGroup;
	}

	public UnitGroup getUnitGroup() {
		return this.unitGroup;
	}

	public void setUnitGroup( UnitGroup unitGroup ) {
		this.unitGroup = unitGroup;
	}

	public IMultiLangString getUnitGroupName() {
		IMultiLangString unitGroupName = null;

		if ( this.getUnitGroup() != null ) {
			unitGroupName = this.getUnitGroup().getName();
		}
		else if ( this.getReferenceToUnitGroup() != null ) {
			unitGroupName = this.getReferenceToUnitGroup().getShortDescription();
		}

		return unitGroupName;
	}

	public String getDefaultUnit() {
		if ( this.getUnitGroup() != null ) {
			return this.getUnitGroup().getReferenceUnit().getName();
		}
		// OK, we can't find the unit group, let's use the GlobalReference
		if ( this.referenceToUnitGroup != null ) {
			return this.referenceToUnitGroup.getShortDescription().getDefaultValue();
		}
		// No chance, for meaningful information
		return null;
	}

	@Override
	public IUnitGroupType getUnitGroupDetails() {
		return new UnitGroupDetails( this.unitGroup, this.referenceToUnitGroup );
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.flowproperty.FlowProperty[id=" + this.id + "]";
	}

	/**
	 * Apply cache fields for flow property, those are:
	 * <ul>
	 * <li>{@link #getDefaultUnit()}</li>
	 * <li>{@link #getUnitGroupName()}</li>
	 * </ul>
	 */
	@Override
	@PrePersist
	protected void applyDataSetCache() {
		super.applyDataSetCache();
		IMultiLangString tmp = this.getUnitGroupName();
		if ( tmp != null ) {
			this.defaultUnitGroupCache = StringUtils.substring( tmp.getDefaultValue(), 0, 20 );
		}
		else {
			this.defaultUnitGroupCache = null;
		}
		this.defaultUnitCache = StringUtils.substring( this.getDefaultUnit(), 0, 10 );
	}

}
