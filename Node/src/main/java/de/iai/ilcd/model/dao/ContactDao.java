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

package de.iai.ilcd.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.velocity.tools.generic.ValueParser;

import de.fzk.iai.ilcd.service.model.IContactListVO;
import de.fzk.iai.ilcd.service.model.IContactVO;
import de.iai.ilcd.model.contact.Contact;

/**
 * Data access object for {@link Contact contacts}
 */
public class ContactDao extends DataSetDao<Contact, IContactListVO, IContactVO> {

	/**
	 * Create the data access object for {@link Contact contacts}
	 */
	public ContactDao() {
		super( "Contact", Contact.class, IContactListVO.class, IContactVO.class );
	}

	protected String getDataStockField() {
		return "contacts";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preCheckAndPersist( Contact dataSet ) {
		// Nothing to do for contacts :)
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryStringOrderJpql( String typeAlias, String sortString ) {
		if ( "classification.classHierarchyAsString".equals( sortString ) ) {
			return typeAlias + ".classificationCache";
		}
		else if ( "email".equals( sortString ) ) {
			return typeAlias + ".email";
		}
		else if ( "www".equals( sortString ) ) {
			return typeAlias + ".www";
		}
		else {
			return typeAlias + ".nameCache";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addWhereClausesAndNamedParamesForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues ) {
		// nothing to do beside the defaults
	}

	/*
	 * ================================================== == DEPRECATED METHODS / BACKWARDS COMPATIBILITY ==
	 * ==================================================
	 */

	/**
	 * Get contact by UUID
	 * 
	 * @return contact with provided UUID
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByUuid(String)}.
	 * @see #getByUuid(String)
	 */
	@Deprecated
	public Contact getContact( String uuid ) {
		return this.getByUuid( uuid );
	}

	/**
	 * Get the contact with the provided JPA id
	 * 
	 * @param datasetId
	 *            JPA id
	 * @return contact with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public Contact getContact( long datasetId ) {
		return this.getByDataSetId( datasetId );
	}

	/**
	 * Get the contact with the provided JPA id
	 * 
	 * @param id
	 *            JPA id as string
	 * @return contact with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(String)}.
	 * @see #getByDataSetId(String)
	 */
	@Deprecated
	public Contact getContactById( String id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get the contact with the provided JPA id
	 * 
	 * @param id
	 *            JPA id
	 * @return contact with the provided JPA id
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByDataSetId(long)}.
	 * @see #getByDataSetId(long)
	 */
	@Deprecated
	public Contact getContactById( Long id ) {
		return this.getByDataSetId( id );
	}

	/**
	 * Get list of all contacts
	 * 
	 * @return list of all contacts
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getAll()}.
	 * @see #getAll()
	 */
	@Deprecated
	public List<Contact> getContacts() {
		return this.getAll();
	}

	/**
	 * Get contacts by main class
	 * 
	 * @param mainClass
	 *            main class to get contact by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getByClass(String)
	 */
	@Deprecated
	public List<Contact> getContactsByClass( String mainClass ) {
		return this.getByClass( mainClass );
	}

	/**
	 * Get the number of contacts by main class
	 * 
	 * @param mainClass
	 *            main class to get contact by
	 * @return the number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String)}.
	 * @see #getNumberByClass(String)
	 */
	@Deprecated
	public long getNumberOfContacts( String mainClass ) {
		return this.getNumberByClass( mainClass );
	}

	/**
	 * Get contacts by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get contact by
	 * @param subClass
	 *            sub class to get contact by
	 * @return matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getByClass(String,String)
	 */
	@Deprecated
	public List<Contact> getContactsByClass( String mainClass, String subClass ) {
		return this.getByClass( mainClass, subClass );
	}

	/**
	 * Get number of contacts by main and sub class
	 * 
	 * @param mainClass
	 *            main class to get contact by
	 * @param subClass
	 *            sub class to get contact by
	 * @return number of matched data sets
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByClass(String,String)}.
	 * @see #getNumberByClass(String,String)
	 */
	@Deprecated
	public long getNumberOfContacts( String mainClass, String subClass ) {
		return this.getNumberByClass( mainClass, subClass );
	}

}
