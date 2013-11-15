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
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.flowproperty.FlowProperty;

/**
 * 
 * @author clemens.duepmeier
 */
public class FlowPropertyReader extends DataSetReader {

	@Override
	public FlowProperty parse( JXPathContext context, PrintWriter out ) {

		context.registerNamespace( "ilcd", "http://lca.jrc.it/ILCD/FlowProperty" );

		FlowProperty flowProperty = new FlowProperty();

		// OK, now read in all fields common to all DataSet types
		readCommonFields( flowProperty, DataSetType.FLOWPROPERTY, context );

		IMultiLangString synonyms = parserHelper.getIMultiLanguageString( "//common:synonyms" );
		flowProperty.setSynonyms( synonyms );

		GlobalReference refToUnitGroup = commonReader.getGlobalReference( "//ilcd:referenceToReferenceUnitGroup", out );
		flowProperty.setReferenceToUnitGroup( refToUnitGroup );

		return flowProperty;
	}
}
