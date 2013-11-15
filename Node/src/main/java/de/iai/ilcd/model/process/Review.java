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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IGlobalReference;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.TypeOfReviewValue;
import de.fzk.iai.ilcd.service.model.process.IDataQualityIndicator;
import de.fzk.iai.ilcd.service.model.process.IReview;
import de.fzk.iai.ilcd.service.model.process.IScope;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "review" )
public class Review implements Serializable, IReview {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Enumerated( EnumType.STRING )
	protected TypeOfReviewValue type;

	@OneToMany( cascade = CascadeType.ALL, targetEntity = ScopeOfReview.class )
	protected Set<IScope> scopes = new HashSet<IScope>();

	@OneToMany( cascade = CascadeType.ALL )
	protected Set<DataQualityIndicator> qualityIndicators = new HashSet<DataQualityIndicator>();

	@ManyToOne( cascade = CascadeType.ALL )
	protected GlobalReference referenceToReport = null;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "review_reviewdetails", joinColumns = @JoinColumn( name = "review_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> reviewDetails = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter reviewDetailsAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Review.this.reviewDetails;
		}
	} );

	@OneToMany( cascade = CascadeType.ALL )
	Set<GlobalReference> referencesToReviewers = new HashSet<GlobalReference>();

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "review_otherreviewdetails", joinColumns = @JoinColumn( name = "review_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> otherReviewDetails = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter otherReviewDetailsAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Review.this.otherReviewDetails;
		}
	} );

	public Review() {

	}

	public Review( TypeOfReviewValue type ) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public IMultiLangString getOtherReviewDetails() {
		return this.otherReviewDetailsAdapter;
	}

	public void setOtherReviewDetails( IMultiLangString otherReviewDetails ) {
		this.otherReviewDetailsAdapter.overrideValues( otherReviewDetails );
	}

	public Set<DataQualityIndicator> getQualityIndicators() {
		return qualityIndicators;
	}

	/**
	 * Convenience method for returning data quality indicators as List in order to user p:dataList (primefaces)
	 * TODO the first element should be, if present, "overall
	 * 
	 * @return List of data quality indicators
	 */
	public List<DataQualityIndicator> getDataQualityIndicatorsAsList() {
		List<DataQualityIndicator> list = new ArrayList<DataQualityIndicator>( this.getQualityIndicators() );
		Collections.sort( list, Collections.reverseOrder( new DataQualityIndicatorComparator() ) );
		return list;
	}

	@Override
	public Set<IDataQualityIndicator> getDataQualityIndicators() {
		// we use type erasure because we know the DataQualityIndicator always implements IDataQualityIndicator
		return (Set<IDataQualityIndicator>) (Set) qualityIndicators;
	}

	protected void setQualityIndicators( Set<DataQualityIndicator> qualityIndicators ) {
		this.qualityIndicators = qualityIndicators;
	}

	public void addQualityIndicator( DataQualityIndicator qualityIndicator ) {
		if ( !qualityIndicators.contains( qualityIndicator ) )
			qualityIndicators.add( qualityIndicator );
	}

	@Override
	public List<IGlobalReference> getReferencesToReviewers() {
		List<IGlobalReference> reviewers = new ArrayList<IGlobalReference>();
		for ( IGlobalReference reviewer : referencesToReviewers )
			reviewers.add( reviewer );
		return reviewers;
	}

	protected void setReferencesToReviewers( Set<GlobalReference> referencesToReviewers ) {
		this.referencesToReviewers = referencesToReviewers;
	}

	public void addReferenceToReviewers( GlobalReference referenceToReviewer ) {
		if ( !referencesToReviewers.contains( referenceToReviewer ) )
			referencesToReviewers.add( referenceToReviewer );
	}

	@Override
	public IMultiLangString getReviewDetails() {
		return this.reviewDetailsAdapter;
	}

	public void setReviewDetails( IMultiLangString reviewDetails ) {
		this.reviewDetailsAdapter.overrideValues( reviewDetails );
	}

	@Override
	public Set<IScope> getScopes() {
		return scopes;
	}

	protected void setScopes( Set<IScope> scopes ) {
		this.scopes = scopes;
	}

	public void addScope( ScopeOfReview scope ) {
		if ( !scopes.contains( scope ) )
			scopes.add( scope );
	}

	@Override
	public TypeOfReviewValue getType() {
		return type;
	}

	public void setType( TypeOfReviewValue type ) {
		this.type = type;
	}

	public GlobalReference getReferenceToReport() {
		return referenceToReport;
	}

	public void setReferenceToReport( GlobalReference referenceToReport ) {
		this.referenceToReport = referenceToReport;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((otherReviewDetails == null) ? 0 : otherReviewDetails.hashCode());
		result = prime * result + ((qualityIndicators == null) ? 0 : qualityIndicators.hashCode());
		result = prime * result + ((referencesToReviewers == null) ? 0 : referencesToReviewers.hashCode());
		result = prime * result + ((reviewDetails == null) ? 0 : reviewDetails.hashCode());
		result = prime * result + ((referenceToReport == null) ? 0 : referenceToReport.hashCode());
		result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		Review other = (Review) obj;
		if ( otherReviewDetails == null ) {
			if ( other.otherReviewDetails != null ) {
				return false;
			}
		}
		else if ( !otherReviewDetails.equals( other.otherReviewDetails ) ) {
			return false;
		}
		if ( qualityIndicators == null ) {
			if ( other.qualityIndicators != null ) {
				return false;
			}
		}
		else if ( !qualityIndicators.equals( other.qualityIndicators ) ) {
			return false;
		}
		if ( referencesToReviewers == null ) {
			if ( other.referencesToReviewers != null ) {
				return false;
			}
		}
		else if ( !referencesToReviewers.equals( other.referencesToReviewers ) ) {
			return false;
		}
		if ( referenceToReport == null ) {
			if ( other.referenceToReport != null ) {
				return false;
			}
		}
		else if ( !referenceToReport.equals( other.referenceToReport ) ) {
			return false;
		}
		if ( reviewDetails == null ) {
			if ( other.reviewDetails != null ) {
				return false;
			}
		}
		else if ( !reviewDetails.equals( other.reviewDetails ) ) {
			return false;
		}
		if ( scopes == null ) {
			if ( other.scopes != null ) {
				return false;
			}
		}
		else if ( !scopes.equals( other.scopes ) ) {
			return false;
		}
		if ( type != other.type ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.process.Review[id=" + id + "]";
	}

}
