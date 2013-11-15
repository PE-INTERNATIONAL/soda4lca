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

package de.iai.ilcd.model.lciamethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import de.fzk.iai.ilcd.service.model.ILCIAMethodVO;
import de.fzk.iai.ilcd.service.model.enums.AreaOfProtectionValue;
import de.fzk.iai.ilcd.service.model.enums.LCIAImpactCategoryValue;
import de.fzk.iai.ilcd.service.model.enums.TypeOfLCIAMethodValue;
import de.fzk.iai.ilcd.service.model.lciamethod.ITimeInformation;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.datastock.DataStock;

/**
 * <p>
 * Class that represents LCIAMethod entity
 * </p>
 * <p>
 * Please note that this class is no thread safe!
 * </p>
 */
@Entity
@Table( name = "lciamethod", uniqueConstraints = @UniqueConstraint( columnNames = { "UUID", "MAJORVERSION", "MINORVERSION", "SUBMINORVERSION" } ) )
@AssociationOverrides( {
		@AssociationOverride( name = "description", joinTable = @JoinTable( name = "lciamethod_description" ), joinColumns = @JoinColumn( name = "lciamethod_id" ) ),
		@AssociationOverride( name = "name", joinTable = @JoinTable( name = "lciamethod_name" ), joinColumns = @JoinColumn( name = "lciamethod_id" ) ) } )
public class LCIAMethod extends DataSet implements ILCIAMethodVO {

	/**
	 * Serialization ID
	 */
	@Transient
	private static final long serialVersionUID = 1365429475652486423L;

	/**
	 * The time information
	 */
	@Embedded
	private TimeInformation timeInformation = new TimeInformation();

	/**
	 * The type of this LCIA method
	 */
	@Enumerated( EnumType.STRING )
	private TypeOfLCIAMethodValue type;

	/**
	 * The areas of protection (singular name to comply with bean conventions)
	 */
	@ElementCollection
	@CollectionTable( name = "lciamethod_areaofprotection", joinColumns = @JoinColumn( name = "lciamethod_id" ) )
	@Enumerated( EnumType.STRING )
	private List<AreaOfProtectionValue> areaOfProtection = new ArrayList<AreaOfProtectionValue>();

	/**
	 * The impact categories (singular name to comply with bean conventions)
	 */
	@ElementCollection
	@CollectionTable( name = "lciamethod_impactcategory", joinColumns = @JoinColumn( name = "lciamethod_id" ) )
	@Enumerated( EnumType.STRING )
	private List<LCIAImpactCategoryValue> impactCategory = new ArrayList<LCIAImpactCategoryValue>();

	/**
	 * The impact indicator
	 */
	@Basic
	@Column( length = 500 )
	private String impactIndicator;

	/**
	 * The methodologies (singular name to comply with bean conventions)
	 */
	@ElementCollection
	@CollectionTable( name = "lciamethod_methodology", joinColumns = @JoinColumn( name = "lciamethod_id" ) )
	private List<String> methodology = new ArrayList<String>();

	/**
	 * The characterisation factors
	 */
	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	private List<LCIAMethodCharacterisationFactor> charactarisationFactors = new ArrayList<LCIAMethodCharacterisationFactor>();

