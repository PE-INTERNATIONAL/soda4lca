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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.fzk.iai.ilcd.service.model.common.IClass;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "clclass" )
// @Table(uniqueConstraints={@UniqueConstraint(columnNames={"catName", "catLevel"})})
public class ClClass implements Serializable, IClass {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "clName" )
	private String name;

	private String clId;

	@Column( name = "clLevel" )
	private int level;

	@Embedded
	private Uuid uuid;

	@ManyToOne( )
	private Category category;

	@Enumerated( EnumType.STRING )
	private DataSetType dataSetType;

	protected ClClass() {

	}

	public ClClass( String name, int level ) {
		this.name = name;
		this.level = level;
	}

	public ClClass( String name, int level, String id ) {
		this.name = name;
		this.level = level;
		this.clId = id;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getClId() {
		return clId;
	}

	public void setClId( String clId ) {
		this.clId = clId;
	}

	@Override
	public Integer getLevel() {
		return level;
	}

	public void setLevel( int level ) {
		this.level = level;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Uuid getUuid() {
		return uuid;
	}

	public void setUuid( Uuid uuid ) {
		this.uuid = uuid;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory( Category category ) {
		this.category = category;
	}

	public DataSetType getDataSetType() {
		return dataSetType;
	}

	public void setDataSetType( DataSetType dataSetType ) {
		this.dataSetType = dataSetType;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.name.hashCode());
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof ClClass) ) {
			return false;
		}
		ClClass other = (ClClass) object;

		if ( this.level == other.level && this.name.equals( other.getName() ) )
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.Category[id=" + id + "]";
	}

}
