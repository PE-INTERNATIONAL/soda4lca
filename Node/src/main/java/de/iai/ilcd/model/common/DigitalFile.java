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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.iai.ilcd.model.source.Source;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "digitalfile" )
public class DigitalFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( length = 1000 )
	private String fileName;

	@ManyToOne( )
	private Source source;

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName( String fileName ) {
		this.fileName = fileName;
	}

	public Source getSource() {
		return source;
	}

	public void setSource( Source source ) {
		this.source = source;
		if ( !source.getFiles().contains( this ) )
			source.addFile( this );
	}

	public String getAbsoluteFileName() {

		String directory = source.getFilesDirectory();

		String path = directory + "/" + fileName;
		return path;
	}

	@Override
	public int hashCode() {
		return this.fileName.hashCode();
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof DigitalFile) ) {
			return false;
		}
		DigitalFile other = (DigitalFile) object;
		if ( (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		if ( this.fileName.equals( other.getFileName() ) )
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.DigitalFile[fileName=" + fileName + "]";
	}

}
