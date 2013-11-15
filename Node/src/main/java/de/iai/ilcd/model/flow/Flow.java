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

package de.iai.ilcd.model.flow;

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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;

import de.fzk.iai.ilcd.service.model.IFlowVO;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.TypeOfFlowValue;
import de.iai.ilcd.model.common.Classification;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "flow", uniqueConstraints = @UniqueConstraint( columnNames = { "UUID", "MAJORVERSION", "MINORVERSION", "SUBMINORVERSION" } ) )
@AssociationOverrides( {
		@AssociationOverride( name = "description", joinTable = @JoinTable( name = "flow_description" ), joinColumns = @JoinColumn( name = "flow_id" ) ),
		@AssociationOverride( name = "name", joinTable = @JoinTable( name = "flow_name" ), joinColumns = @JoinColumn( name = "flow_id" ) ) } )
public class Flow extends DataSet implements Serializable, IFlowVO {

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "flow_synonyms", joinColumns = @JoinColumn( name = "flow_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> synonyms = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter synonymsAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Flow.this.synonyms;
		}
	} );

	@Enumerated( EnumType.STRING )
	private TypeOfFlowValue type;

	@ManyToOne( cascade = CascadeType.ALL )
	protected Classification categorization = new Classification();

	protected String casNumber;

	protected String sumFormula;

	@ManyToOne( cascade = CascadeType.ALL )
	protected FlowPropertyDescription referenceProperty;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	Set<FlowPropertyDescription> propertyDescriptions = new HashSet<FlowPropertyDescription>();

	/**
	 * The data stocks this flow is contained in
	 */
	@ManyToMany( mappedBy = "flows", fetch = FetchType.LAZY )
	protected Set<DataStock> containingDataStocks = new HashSet<DataStock>();

	/**
	 * Cache for the reference property.
	 * 20 character limit should be sufficient
	 */
	@SuppressWarnings( "unused" )
	// only for query efficiency
	@Basic
	@Column( name = "referenceProperty_cache", length = 20 )
	private String referencePropertyCache;

	/**
	 * Cache for the reference property unit.
	 * 10 character limit should be sufficient
	 */
	@SuppressWarnings( "unused" )
	// only for query efficiency
	@Basic
	@Column( name = "referencePropertyUnit_cache", length = 10 )
	private String referencePropertyUnitCache;

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
		stock.addFlow( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeSelfFromDataStock( DataStock stock ) {
		stock.removeFlow( this );
	}

	@Override
	public String getCasNumber() {
		return this.casNumber;
	}

	public void setCasNumber( String casNumber ) {
		this.casNumber = casNumber;
	}

	public Classification getCategorization() {
		return this.categorization;
	}

	@Override
	public Classification getFlowCategorization() {
		return this.getCategorization();
	}

	public void setCategorization( Classification categorization ) {
		this.categorization = categorization;
	}

	public Set<FlowPropertyDescription> getPropertyDescriptions() {
		return this.propertyDescriptions;
	}

	protected void setPropertyDescriptions( Set<FlowPropertyDescription> propertyDescriptions ) {
		this.propertyDescriptions = propertyDescriptions;
	}

	public void addPropertDesription( FlowPropertyDescription propertyDescription ) {
		if ( !this.propertyDescriptions.contains( propertyDescription ) ) {
			this.propertyDescriptions.add( propertyDescription );
		}
	}

	public FlowPropertyDescription getReferenceProperty() {
		return this.referenceProperty;
	}

	@Override
	public FlowPropertyDescription getReferenceFlowProperty() {
		return this.getReferenceProperty();
	}

	public void setReferenceProperty( FlowPropertyDescription referenceProperty ) {
		this.referenceProperty = referenceProperty;
	}

	@Override
	public String getSumFormula() {
		return this.sumFormula;
	}

	public void setSumFormula( String sumFormula ) {
		this.sumFormula = sumFormula;
	}

	@Override
	public IMultiLangString getSynonyms() {
		return this.synonymsAdapter;
	}

	public void setSynonyms( IMultiLangString synonyms ) {
		this.synonymsAdapter.overrideValues( synonyms );
	}

	@Override
	public TypeOfFlowValue getType() {
		return this.type;
	}

	public void setType( TypeOfFlowValue type ) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.flow.Flow[id=" + this.id + "]";
	}

	/**
	 * Gets the class hierarchy dependent on the type of the flow.
	 * 
	 * @return hierarchy by {@link #getCategorization()} for {@link #isElementaryFlow() elementary flows} else by
	 *         {@link #getClassification()}
	 */
	@Override
	protected String getClassificationHierarchyAsStringForCache() {
		if ( this.isElementaryFlow() ) {
			if ( this.categorization != null ) {
				return this.categorization.getClassHierarchyAsString();
			}
			else {
				return null;
			}
		}
		else {
			return super.getClassificationHierarchyAsStringForCache();
		}
	}

	/**
	 * Apply cache fields for flow, those are:
	 * <ul>
	 * <li>{@link #getReferenceProperty()}</li>
	 * <li>{@link FlowPropertyDescription#getDefaultUnit() default unit} of {@link #getReferenceProperty()}</li>
	 * </ul>
	 */
	@Override
	@PrePersist
	protected void applyDataSetCache() {
		super.applyDataSetCache();
		if ( this.referenceProperty != null ) {
			if ( this.referenceProperty.getFlowPropertyName() != null ) {
				this.referencePropertyCache = StringUtils.substring( this.referenceProperty.getFlowPropertyName().getDefaultValue(), 0, 20 );
			}
			else {
				this.referencePropertyCache = null;
			}
			this.referencePropertyUnitCache = StringUtils.substring( this.referenceProperty.getDefaultUnit(), 0, 10 );
		}
		else {
			this.referencePropertyCache = null;
			this.referencePropertyUnitCache = null;
		}
	}

	public boolean isElementaryFlow() {
		return TypeOfFlowValue.ELEMENTARY_FLOW.equals( this.getType() );
	}

}
