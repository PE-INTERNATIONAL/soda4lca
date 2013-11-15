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

package de.iai.ilcd.model.common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "categorysystem" )
public class CategorySystem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "csname" )
	private String name;

	@OneToMany( cascade = CascadeType.ALL )
	private Set<Category> categories = new HashSet<Category>();

	protected CategorySystem() {
	}

	public CategorySystem( String name ) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	protected void setCategories( Set<Category> categories ) {
		this.categories = categories;
	}

	public void addCategory( Category cat ) {
		if ( !categories.contains( cat ) )
			categories.add( cat );
	}

	public void removeCategory( Category cat ) {
		if ( categories.contains( cat ) )
			categories.remove( cat );
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (name != null ? name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof CategorySystem) ) {
			return false;
		}
		CategorySystem other = (CategorySystem) object;
		if ( (this.name == null && other.name != null) || (this.name != null && !this.name.equals( other.name )) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.ClassificationSystem[name=" + name + "]";
	}

}
