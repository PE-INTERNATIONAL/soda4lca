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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "geographicalarea" )
public class GeographicalArea implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	private String areaCode;

	@Column( name = "areaName" )
	private String name;

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode( String areaCode ) {
		this.areaCode = areaCode;
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
		hash += (areaCode != null ? areaCode.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof GeographicalArea) ) {
			return false;
		}
		GeographicalArea other = (GeographicalArea) object;
		if ( (this.areaCode == null) || (other.getAreaCode() == null) || !this.areaCode.equals( other.getAreaCode() ) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.GeographicalArea[areaCode=" + areaCode + "]";
	}

}
