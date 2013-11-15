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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.unitgroup.Unit;
import de.iai.ilcd.model.unitgroup.UnitGroup;

/**
 * 
 * @author clemens.duepmeier
 */
public class UnitGroupReader extends DataSetReader {

	public static Logger logger = LoggerFactory.getLogger( de.iai.ilcd.xml.read.UnitGroupReader.class );

	Namespace unitGroupNamespace = Namespace.getNamespace( "ilcd", "http://lca.jrc.it/ILCD/UnitGroup" );

	@Override
	public UnitGroup parse( JXPathContext context, PrintWriter out ) {

		context.registerNamespace( "ilcd", "http://lca.jrc.it/ILCD/UnitGroup" );

		UnitGroup ugroup = new UnitGroup();

		// OK, now read in all fields common to all DataSet types
		readCommonFields( ugroup, DataSetType.UNITGROUP, context );

		// get all the units and set the reference unit
		String referenceUnitString = parserHelper.getStringValue( "//ilcd:referenceToReferenceUnit" );
		Integer referenceIndex = (referenceUnitString != null ? Integer.parseInt( referenceUnitString ) : null);

		List<Unit> units = readUnits( context );
		for ( Unit unit : units ) {
			ugroup.addUnit( unit );
			if ( referenceIndex != null && referenceIndex == unit.getInternalId() )
				ugroup.setReferenceUnit( unit );
		}

		return ugroup;
	}

	private List<Unit> readUnits( JXPathContext context ) {
		List<Unit> units = new ArrayList<Unit>();

		List<Element> unitElements = parserHelper.getElements( "//ilcd:unit" );

		for ( Element elem : unitElements ) {
			String internalId = elem.getAttributeValue( "dataSetInternalID" );
			String meanValue = elem.getChild( "meanValue", unitGroupNamespace ).getText();
			String name = elem.getChild( "name", unitGroupNamespace ).getText();
			// logger.debug("name of unit: " + name);
			IMultiLangString description = parserHelper.getIMultiLanguageString( elem, "generalComment", unitGroupNamespace );
			// logger.debug("unit beschreibung: " +
			// description.getDefaultValue());

			Unit unit = new Unit();
			unit.setInternalId( Integer.parseInt( internalId ) );
			// logger.debug("parse meanValue: " +meanValue);
			unit.setMeanValue( Double.parseDouble( meanValue ) );
			unit.setName( name );
			unit.setDescription( description );

			units.add( unit );
		}

		return units;
	}
}
