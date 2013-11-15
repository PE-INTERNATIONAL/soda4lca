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

package de.iai.ilcd.model.source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import de.fzk.iai.ilcd.service.model.ISourceVO;
import de.fzk.iai.ilcd.service.model.common.IGlobalReference;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.GlobalReferenceTypeValue;
import de.fzk.iai.ilcd.service.model.enums.PublicationTypeValue;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DigitalFile;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "source", uniqueConstraints = @UniqueConstraint( columnNames = { "UUID", "MAJORVERSION", "MINORVERSION", "SUBMINORVERSION" } ) )
@AssociationOverrides( {
		@AssociationOverride( name = "description", joinTable = @JoinTable( name = "source_description" ), joinColumns = @JoinColumn( name = "source_id" ) ),
		@AssociationOverride( name = "name", joinTable = @JoinTable( name = "source_name" ), joinColumns = @JoinColumn( name = "source_id" ) ) } )
public class Source extends DataSet implements Serializable, ISourceVO {

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "source_shortName", joinColumns = @JoinColumn( name = "source_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> shortName = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter shortNameAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Source.this.shortName;
		}
	} );

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "source_citation", joinColumns = @JoinColumn( name = "source_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> citation = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter citationAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Source.this.citation;
		}
	} );

	@Enumerated( EnumType.STRING )
	protected PublicationTypeValue publicationType;

	@OneToMany( cascade = CascadeType.ALL, mappedBy = "source" )
	Set<DigitalFile> files = new HashSet<DigitalFile>();

	@OneToMany( cascade = CascadeType.ALL )
	Set<GlobalReference> contacts = new HashSet<GlobalReference>();

	/**
	 * The data stocks this source is contained in
	 */
	@ManyToMany( mappedBy = "sources", fetch = FetchType.LAZY )
	protected Set<DataStock> containingDataStocks = new HashSet<DataStock>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<DataStock> getContainingDataStocks() {
		return this.containingDataStocks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addSelfToDataStock( DataStock stock ) {
		stock.addSource( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeSelfFromDataStock( DataStock stock ) {
		stock.removeSource( this );
	}

	@Override
	public IMultiLangString getCitation() {
		return this.citationAdapter;
	}

	public void setCitation( IMultiLangString citation ) {
		this.citationAdapter.overrideValues( citation );
	}

	@Override
	public PublicationTypeValue getPublicationType() {
		return this.publicationType;
	}

	public void setPublicationType( PublicationTypeValue publicationType ) {
		this.publicationType = publicationType;
	}

	public IMultiLangString getShortName() {
		return this.shortNameAdapter;
	}

	public void setShortName( IMultiLangString shortName ) {
		this.shortNameAdapter.overrideValues( shortName );
	}

	public Set<GlobalReference> getContacts() {
		return this.contacts;
	}

	/**
	 * Convenience method for returning global references as List in order to user p:dataList (primefaces)
	 * 
	 * @return List of global references
	 */
	public List<GlobalReference> getContactsAsList() {
		return new ArrayList<GlobalReference>( this.getContacts() );
	}

	@Override
	public List<IGlobalReference> getBelongsTo() {
		List<IGlobalReference> belongs = new ArrayList<IGlobalReference>();
		if ( this.contacts == null ) {
			this.contacts = new HashSet<GlobalReference>();
		}
		for ( GlobalReference ref : this.contacts ) {
			belongs.add( ref );
		}

		return belongs;
	}

	protected void setContacts( Set<GlobalReference> contacts ) {
		this.contacts = contacts;
	}

	public void addContact( GlobalReference contact ) {
		if ( !this.contacts.contains( contact ) ) {
			this.contacts.add( contact );
		}
	}

	public Set<DigitalFile> getFiles() {
		return this.files;
	}

	/**
	 * Convenience method for returning files as List in order to user p:dataList (primefaces)
	 * 
	 * @return List of files
	 */
	public List<DigitalFile> getFilesAsList() {
		return new ArrayList<DigitalFile>( this.getFiles() );
	}

	@Override
	public List<IGlobalReference> getFileReferences() {
		List<IGlobalReference> fileRefs = new ArrayList<IGlobalReference>();

		for ( DigitalFile file : this.files ) {
			GlobalReference fileRef = new GlobalReference();
			fileRef.setType( GlobalReferenceTypeValue.OTHER_EXTERNAL_FILE );
			fileRef.setShortDescription( new MultiLangStringMapAdapter( file.getFileName() ) );
			fileRef.setRefObjectId( this.getUuidAsString() );
			// @TODO : provide correct HREF attribute, maybe other too

			// add to list
			fileRefs.add( fileRef );
		}

		return fileRefs;
	}

	protected void setFiles( Set<DigitalFile> files ) {
		this.files = files;
	}

	public void addFile( DigitalFile file ) {
		if ( !this.files.contains( file ) ) {
			this.files.add( file );
			file.setSource( this );
		}
	}

	public String getFilesDirectory() {
		String basePath = ConfigurationService.INSTANCE.getDigitalFileBasePath();
		return basePath + "/" + this.id.toString();
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.source.Source[id=" + this.id + "]";
	}

}
