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

package de.iai.ilcd.model.contact;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import de.fzk.iai.ilcd.service.model.IContactVO;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "contact", uniqueConstraints = @UniqueConstraint( columnNames = { "UUID", "MAJORVERSION", "MINORVERSION", "SUBMINORVERSION" } ) )
@AssociationOverrides( {
		@AssociationOverride( name = "description", joinTable = @JoinTable( name = "contact_description" ), joinColumns = @JoinColumn( name = "contact_id" ) ),
		@AssociationOverride( name = "name", joinTable = @JoinTable( name = "contact_name" ), joinColumns = @JoinColumn( name = "contact_id" ) ) } )
public class Contact extends DataSet implements Serializable, IContactVO {

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "contact_shortname", joinColumns = @JoinColumn( name = "contact_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> shortName = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter shortNameAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return Contact.this.shortName;
		}
	} );

	protected String contactAddress = null;

	protected String phone = null;

	protected String fax = null;

	protected String email = null;

	protected String www = null;

	protected String centralContactPoint = null;

	/**
	 * The data stocks this contact is contained in
	 */
	@ManyToMany( mappedBy = "contacts", fetch = FetchType.LAZY )
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
		stock.addContact( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeSelfFromDataStock( DataStock stock ) {
		stock.removeContact( this );
	}

	@Override
	public String getCentralContactPoint() {
		return this.centralContactPoint;
	}

	public void setCentralContactPoint( String centralContactPoint ) {
		if ( centralContactPoint != null && centralContactPoint.length() > 256 ) {
			centralContactPoint = centralContactPoint.substring( 0, 255 );
		}
		this.centralContactPoint = centralContactPoint;
	}

	public String getContactAddress() {
		return this.contactAddress;
	}

	public void setContactAddress( String contactAddress ) {
		this.contactAddress = contactAddress;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	public void setEmail( String email ) {
		this.email = email;
	}

	@Override
	public String getFax() {
		return this.fax;
	}

	public void setFax( String fax ) {
		this.fax = fax;
	}

	@Override
	public String getWww() {
		return this.www;
	}

	public void setWww( String homePage ) {
		this.www = homePage;
	}

	@Override
	public String getPhone() {
		return this.phone;
	}

	public void setPhone( String phone ) {
		this.phone = phone;
	}

	@Override
	public IMultiLangString getShortName() {
		return this.shortNameAdapter;
	}

	public void setShortName( IMultiLangString shortName ) {
		this.shortNameAdapter.overrideValues( shortName );
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.contact.Contact[id=" + this.id + "]";
	}

	@Override
	protected String getNameAsStringForCache() {
		if ( this.shortName != null ) {
			final String snDef = this.shortNameAdapter.getDefaultValue();
			if ( snDef != null && !snDef.trim().isEmpty() ) {
				return snDef;
			}
		}

		if ( this.name != null ) {
			return this.nameAdapter.getDefaultValue();
		}

		return null;
	}

}
