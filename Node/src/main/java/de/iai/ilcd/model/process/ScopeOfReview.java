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
import java.util.HashSet;
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
import javax.persistence.Table;

import de.fzk.iai.ilcd.service.model.enums.MethodOfReviewValue;
import de.fzk.iai.ilcd.service.model.enums.ScopeOfReviewValue;
import de.fzk.iai.ilcd.service.model.process.IScope;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "scopeofreview" )
public class ScopeOfReview implements Serializable, IScope {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Enumerated( EnumType.STRING )
	protected ScopeOfReviewValue name;

	@ElementCollection( )
	@CollectionTable( name = "review_methods", joinColumns = @JoinColumn( name = "scopeofreview_id" ) )
	@Column( name = "method" )
	@Enumerated( EnumType.STRING )
	Set<MethodOfReviewValue> methods = new HashSet<MethodOfReviewValue>();

	protected ScopeOfReview() {

	}

	public ScopeOfReview( ScopeOfReviewValue name ) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	@Override
	public Set<MethodOfReviewValue> getMethods() {
		return methods;
	}

	protected void setMethods( Set<MethodOfReviewValue> methods ) {
		this.methods = methods;
	}

	public void addMethod( MethodOfReviewValue method ) {
		if ( !methods.contains( method ) )
			methods.add( method );
	}

	@Override
	public ScopeOfReviewValue getName() {
		return name;
	}

	public void setName( ScopeOfReviewValue name ) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScopeOfReview other = (ScopeOfReview) obj;
		if (methods == null) {
			if (other.methods != null)
				return false;
		} else if (!methods.equals(other.methods))
			return false;
		if (name != other.name)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.process.ScopeOfReview[name=" + name + "]";
	}

}
