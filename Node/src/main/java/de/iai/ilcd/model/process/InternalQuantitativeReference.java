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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.TypeOfQuantitativeReferenceValue;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "quantitativereference" )
public class InternalQuantitativeReference implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Enumerated( EnumType.STRING )
	protected TypeOfQuantitativeReferenceValue type;

	@ElementCollection
	@CollectionTable( name = "process_quantref_referenceids", joinColumns = @JoinColumn( name = "internalquantitativereference_id" ) )
	@Column( name = "refId" )
	Set<Integer> referenceIds = new HashSet<Integer>();

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "process_quantref_otherreference", joinColumns = @JoinColumn( name = "internalquantitativereference_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> otherReference = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter otherReferenceAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return InternalQuantitativeReference.this.otherReference;
		}
	} );

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public IMultiLangString getOtherReference() {
		return this.otherReferenceAdapter;
	}

	public void setOtherReference( IMultiLangString otherReference ) {
		this.otherReferenceAdapter.overrideValues( otherReference );
	}

	public Set<Integer> getReferenceIds() {
		return referenceIds;
	}

	protected void setReferenceIds( Set<Integer> referenceIds ) {
		this.referenceIds = referenceIds;
	}

	public void addReferenceId( Integer referenceId ) {
		// if (! referenceIds.contains(referenceId))
		referenceIds.add( referenceId );
	}

	public TypeOfQuantitativeReferenceValue getType() {
		return type;
	}

	public void setType( TypeOfQuantitativeReferenceValue type ) {
		this.type = type;
	}

}
