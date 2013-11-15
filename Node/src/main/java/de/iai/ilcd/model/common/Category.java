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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "category" )
// @Table(uniqueConstraints={@UniqueConstraint(columnNames={"catName", "catLevel"})})
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	private String catName;

	private String catId;

	private int catLevel;

	@Embedded
	private Uuid uuid;

	@Enumerated( EnumType.STRING )
	private DataSetType dataSetType;

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getCatId() {
		return catId;
	}

	public void setCatId( String catId ) {
		this.catId = catId;
	}

	public int getCatLevel() {
		return catLevel;
	}

	public void setCatLevel( int catLevel ) {
		this.catLevel = catLevel;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName( String catName ) {
		this.catName = catName;
	}

	public DataSetType getDataSetType() {
		return dataSetType;
	}

	public void setDataSetType( DataSetType dataSetType ) {
		this.dataSetType = dataSetType;
	}

	public Uuid getUuid() {
		return uuid;
	}

	public void setUuid( Uuid uuid ) {
		this.uuid = uuid;
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
		if ( !(object instanceof Category) ) {
			return false;
		}
		Category other = (Category) object;
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.Category[id=" + id + "]";
	}

}
