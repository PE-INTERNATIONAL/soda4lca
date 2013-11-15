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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;

import de.fzk.iai.ilcd.service.model.IProcessVO;
import de.fzk.iai.ilcd.service.model.common.ILString;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.CompletenessValue;
import de.fzk.iai.ilcd.service.model.enums.TypeOfProcessValue;
import de.fzk.iai.ilcd.service.model.enums.TypeOfQuantitativeReferenceValue;
import de.fzk.iai.ilcd.service.model.process.IComplianceSystem;
import de.fzk.iai.ilcd.service.model.process.IQuantitativeReference;
import de.fzk.iai.ilcd.service.model.process.IReferenceFlow;
import de.fzk.iai.ilcd.service.model.process.IReview;
import de.iai.ilcd.model.common.Classification;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.common.MultiLanguageString;
import de.iai.ilcd.model.common.Uuid;
import de.iai.ilcd.model.common.exception.FormatException;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;
import de.iai.ilcd.webgui.controller.ui.ComplianceUtilHandler;
import de.iai.ilcd.webgui.controller.ui.ComplianceUtilHandler.ComplianceSystemCode;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "process", uniqueConstraints = @UniqueConstraint( columnNames = { "UUID", "MAJORVERSION", "MINORVERSION", "SUBMINORVERSION" } ) )
@AssociationOverrides( {
		@AssociationOverride( name = "description", joinTable = @JoinTable( name = "process_description" ), joinColumns = @JoinColumn( name = "process_id" ) ),
		@AssociationOverride( name = "name", joinTable = @JoinTable( name = "process_name" ), joinColumns = @JoinColumn( name = "process_id" ) ) } )
