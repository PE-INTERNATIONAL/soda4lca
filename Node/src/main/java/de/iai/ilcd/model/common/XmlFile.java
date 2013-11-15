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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "xmlfile" )
public class XmlFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	/**
	 * Uncompressed version of XML content. Is not stored in database, will be set automatically after loading
	 * {@link #compressedContent} from database.
	 * 
	 * @see #decompressContent()
	 */
	@Transient
	protected String content;

	/**
	 * Compressed version of {@link #content}. Is being auto generated prior to persist/merge
	 * 
	 * @see #compressContent()
	 */
	@Basic
	private byte[] compressedContent;

	public Long getId() {
		return this.id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent( String content ) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof XmlFile) ) {
			return false;
		}
		XmlFile other = (XmlFile) object;
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		return true;
	}

	/**
	 * Compress the content of XML file prior to persist/merge events in order to save database space and be compatible
	 * with MySQL server default configurations
	 * as long as possible (1MB max package size)
	 * 
	 * @throws Exception
	 *             if anything goes wrong, just in-memory IO operations, should not happen
	 * @see #decompressContent()
	 */
	@PrePersist
	protected void compressContent() throws Exception {
		if ( this.content == null ) {
			this.compressedContent = null;
		}
		else {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gzipOut = new GZIPOutputStream( out );
			gzipOut.write( this.content.getBytes( "UTF-8" ) );
			gzipOut.flush();
			gzipOut.close();
			this.compressedContent = out.toByteArray();
		}
	}

	/**
	 * Decompress content of XML file after loading. The reason for doing this, see {@link #compressContent()}
	 * 
	 * @throws Exception
	 *             if anything goes wrong, just in-memory IO operations, should not happen
	 * @see #compressContent()
	 */
	@PostLoad
	protected void decompressContent() throws Exception {
		if ( this.compressedContent == null || this.compressedContent.length == 0 ) {
			this.content = null;
		}
		else {
			GZIPInputStream gzipIn = new GZIPInputStream( new ByteArrayInputStream( this.compressedContent ) );
			this.content = new String( IOUtils.toByteArray( gzipIn ), "UTF-8" );
		}
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.XmlFile[id=" + this.id + "]";
	}

	public byte[] getContentHash() {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance( "MD5" );
			return md.digest( this.compressedContent );
		}
		catch ( NoSuchAlgorithmException e ) {
			e.printStackTrace();
		}
		return new byte[0];
	}
}
