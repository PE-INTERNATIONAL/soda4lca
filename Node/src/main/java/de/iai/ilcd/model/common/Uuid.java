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
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author clemens.duepmeier
 */
@Embeddable
public class Uuid implements Serializable {

	@Basic
	@Column( name = "UUID" )
	private String uuid;

	public Uuid() {
		this.uuid = UUID.randomUUID().toString();
	}

	public Uuid( String uuid ) {
		// check UUID
		UUID.fromString( uuid );
		this.uuid = uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid( String uuid ) {
		// check uuid
		UUID.fromString( uuid );
		this.uuid = uuid;
	}

	public UUID getUUID() {
		return UUID.fromString( this.uuid );
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj == null ) {
			return false;
		}
		if ( this.getClass() != obj.getClass() ) {
			return false;
		}
		final Uuid other = (Uuid) obj;
		if ( (this.uuid == null) ? (other.uuid != null) : !this.uuid.equals( other.uuid ) ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
		return hash;
	}

}