public class Process extends DataSet implements Serializable, IProcessVO {

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "processname_base", joinColumns = @JoinColumn( name = "process_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> baseName = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter basePartAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Process.this.baseName;
		}
	} );

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "processname_route", joinColumns = @JoinColumn( name = "process_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> nameRoute = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter routePartAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Process.this.nameRoute;
		}
	} );

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "processname_location", joinColumns = @JoinColumn( name = "process_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> nameLocation = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter locationPartAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Process.this.nameLocation;
		}
	} );

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "processname_unit", joinColumns = @JoinColumn( name = "process_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> nameUnit = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter unitPartAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Process.this.nameUnit;
		}
	} );

	public IMultiLangString getBaseName() {
		return this.basePartAdapter;
	}

	public void setBaseName( IMultiLangString basePart ) {
		this.basePartAdapter.overrideValues( basePart );
	}

	public IMultiLangString getNameLocation() {
		return this.locationPartAdapter;
	}

	public void setNameLocation( IMultiLangString locationPart ) {
		this.locationPartAdapter.overrideValues( locationPart );
	}

	public IMultiLangString getNameRoute() {
		return this.routePartAdapter;
	}

	public void setNameRoute( IMultiLangString routePart ) {
		this.routePartAdapter.overrideValues( routePart );
	}

	public IMultiLangString getNameUnit() {
		return this.unitPartAdapter;
	}

	public void setNameUnit( IMultiLangString unitPart ) {
		this.unitPartAdapter.overrideValues( unitPart );
	}

	public IMultiLangString getFullName() {
		StringBuffer name = new StringBuffer();

		if ( baseName != null && basePartAdapter.getDefaultValue() != null ) {
			name.append( basePartAdapter.getDefaultValue() );
		}
		if ( nameRoute != null && routePartAdapter.getDefaultValue() != null ) {
			name.append( ";" + routePartAdapter.getDefaultValue() );
		}
		if ( nameLocation != null && locationPartAdapter.getDefaultValue() != null ) {
			name.append( ";" + locationPartAdapter.getDefaultValue() );
		}
		if ( nameUnit != null && unitPartAdapter.getDefaultValue() != null ) {
			name.append( ";" + unitPartAdapter.getDefaultValue() );
		}

		MultiLanguageString joinedName = new MultiLanguageString( name.toString() );
		for ( ILString lString : basePartAdapter.getLStrings() ) {
			String language = lString.getLang();
			if ( language == null )
				continue;
			String baseValue = lString.getValue();
			StringBuilder joinedValue = new StringBuilder();

			if ( baseValue != null ) {
				joinedValue.append( baseValue );
			}
			if ( nameRoute != null && routePartAdapter.getValue( language ) != null ) {
				joinedValue.append( ";" ).append( routePartAdapter.getValue( language ) );
			}
			if ( nameLocation != null && locationPartAdapter.getValue( language ) != null ) {
				joinedValue.append( ";" ).append( locationPartAdapter.getValue( language ) );
			}
			if ( nameUnit != null && unitPartAdapter.getValue( language ) != null ) {
				joinedValue.append( ";" ).append( unitPartAdapter.getValue( language ) );
			}

			joinedName.addLString( language, joinedValue.toString() );

		}

		return joinedName;
	}

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "process_synonyms", joinColumns = @JoinColumn( name = "process_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> synonyms = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter synonymsAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Process.this.synonyms;
		}
	} );

	@Enumerated( EnumType.STRING )
	protected TypeOfProcessValue type;

	protected boolean parameterized;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "process_useadvice", joinColumns = @JoinColumn( name = "process_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> useAdvice = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter useAdviceAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Process.this.useAdvice;
		}
	} );

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "process_technicalpurpose", joinColumns = @JoinColumn( name = "process_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> technicalPurpose = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter technicalPurposeAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Process.this.technicalPurpose;
		}
	} );

	@OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	protected TimeInformation timeInformation;

	@OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	protected Geography geography;

	protected String format;

	@ManyToOne( cascade = CascadeType.ALL )
	protected GlobalReference ownerReference;

	@OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	protected LCIMethodInformation lCIMethodInformation;

	@Enumerated( EnumType.STRING )
	protected CompletenessValue completeness;

	@OneToMany( cascade = CascadeType.ALL, targetEntity = Review.class )
	protected List<IReview> reviews = new ArrayList<IReview>();

	@OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	protected AccessInformation accessInformation;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ComplianceSystem.class )
	protected Set<IComplianceSystem> complianceSystems = new HashSet<IComplianceSystem>();

	@ManyToOne( cascade = CascadeType.ALL )
	protected GlobalReference approvedBy;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@OrderBy( "internalId" )
	protected List<Exchange> exchanges = new LinkedList<Exchange>();

	@OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	protected InternalQuantitativeReference internalReference;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	protected List<LciaResult> lciaResults = new LinkedList<LciaResult>();

	protected boolean resultsIncluded;

	protected boolean exchangesIncluded;

	/**
	 * Flag to indicate if product model is contained
	 */
	@Basic
	@Column( name = "containsProductModel" )
	private boolean containsProductModel;

	/**
	 * The data stocks this process is contained in
	 */
	@ManyToMany( mappedBy = "processes", fetch = FetchType.LAZY )
	protected Set<DataStock> containingDataStocks = new HashSet<DataStock>();

	@Transient
	boolean accessRestricted = false;

	/**
	 * Cache for the LCI method information - used for filtering &amp; order in queries.
	 * 20 character limit should be sufficient.
	 */
	@SuppressWarnings( "unused" )
	// only for query efficiency
	@Basic
	@Column( name = "lciMethodInformation_cache", length = 20 )
	private String lciMethodInformationCache;

	/**
	 * Cache for the LCI method information - used for filtering &amp; order in queries.
	 * 1 character limit is sufficient (due to {@link ComplianceSystemCode#toString()}).
	 * Used for JSF display as well, because no truncation is being risked.
	 */
	@Basic
	@Column( name = "complianceSystem_cache", length = 1 )
	private String complianceSystemCache;

	public Process() {
	}

	// TODO this is VERY ugly
	public Process( IProcessVO voProcess ) {
		this.setUuid( new Uuid( voProcess.getUuidAsString() ) );
		this.setPermanentUri( voProcess.getPermanentUri() );
		this.setHref( voProcess.getHref() );
		String[] nameParts = voProcess.getDefaultName().split( ";" );
		if ( nameParts.length > 0 ) {
			this.setBaseName( new MultiLangStringMapAdapter( nameParts[0] ) );
		}
		if ( nameParts.length > 1 ) {
			this.setNameRoute( new MultiLangStringMapAdapter( nameParts[1] ) );
		}
		if ( nameParts.length > 2 ) {
			this.setNameLocation( new MultiLangStringMapAdapter( nameParts[2] ) );
		}
		if ( nameParts.length > 3 ) {
			this.setNameUnit( new MultiLangStringMapAdapter( nameParts[3] ) );
		}
		if ( voProcess.getSynonyms() != null ) {
			this.setSynonyms( voProcess.getSynonyms() );
		}
		if ( voProcess.getClassification() != null ) {
			this.setClassification( new Classification( voProcess.getClassification() ) );
		}
		if ( voProcess.getType() != null ) {
			this.setType( voProcess.getType() );
		}
		this.setParameterized( voProcess.getParameterized() );
		if ( voProcess.getDescription() != null ) {
			this.setDescription( voProcess.getDescription() );
		}
		if ( voProcess.getUseAdvice() != null ) {
			this.setUseAdvice( voProcess.getUseAdvice() );
		}
		if ( voProcess.getLocation() != null ) {
			Geography poGeography = new Geography();
			poGeography.setLocation( voProcess.getLocation() );
			this.setGeography( poGeography );
		}
		TimeInformation poTimeInformation = new TimeInformation();
		if ( voProcess.getTimeInformation() != null && voProcess.getTimeInformation().getReferenceYear() != null ) {
			poTimeInformation.setReferenceYear( voProcess.getTimeInformation().getReferenceYear() );
		}
		if ( voProcess.getTimeInformation() != null && voProcess.getTimeInformation().getValidUntil() != null ) {
			poTimeInformation.setValidUntil( voProcess.getTimeInformation().getValidUntil() );
		}
		this.setTimeInformation( poTimeInformation );
		if ( voProcess.getDataSetVersion() != null ) {
			DataSetVersion dataSetVersion = new DataSetVersion();
			try {
				dataSetVersion = DataSetVersion.parse( voProcess.getDataSetVersion() );
			}
			catch ( FormatException ex ) {
				// ignore; this should not happen
			}
			this.setVersion( dataSetVersion );
		}
		if ( voProcess.getReviews() != null && voProcess.getReviews().size() > 0 ) {
			this.setReviews( voProcess.getReviews() );
			// this.setFormat("ILCD format");
		}
	}

	/**
	 * Apply cache fields for process, those are cached values for:
	 * <ul>
	 * <li>{@link #getLCIMethodInformation()}</li>
	 * </ul>
	 */
	@Override
	@PrePersist
	protected void applyDataSetCache() {
		super.applyDataSetCache();
		if ( this.lCIMethodInformation != null && this.lCIMethodInformation.getMethodPrinciple() != null ) {
			this.lciMethodInformationCache = StringUtils.substring( this.lCIMethodInformation.getMethodPrinciple().value(), 0, 20 );
		}
		else {
			this.lciMethodInformationCache = null;
		}
		// TODO: use dependency injection later
		ComplianceUtilHandler compHandler = new ComplianceUtilHandler();
		this.complianceSystemCache = StringUtils.substring( compHandler.getComplianceCodeAsString( this.complianceSystems ), 0, 1 );
	}

	/**
	 * Get process name for the cache instead of {@link DataSet#getName()}.
	 * 
	 * @return the {@link #getProcessName() process name}
	 */
	@Override
	protected String getNameAsStringForCache() {
		return this.getName().getValue();
	}

	/**
	 * Get the cached value for the highest compliance system
	 * 
	 * @return cached value for the highest compliance system
	 */
	public String getComplianceSystemCache() {
		return this.complianceSystemCache;
	}

	@Override
	public AccessInformation getAccessInformation() {
		return this.accessInformation;
	}

	public void setAccessInformation( AccessInformation accessInformation ) {
		this.accessInformation = accessInformation;
	}

	public CompletenessValue getCompleteness() {
		return this.completeness;
	}

	@Override
	public CompletenessValue getCompletenessProductModel() {
		return this.completeness;
	}

	public void setCompleteness( CompletenessValue completeness ) {
		this.completeness = completeness;
	}

	public List<Exchange> getExchanges() {
		return this.exchanges;
	}

	public List<Exchange> getExchanges( String direction ) {
		List<Exchange> filtered = new LinkedList<Exchange>();

		for ( Exchange exchange : this.exchanges ) {
			if ( exchange.getExchangeDirection() != null && exchange.getExchangeDirection().toString().equals( direction ) ) {
				filtered.add( exchange );
			}
		}

		return filtered;
	}

	protected void setExchanges( List<Exchange> exchanges ) {
		this.exchanges = exchanges;
	}

	public void addExchange( Exchange exchange ) {
		this.exchanges.add( exchange );
	}

	@Override
	public Set<IComplianceSystem> getComplianceSystems() {
		return this.complianceSystems;
	}

	/**
	 * Convenience method for returning compliance systems as List in order to user p:dataList (primefaces)
	 * 
	 * @return List of compliance systems
	 */
	public List<IComplianceSystem> getComplianceSystemsAsList() {
		return new ArrayList<IComplianceSystem>( this.getComplianceSystems() );
	}

	protected void setComplianceSystems( Set<IComplianceSystem> compliances ) {
		this.complianceSystems = compliances;
	}

	public void addComplianceSystem( ComplianceSystem compliance ) {
		this.complianceSystems.add( compliance );
	}

	@Override
	public GlobalReference getApprovedBy() {
		return this.approvedBy;
	}

	public void setApprovedBy( GlobalReference approvedByReference ) {
		this.approvedBy = approvedByReference;
	}

	@Override
	public String getFormat() {
		return this.format;
	}

	public void setFormat( String format ) {
		this.format = format;
	}

	public Geography getGeography() {
		return this.geography;
	}

	public void setGeography( Geography geography ) {
		this.geography = geography;
	}

	@Override
	public String getLocation() {
		if ( this.geography != null ) {
			return this.geography.getLocation();
		}
		else {
			return null;
		}
	}

	@Override
	public Boolean getParameterized() {
		return this.parameterized;
	}

	public void setParameterized( boolean parameterized ) {
		this.parameterized = parameterized;
	}

	public boolean isResultsIncluded() {
		return this.resultsIncluded;
	}

	@Override
	public Boolean getHasResults() {
		return this.resultsIncluded;
	}

	public void setResultsIncluded( boolean resultsIncluded ) {
		this.resultsIncluded = resultsIncluded;
	}

	public boolean isExchangesIncluded() {
		return this.exchangesIncluded;
	}

	public void setExchangesIncluded( boolean exchangesIncluded ) {
		this.exchangesIncluded = exchangesIncluded;
	}

	@Override
	public LCIMethodInformation getLCIMethodInformation() {
		return this.lCIMethodInformation;
	}

	public void setLCIMethodInformation( LCIMethodInformation lciMethodInformation ) {
		this.lCIMethodInformation = lciMethodInformation;
	}

	public List<LciaResult> getLciaResults() {
		return this.lciaResults;
	}

	protected void setLciaResults( List<LciaResult> lciaResults ) {
		this.lciaResults = lciaResults;
	}

	public void addLciaResult( LciaResult result ) {
		this.lciaResults.add( result );
	}

	@Override
	public GlobalReference getOwnerReference() {
		return this.ownerReference;
	}

	public void setOwnerReference( GlobalReference ownerReference ) {
		this.ownerReference = ownerReference;
	}

	@Override
	public IMultiLangString getName() {
		return getFullName();
	}

	public InternalQuantitativeReference getInternalReference() {
		return this.internalReference;
	}

	public void setInternalReference( InternalQuantitativeReference internalReference ) {
		this.internalReference = internalReference;
	}

	@Override
	public IQuantitativeReference getQuantitativeReference() {
		return new QuantitativeReference( this.internalReference, this.exchanges );
	}

	public List<Exchange> getReferenceExchanges() {
		List<Exchange> refExchanges = new ArrayList<Exchange>();

		if ( this.internalReference == null || this.internalReference.referenceIds == null ) {
			return refExchanges;
		}
		if ( this.internalReference.referenceIds.size() <= 0 ) {
			return refExchanges;
		}
		for ( Integer intObject : this.internalReference.referenceIds ) {
			for ( Exchange exchange : this.exchanges ) {
				// because we cannot guarantie that internId's runnin from 0 and are continously allocated, we have to
				// search for them
				if ( exchange.getInternalId() == intObject.intValue() ) {
					refExchanges.add( exchange );
					break;
				}
			}
		}
		return refExchanges;
	}

	public IMultiLangString getOtherReference() {
		if ( this.internalReference == null ) {
			return null;
		}
		return this.internalReference.getOtherReference();
	}

	@Override
	public List<IReview> getReviews() {
		return this.reviews;
	}

	protected void setReviews( List<IReview> reviews ) {
		this.reviews = reviews;
	}

	public void addReview( Review review ) {
		if ( !this.reviews.contains( review ) ) {
			this.reviews.add( review );
		}
	}

	@Override
	public IMultiLangString getSynonyms() {
		return this.synonymsAdapter;
	}

	public void setSynonyms( IMultiLangString synonyms ) {
		this.synonymsAdapter.overrideValues( synonyms );
	}

	@Override
	public TimeInformation getTimeInformation() {
		return this.timeInformation;
	}

	public void setTimeInformation( TimeInformation timeInformation ) {
		this.timeInformation = timeInformation;
	}

	@Override
	public TypeOfProcessValue getType() {
		return this.type;
	}

	public void setType( TypeOfProcessValue type ) {
		this.type = type;
	}

	@Override
	public IMultiLangString getUseAdvice() {
		return this.useAdviceAdapter;
	}

	public void setUseAdvice( IMultiLangString useAdvice ) {
		this.useAdviceAdapter.overrideValues( useAdvice );
	}

	@Override
	public IMultiLangString getTechnicalPurpose() {
		return this.technicalPurposeAdapter;
	}

	public void setTechnicalPurpose( IMultiLangString technicalPurpose ) {
		this.technicalPurposeAdapter.overrideValues( technicalPurpose );
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.process.Process[id=" + this.id + "]";
	}

	@Override
	public String getOverallQuality() {
		String overallQuality = null;

		return overallQuality;
	}

	@Override
	public boolean isAccessRestricted() {
		return this.accessRestricted;
	}

	@Override
	public void setAccessRestricted( boolean restricted ) {
		this.accessRestricted = restricted;
	}

	@Override
	public Set<DataStock> getContainingDataStocks() {
		return this.containingDataStocks;
	}

	protected void setContainingDataStocks( Set<DataStock> containingDataStocks ) {
		this.containingDataStocks = containingDataStocks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addSelfToDataStock( DataStock stock ) {
		stock.addProcess( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeSelfFromDataStock( DataStock stock ) {
		stock.removeProcess( this );
	}

	/**
	 * Get the flag to indicate if product model is contained
	 * 
	 * @return <code>true</code> if product model contained, <code>false</code> otherwise
	 */
	public boolean isContainsProductModel() {
		return this.containsProductModel;
	}

	/**
	 * Set the flag to indicate if product model is contained
	 * 
	 * @param containsProductModel
	 *            new value
	 */
	public void setContainsProductModel( boolean containsProductModel ) {
		this.containsProductModel = containsProductModel;
	}

	/**
	 * Get the flag to indicate if product model is contained
	 * 
	 * @return <code>{@link Boolean#TRUE}</code> if product model contained, <code>{@link Boolean#FALSE}</code>
	 *         otherwise
	 */
	@Override
	public Boolean getContainsProductModel() {
		return this.containsProductModel ? Boolean.TRUE : Boolean.FALSE;
	}
}

class QuantitativeReference implements Serializable, IQuantitativeReference {

	List<Exchange> exchanges;

	InternalQuantitativeReference internalReference;

	public QuantitativeReference( InternalQuantitativeReference internalReference, List<Exchange> exchanges ) {
		this.exchanges = exchanges;
		this.internalReference = internalReference;
	}

	@Override
	public List<IReferenceFlow> getReferenceFlows() {
		List<IReferenceFlow> refExchanges = new ArrayList<IReferenceFlow>();

		if ( this.internalReference == null || this.internalReference.referenceIds == null ) {
			return refExchanges;
		}
		if ( this.internalReference.referenceIds.size() <= 0 ) {
			return refExchanges;
		}
		for ( Integer intObject : this.internalReference.referenceIds ) {
			for ( Exchange exchange : this.exchanges ) {
				// because we cannot guarantie that internId's runnin from 0 and are continously allocated, we have to
				// search for them
				if ( exchange.getInternalId() == intObject.intValue() ) {
					refExchanges.add( exchange );
					break;
				}
			}
		}
		return refExchanges;
	}

	@Override
	public IMultiLangString getFunctionalUnit() {
		if ( this.internalReference != null ) {
			return this.internalReference.getOtherReference();
		}
		else {
			return null;
		}
	}

	@Override
	public TypeOfQuantitativeReferenceValue getType() {
		if ( this.internalReference != null ) {
			return this.internalReference.getType();
		}
		else {
			return null;
		}
	}
}
