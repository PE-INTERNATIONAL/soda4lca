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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.lciamethod.ITimeInformation;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * Time information for {@link LCIAMethod LCIA methods}
 */
@Embeddable
public class TimeInformation implements ITimeInformation, Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -8055792015884241376L;

	/**
	 * Duration information
	 */
	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "lciamethod_ti_durationdescription", joinColumns = @JoinColumn( name = "lciamethod_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> duration = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter durationAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return TimeInformation.this.duration;
		}
	} );

	/**
	 * Reference year information
	 */
	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "lciamethod_ti_referenceyeardescription", joinColumns = @JoinColumn( name = "lciamethod_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> referenceYear = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter referenceYearAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return TimeInformation.this.referenceYear;
		}
	} );

	/**
	 * Get the duration information
	 * 
	 * @return duration information
	 */
	@Override
	public IMultiLangString getDuration() {
		return this.durationAdapter;
	}

	/**
	 * Get the reference year information
	 * 
	 * @return reference year information
	 */
	@Override
	public IMultiLangString getReferenceYear() {
		return this.referenceYearAdapter;
	}

	/**
	 * Set the duration information
	 * 
	 * @param duration
	 *            the new duration information
	 */
	public void setDuration( IMultiLangString duration ) {
		this.durationAdapter.overrideValues( duration );
	}

	/**
	 * Set the reference year information
	 * 
	 * @param referenceYear
	 *            the new reference year information
	 */
	public void setReferenceYear( IMultiLangString referenceYear ) {
		this.referenceYearAdapter.overrideValues( referenceYear );
	}

}
