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

import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.ILString;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.model.common.MultiLanguageString;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 * @deprecated
 * 
 */
// @Entity
// @Table( name = "processname" )
public class ProcessName implements Serializable {

	// @Id
	// @GeneratedValue( strategy = GenerationType.IDENTITY )
	protected long id;

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	private int foo;

	public int getFoo() {
		return foo;
	}

	public void setFoo( int foo ) {
		this.foo = foo;
	}

	// @ElementCollection
	// @Column( name = "value", columnDefinition = "TEXT" )
	// @CollectionTable( name = "processname_base", joinColumns = @JoinColumn( name = "processname_id" ) )
	// @MapKeyColumn( name = "lang" )
	protected final Map<String, String> basePart = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter basePartAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return ProcessName.this.basePart;
		}
	} );

	// @ElementCollection
	// @Column( name = "value", columnDefinition = "TEXT" )
	// @CollectionTable( name = "processname_route", joinColumns = @JoinColumn( name = "processname_id" ) )
	// @MapKeyColumn( name = "lang" )
	protected final Map<String, String> routePart = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter routePartAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return ProcessName.this.routePart;
		}
	} );

	// @ElementCollection
	// @Column( name = "value", columnDefinition = "TEXT" )
	// @CollectionTable( name = "processname_location", joinColumns = @JoinColumn( name = "processname_id" ) )
	// @MapKeyColumn( name = "lang" )
	protected final Map<String, String> locationPart = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter locationPartAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return ProcessName.this.locationPart;
		}
	} );

	// @ElementCollection
	// @Column( name = "value", columnDefinition = "TEXT" )
	// @CollectionTable( name = "processname_unit", joinColumns = @JoinColumn( name = "processname_id" ) )
	// @MapKeyColumn( name = "lang" )
	protected final Map<String, String> unitPart = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter unitPartAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return ProcessName.this.unitPart;
		}
	} );

	public ProcessName() {
	}

	public ProcessName( IMultiLangString basePart, IMultiLangString routePart, IMultiLangString locationPart, IMultiLangString unitPart ) {
		if ( basePart != null ) {
			setBasePart( basePart );
		}
		if ( routePart != null ) {
			setRoutePart( routePart );
		}
		if ( locationPart != null ) {
			setLocationPart( locationPart );
		}
		if ( unitPart != null ) {
			setUnitPart( unitPart );
		}
	}

	public IMultiLangString getBasePart() {
		return this.basePartAdapter;
	}

	public void setBasePart( IMultiLangString basePart ) {
		this.basePartAdapter.overrideValues( basePart );
	}

	public IMultiLangString getLocationPart() {
		return this.locationPartAdapter;
	}

	public void setLocationPart( IMultiLangString locationPart ) {
		this.locationPartAdapter.overrideValues( locationPart );
	}

	public IMultiLangString getRoutePart() {
		return this.routePartAdapter;
	}

	public void setRoutePart( IMultiLangString routePart ) {
		this.routePartAdapter.overrideValues( routePart );
	}

	public IMultiLangString getUnitPart() {
		return this.unitPartAdapter;
	}

	public void setUnitPart( IMultiLangString unitPart ) {
		this.unitPartAdapter.overrideValues( unitPart );
	}

	public String getName() {
		StringBuffer name = new StringBuffer();

		if ( basePart != null && basePartAdapter.getDefaultValue() != null ) {
			name.append( basePartAdapter.getDefaultValue() );
		}
		if ( routePart != null && routePartAdapter.getDefaultValue() != null ) {
			name.append( ";" + routePartAdapter.getDefaultValue() );
		}
		if ( locationPart != null && locationPartAdapter.getDefaultValue() != null ) {
			name.append( ";" + locationPartAdapter.getDefaultValue() );
		}
		if ( unitPart != null && unitPartAdapter.getDefaultValue() != null ) {
			name.append( ";" + unitPartAdapter.getDefaultValue() );
		}
		return name.toString();

	}

	public IMultiLangString getFullName() {
		MultiLanguageString joinedName = new MultiLanguageString( this.getName() );
		for ( ILString lString : basePartAdapter.getLStrings() ) {
			String language = lString.getLang();
			if ( language == null )
				continue;
			String baseValue = lString.getValue();
			StringBuilder joinedValue = new StringBuilder();

			if ( baseValue != null ) {
				joinedValue.append( baseValue );
			}
			if ( routePart != null && routePartAdapter.getValue( language ) != null ) {
				joinedValue.append( ";" ).append( routePartAdapter.getValue( language ) );
			}
			if ( locationPart != null && locationPartAdapter.getValue( language ) != null ) {
				joinedValue.append( ";" ).append( locationPartAdapter.getValue( language ) );
			}
			if ( unitPart != null && unitPartAdapter.getValue( language ) != null ) {
				joinedValue.append( ";" ).append( unitPartAdapter.getValue( language ) );
			}

			joinedName.addLString( language, joinedValue.toString() );

		}

		return joinedName;
	}
}
