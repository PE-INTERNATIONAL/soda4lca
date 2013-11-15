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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import de.fzk.iai.ilcd.service.model.common.IClass;
import de.fzk.iai.ilcd.service.model.common.IClassification;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "classification" )
public class Classification implements Serializable, IClassification {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "clname" )
	private String name;

	@ManyToOne
	@JoinColumn( name = "catSystem" )
	private CategorySystem categorySystem;

	@OneToMany( cascade = CascadeType.ALL )
	@OrderBy( "level" )
	private List<ClClass> classes = new ArrayList<ClClass>();

	public Classification() {

	}

	public Classification( String name ) {
		this.name = name;
	}

	public Classification( IClassification second ) {
		name = second.getName();
		for ( IClass iclass : second.getIClassList() ) {
			ClClass clClass = new ClClass( iclass.getName(), iclass.getLevel() );
			classes.add( clClass );
		}
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public CategorySystem getCategorySystem() {
		return categorySystem;
	}

	public void setCategorySystem( CategorySystem categorySystem ) {
		this.categorySystem = categorySystem;
	}

	public List<ClClass> getClasses() {
		return classes;
	}

	protected void setClasses( List<ClClass> classes ) {
		this.classes = classes;
	}

	@Override
	public List<IClass> getIClassList() {
		List<IClass> iClasses = new ArrayList<IClass>();
		for ( IClass iClass : classes ) {
			iClasses.add( iClass );
		}
		return iClasses;
	}

	public void addClass( ClClass clClass ) {
		if ( !classes.contains( clClass ) )
			classes.add( clClass );
	}

	public void removeClass( ClClass clClass ) {
		if ( classes.contains( clClass ) )
			classes.remove( clClass );
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public String getClassHierarchyAsString() {
		StringBuilder buffer = new StringBuilder();

		int numberOfClasses = classes.size();
		int i = 0;
		for ( ClClass clClass : classes ) {
			buffer.append( clClass.getName() );
			if ( i++ < numberOfClasses - 1 )
				buffer.append( " / " );
		}

		return buffer.toString();
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
		if ( !(object instanceof Classification) ) {
			return false;
		}
		Classification other = (Classification) object;
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.Classification[id=" + id + "]";
	}

}
