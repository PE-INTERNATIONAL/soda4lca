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

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.process.ITimeInformation;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity( name = "process_timeinformation" )
@Table( name = "process_timeinformation" )
public class TimeInformation implements Serializable, ITimeInformation {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	protected long id;

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	protected Integer referenceYear = null;

	protected Integer validUntil = null;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "process_timedescription", joinColumns = @JoinColumn( name = "process_timeinformation_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> description = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter descriptionAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return TimeInformation.this.description;
		}
	} );

	public IMultiLangString getDescription() {
		return this.descriptionAdapter;
	}

	public void setDescription( IMultiLangString description ) {
		this.descriptionAdapter.overrideValues( description );
	}

	@Override
	public Integer getReferenceYear() {
		return referenceYear;
	}

	public void setReferenceYear( Integer referenceYear ) {
		this.referenceYear = referenceYear;
	}

	@Override
	public Integer getValidUntil() {
		return validUntil;
	}

	public void setValidUntil( Integer validUntil ) {
		this.validUntil = validUntil;
	}
}
