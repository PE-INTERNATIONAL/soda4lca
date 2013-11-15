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
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.flow.IReferenceFlowPropertyType;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.flowproperty.FlowProperty;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "flowpropertydescription" )
public class FlowPropertyDescription implements Serializable, IReferenceFlowPropertyType {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	protected int internalId;

	@ManyToOne( fetch = FetchType.LAZY )
	protected FlowProperty flowProperty;

	@ManyToOne( cascade = CascadeType.ALL )
	protected GlobalReference flowPropertyRef;

	protected double meanValue;

	protected double minValue;

	@Column( name = "maximumValue" )
	protected double maxValue;

	protected String uncertaintyType;

	protected float standardDeviation;

	protected String derivationType;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "flowpropertydescription_description", joinColumns = @JoinColumn( name = "flowpropertydescription_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> description = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter descriptionAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return FlowPropertyDescription.this.description;
		}
	} );

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getDerivationType() {
		return derivationType;
	}

	public void setDerivationType( String derivationType ) {
		this.derivationType = derivationType;
	}

	public IMultiLangString getDescription() {
		return this.descriptionAdapter;
	}

	public void setDescription( IMultiLangString description ) {
		this.descriptionAdapter.overrideValues( description );
	}

	public FlowProperty getFlowProperty() {
		return flowProperty;
	}

	public void setFlowProperty( FlowProperty flowProperty ) {
		this.flowProperty = flowProperty;
	}

	public IMultiLangString getFlowPropertyName() {
		IMultiLangString name = null;
		if ( getFlowProperty() != null )
			name = getFlowProperty().getName();
		else
			name = getFlowPropertyRef().getShortDescription();

		return name;
	}

	@Override
	public IMultiLangString getName() {
		return this.getFlowPropertyName();
	}

	@Override
	public String getDefaultUnit() {
		if ( getFlowProperty() != null )
			return getFlowProperty().getDefaultUnit();
		else
			return null;
	}

	public String getFlowPropertyUnit() {
		return this.getDefaultUnit();
	}

	public GlobalReference getFlowPropertyRef() {
		return flowPropertyRef;
	}

	@Override
	public GlobalReference getReference() {
		return this.getFlowPropertyRef();
	}

	public void setFlowPropertyRef( GlobalReference flowPropertyRef ) {
		this.flowPropertyRef = flowPropertyRef;
	}

	public int getInternalId() {
		return internalId;
	}

	public void setInternalId( int internalId ) {
		this.internalId = internalId;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue( double maxValue ) {
		this.maxValue = maxValue;
	}

	public double getMeanValue() {
		return meanValue;
	}

	public void setMeanValue( double meanValue ) {
		this.meanValue = meanValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue( double minValue ) {
		this.minValue = minValue;
	}

	public float getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation( float standardDeviation ) {
		this.standardDeviation = standardDeviation;
	}

	public String getUncertaintyType() {
		return uncertaintyType;
	}

	public void setUncertaintyType( String uncertaintyType ) {
		this.uncertaintyType = uncertaintyType;
	}

	// TODO: generate it from configuration information
	@Override
	public String getHref() {
		return "";
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof FlowPropertyDescription) ) {
			return false;
		}
		FlowPropertyDescription other = (FlowPropertyDescription) object;
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.flow.FlowPropertyDescription[id=" + id + "]";
	}

}
