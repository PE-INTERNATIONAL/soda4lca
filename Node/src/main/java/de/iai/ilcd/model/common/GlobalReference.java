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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IGlobalReference;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.GlobalReferenceTypeValue;
import de.iai.ilcd.model.common.exception.FormatException;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "globalreference" )
public class GlobalReference implements Serializable, IGlobalReference {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "globalreference_shortdescription", joinColumns = @JoinColumn( name = "globalreference_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> shortDescription = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter shortDescriptionAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return GlobalReference.this.shortDescription;
		}
	} );

	// private MultiLanguageText shortDescription = new MultiLanguageText();

	@ElementCollection
	@CollectionTable( name = "globalreference_subreferences", joinColumns = @JoinColumn( name = "globalreference_id" ) )
	private List<String> subReferences = new LinkedList<String>();

	@Enumerated( EnumType.STRING )
	private GlobalReferenceTypeValue type;

	@Embedded
	private Uuid uuid;

	DataSetVersion version;

	String uri;

	// URL to objects will be generated automatically
	@Transient
	String href = null;

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	@Override
	public GlobalReferenceTypeValue getType() {
		return type;
	}

	@Override
	public void setType( GlobalReferenceTypeValue refType ) {
		this.type = refType;
	}

	@Override
	public IMultiLangString getShortDescription() {
		return this.shortDescriptionAdapter;
	}

	public void setShortDescription( IMultiLangString shortDescription ) {
		this.shortDescriptionAdapter.overrideValues( shortDescription );
	}

	public List<String> getSubReferences() {
		return subReferences;
	}

	protected void setSubReferences( List<String> subReferences ) {
		this.subReferences = subReferences;
	}

	public void addSubReference( String subReference ) {
		subReferences.add( subReference );
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public void setUri( String uri ) {
		this.uri = uri;
	}

	public Uuid getUuid() {
		if ( uuid != null )
			return uuid;
		Uuid uuidFromUri = getUuidFromUri();
		if ( uuidFromUri != null )
			return uuidFromUri;
		return null;
	}

	public void setUuid( Uuid uuid ) {
		this.uuid = uuid;
	}

	@Override
	public String getRefObjectId() {
		if ( this.uuid != null )
			return this.uuid.getUuid();
		Uuid uuidFromUri = getUuidFromUri();
		if ( uuidFromUri != null )
			return uuidFromUri.getUuid();
		return null;
	}

	private Uuid getUuidFromUri() {
		Uuid uuidFromUri = null;
		if ( uri == null )
			return null;
		try {
			GlobalRefUriAnalyzer analyzer = new GlobalRefUriAnalyzer( uri );
			return analyzer.getUuid();
		}
		catch ( Exception e ) {
			// we do nothing here
		}
		return null;
	}

	@Override
	public void setRefObjectId( String value ) {
		uuid = new Uuid( value );
	}

	public DataSetVersion getVersion() {
		return version;
	}

	public void setVersion( DataSetVersion version ) {
		this.version = version;
	}

	@Override
	public String getVersionAsString() {
		if ( version != null )
			return version.getVersionString();
		return null;
	}

	@Override
	public void setVersion( String versionString ) {
		DataSetVersion newVersion = null;
		try {
			newVersion = DataSetVersion.parse( versionString );
		}
		catch ( FormatException ex ) {
			// we do nothing here
		}
		if ( newVersion != null )
			this.version = newVersion;
	}

	@Override
	public String getHref() {
		// @TODO: include HREF generation code here later
		return href;
	}

	public void setHref( String href ) {
		this.href = href;
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
		if ( !(object instanceof GlobalReference) ) {
			return false;
		}
		GlobalReference other = (GlobalReference) object;
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.GlobalReference[id=" + id + "]";
	}

}
