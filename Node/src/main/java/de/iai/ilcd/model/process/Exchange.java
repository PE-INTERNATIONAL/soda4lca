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

package de.iai.ilcd.model.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IGlobalReference;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.process.IReferenceFlow;
import de.iai.ilcd.model.common.Classification;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.flowproperty.FlowProperty;
import de.iai.ilcd.model.unitgroup.UnitGroup;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "exchange" )
public class Exchange implements Serializable, IReferenceFlow {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	protected int internalId;

	@ManyToOne( cascade = CascadeType.ALL )
	protected GlobalReference flowReference = null;

	@ManyToOne( fetch = FetchType.LAZY )
	protected Flow flow;

	protected String location = "";

	protected String functionType = "";

	@Enumerated( EnumType.STRING )
	protected ExchangeDirection exchangeDirection;

	protected String referenceToVariable;

	protected float meanAmount = 0;

	protected float resultingAmount = 0;

	protected float minimumAmount = 0;

	protected float maximumAmount = 0;

	protected String uncertaintyDistribution = "";

	protected float standardDeviation = -1;

	protected String allocation = "";

	protected String dataSource = "";

	protected String derivationType = "";

	@ManyToOne
	protected GlobalReference refToDataSource = null;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "exchange_comment", joinColumns = @JoinColumn( name = "exchange_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> comment = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter commentAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Exchange.this.comment;
		}
	} );

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getAllocation() {
		return allocation;
	}

	public void setAllocation( String allocation ) {
		this.allocation = allocation;
	}

	public IMultiLangString getComment() {
		return this.commentAdapter;
	}

	public void setComment( IMultiLangString comment ) {
		this.commentAdapter.overrideValues( comment );
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource( String dataSource ) {
		this.dataSource = dataSource;
	}

	public String getDerivationType() {
		return derivationType;
	}

	public void setDerivationType( String derivationType ) {
		this.derivationType = derivationType;
	}

	public ExchangeDirection getExchangeDirection() {
		return exchangeDirection;
	}

	public void setExchangeDirection( ExchangeDirection exchangeDirection ) {
		this.exchangeDirection = exchangeDirection;
	}

	public GlobalReference getFlowReference() {
		return flowReference;
	}

	@Override
	public IGlobalReference getReference() {
		return this.getFlowReference();
	}

	public void setFlowReference( GlobalReference flowReference ) {
		this.flowReference = flowReference;
	}

	public Flow getFlow() {
		return flow;
	}

	public void setFlow( Flow flow ) {
		this.flow = flow;
	}

	public String getFunctionType() {
		return functionType;
	}

	public void setFunctionType( String functionType ) {
		this.functionType = functionType;
	}

	public int getInternalId() {
		return internalId;
	}

	public void setInternalId( int internalId ) {
		this.internalId = internalId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation( String location ) {
		this.location = location;
	}

	public float getMaximumAmount() {
		return maximumAmount;
	}

	public void setMaximumAmount( float maximumAmount ) {
		this.maximumAmount = maximumAmount;
	}

	public float getMeanAmount() {
		return meanAmount;
	}

	@Override
	public float getMeanValue() {
		return this.getMeanAmount();
	}

	public void setMeanAmount( float meanAmount ) {
		this.meanAmount = meanAmount;
	}

	public float getMinimumAmount() {
		return minimumAmount;
	}

	public void setMinimumAmount( float minimumAmount ) {
		this.minimumAmount = minimumAmount;
	}

	public GlobalReference getRefToDataSource() {
		return refToDataSource;
	}

	public void setRefToDataSource( GlobalReference refToDataSource ) {
		this.refToDataSource = refToDataSource;
	}

	public String getReferenceToVariable() {
		return referenceToVariable;
	}

	public void setReferenceToVariable( String referenceToVariable ) {
		this.referenceToVariable = referenceToVariable;
	}

	public float getResultingAmount() {
		return resultingAmount;
	}

	public void setResultingAmount( float resultingAmount ) {
		this.resultingAmount = resultingAmount;
	}

	public float getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation( float standardDeviation ) {
		this.standardDeviation = standardDeviation;
	}

	public String getUncertaintyDistribution() {
		return uncertaintyDistribution;
	}

	public void setUncertaintyDistribution( String uncertaintyDistribution ) {
		this.uncertaintyDistribution = uncertaintyDistribution;
	}

	@Override
	public IMultiLangString getFlowName() {
		if ( flow != null )
			return flow.getName();
		else {
			return flowReference.getShortDescription();
		}
	}

	public String getFlowType() {
		if ( flow != null )
			return flow.getType().getValue();
		else
			return null;
	}

	public Classification getClassification() {
		Classification classification = null;
		if ( flow != null ) {
			if ( flow.getType().equals( flow.getType().ELEMENTARY_FLOW ) )
				classification = flow.getCategorization();
			else
				classification = flow.getClassification();
		}
		return classification;
	}

	public String getClassificationAsString() {

		Classification classification = this.getClassification();
		if ( classification != null )
			return classification.getClassHierarchyAsString();

		return null;
	}

	public IMultiLangString getReferenceFlowPropertyName() {
		if ( flow == null )
			return null;
		FlowProperty referenceProperty = flow.getReferenceProperty().getFlowProperty();
		if ( referenceProperty != null )
			return referenceProperty.getName();
		else {
			return flow.getReferenceProperty().getFlowPropertyRef().getShortDescription();
		}
	}

	@Override
	public IMultiLangString getFlowPropertyName() {
		return this.getReferenceFlowPropertyName();
	}

	public String getReferenceUnit() {
		if ( flow == null || flow.getReferenceProperty() == null || flow.getReferenceProperty().getFlowProperty() == null ) {
			return null;
		}
		else {
			// TODO: review consistency check
			FlowProperty referenceProperty = flow.getReferenceProperty().getFlowProperty();
			UnitGroup unitGroup = referenceProperty.getUnitGroup();
			if ( unitGroup == null )
				return null;
			else
				return unitGroup.getReferenceUnit().getName();
		}
	}

	@Override
	public String getUnit() {
		return this.getReferenceUnit();
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
		if ( !(object instanceof Exchange) ) {
			return false;
		}
		Exchange other = (Exchange) object;
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.process.Exchange[id=" + id + "]";
	}

}