	/**
	 * The data stocks this LCIA Method is contained in
	 */
	@ManyToMany( mappedBy = "lciaMethods", fetch = FetchType.LAZY )
	protected Set<DataStock> containingDataStocks = new HashSet<DataStock>();

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
		stock.addLCIAMethod( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeSelfFromDataStock( DataStock stock ) {
		stock.removeLCIAMethod( this );
	}

	/**
	 * Get the time information
	 * 
	 * @return time information
	 */
	@Override
	public ITimeInformation getTimeInformation() {
		return this.timeInformation;
	}

	/**
	 * Set the new time information
	 * 
	 * @param timeInformation
	 *            the new time information
	 */
	public void setTimeInformation( TimeInformation timeInformation ) {
		this.timeInformation = timeInformation;
	}

	/**
	 * Get the type of this LCIA method
	 * 
	 * @return type of this LCIA method
	 */
	@Override
	public TypeOfLCIAMethodValue getType() {
		return this.type;
	}

	/**
	 * Set the type of the LCIA method
	 * 
	 * @param type
	 *            new type of the LCIA method
	 */
	public void setType( TypeOfLCIAMethodValue type ) {
		this.type = type;
	}

	/**
	 * Get the areas of protection
	 * 
	 * @return areas of protection
	 */
	@Override
	public List<AreaOfProtectionValue> getAreaOfProtection() {
		return this.areaOfProtection;
	}

	/**
	 * Set the areas of protection
	 * 
	 * @param areasOfProtection
	 *            areas of protection
	 */
	protected void setAreasOfProtection( List<AreaOfProtectionValue> areasOfProtection ) {
		this.areaOfProtection = areasOfProtection;
	}

	/**
	 * Add an area of protection
	 * 
	 * @param areaOfProtection
	 *            area of protection to add
	 */
	public void addAreaOfProtection( AreaOfProtectionValue areaOfProtection ) {
		if ( !this.areaOfProtection.contains( areaOfProtection ) ) {
			this.areaOfProtection.add( areaOfProtection );
		}
	}

	/**
	 * Get the impact categories
	 * 
	 * @return impact categories
	 */
	@Override
	public List<LCIAImpactCategoryValue> getImpactCategory() {
		return this.impactCategory;
	}

	/**
	 * Set the impact categories
	 * 
	 * @param impactCategory
	 *            new impact categories
	 */
	protected void setImpactCategory( List<LCIAImpactCategoryValue> impactCategory ) {
		this.impactCategory = impactCategory;
	}

	/**
	 * Add an impact category
	 * 
	 * @param impactCategory
	 *            impact category to add
	 */
	public void addImpactCategory( LCIAImpactCategoryValue impactCategory ) {
		if ( !this.impactCategory.contains( impactCategory ) ) {
			this.impactCategory.add( impactCategory );
		}
	}

	/**
	 * Get the impact indicator
	 * 
	 * @return impact indicator
	 */
	@Override
	public String getImpactIndicator() {
		return this.impactIndicator;
	}

	/**
	 * Set the new impact indicator
	 * 
	 * @param impactIndicator
	 *            the new impact indicator
	 */
	public void setImpactIndicator( String impactIndicator ) {
		if ( impactIndicator != null && impactIndicator.length() > 500 ) {
			throw new IllegalArgumentException( "Impact indicator field must not be longer than 500 characters!" );
		}
		this.impactIndicator = impactIndicator;
	}

	/**
	 * Get the methodologies
	 * 
	 * @return methodologies
	 */
	@Override
	public List<String> getMethodology() {
		return this.methodology;
	}

	/**
	 * Set the new methodologies
	 * 
	 * @param methodology
	 *            the new methodologies
	 */
	protected void setMethodology( List<String> methodology ) {
		this.methodology = methodology;
	}

	/**
	 * Add a methodology
	 * 
	 * @param methodology
	 *            methodology to add
	 */
	public void addMethodology( String methodology ) {
		if ( !this.methodology.contains( methodology ) ) {
			this.methodology.add( methodology );
		}
	}

	/**
	 * Get the characterisation factors
	 * 
	 * @return characterisation factors
	 */
	public List<LCIAMethodCharacterisationFactor> getCharactarisationFactors() {
		return this.charactarisationFactors;
	}

	/**
	 * Set the new characterisation factors
	 * 
	 * @param charactarisationFactors
	 *            the new characterisation factors
	 */
	protected void setCharactarisationFactors( List<LCIAMethodCharacterisationFactor> charactarisationFactors ) {
		this.charactarisationFactors = charactarisationFactors;
	}

	/**
	 * Add a characterisation factor
	 * 
	 * @param characterisationFactor
	 *            characterisation factor to add
	 */
	public void addCharacterisationFactor( LCIAMethodCharacterisationFactor characterisationFactor ) {
		if ( !this.charactarisationFactors.contains( characterisationFactor ) ) {
			this.charactarisationFactors.add( characterisationFactor );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return LCIAMethod.class.getName() + "[id=" + this.getId() + "]";
	}

}
