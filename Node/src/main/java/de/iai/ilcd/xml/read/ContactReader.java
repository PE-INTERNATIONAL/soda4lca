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

package de.iai.ilcd.xml.read;

import java.io.PrintWriter;

import org.apache.commons.jxpath.JXPathContext;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.contact.Contact;

/**
 * 
 * @author clemens.duepmeier
 */
public class ContactReader extends DataSetReader {

	@Override
	public Contact parse( JXPathContext context, PrintWriter out ) {

		context.registerNamespace( "ilcd", "http://lca.jrc.it/ILCD/Contact" );

		Contact contact = new Contact();

		// OK, now read in all fields common to all DataSet types
		readCommonFields( contact, DataSetType.CONTACT, context );

		IMultiLangString shortName = parserHelper.getIMultiLanguageString( "//common:shortName" );
		String contactAddress = parserHelper.getStringValue( "//ilcd:contactAddress" );
		String phone = parserHelper.getStringValue( "//ilcd:telephone" );
		String fax = parserHelper.getStringValue( "//ilcd:telefax" );
		String email = parserHelper.getStringValue( "//ilcd:email" );
		String homePage = parserHelper.getStringValue( "//ilcd:WWWAddress" );
		String ccPoint = parserHelper.getStringValue( "//ilcd:centralContactPoint" );
		IMultiLangString contactDescription = parserHelper.getIMultiLanguageString( "//ilcd:contactDescriptionOrComment" );

		if ( out != null ) {
			if ( shortName == null || contact.getName() == null )
				out.println( "Warning: One of the fields 'name' or 'shortName' of the contact data set is empty" );
		}
		contact.setShortName( shortName );
		if ( contact.getName() == null )
			contact.setName( shortName );
		contact.setContactAddress( contactAddress );
		contact.setPhone( phone );
		contact.setFax( fax );
		contact.setEmail( email );
		contact.setWww( homePage );
		contact.setCentralContactPoint( ccPoint );
		contact.setDescription( contactDescription );

		return contact;
	}
}
