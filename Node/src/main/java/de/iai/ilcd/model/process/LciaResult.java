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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "lciaresult" )
public class LciaResult implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@ManyToOne( cascade = CascadeType.ALL )
	protected GlobalReference methodReference = null;

	protected float meanAmount = 0;

	protected String uncertaintyDistribution = "";

	protected float standardDeviation = -1;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "lciaresult_comment", joinColumns = @JoinColumn( name = "lciaresult_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> comment = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter commentAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return LciaResult.this.comment;
		}
	} );

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public IMultiLangString getComment() {
		return this.commentAdapter;
	}

	public void setComment( IMultiLangString comment ) {
		this.commentAdapter.overrideValues( comment );
	}

	public float getMeanAmount() {
		return meanAmount;
	}

	public void setMeanAmount( float meanAmount ) {
		this.meanAmount = meanAmount;
	}

	public GlobalReference getMethodReference() {
		return methodReference;
	}

	public void setMethodReference( GlobalReference methodReference ) {
		this.methodReference = methodReference;
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
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof LciaResult) ) {
			return false;
		}
		LciaResult other = (LciaResult) object;
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.process.LciaResult[id=" + id + "]";
	}

}
