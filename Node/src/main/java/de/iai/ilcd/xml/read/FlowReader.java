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
import de.fzk.iai.ilcd.service.model.enums.TypeOfFlowValue;
import de.iai.ilcd.model.common.ClClass;
import de.iai.ilcd.model.common.Classification;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.common.Uuid;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.flow.FlowPropertyDescription;

/**
 * 
 * @author clemens.duepmeier
 */
public class FlowReader extends DataSetReader {

	public static Logger logger = LoggerFactory.getLogger( de.iai.ilcd.xml.read.FlowReader.class );

	private Namespace flowNamespace = Namespace.getNamespace( "ilcd", "http://lca.jrc.it/ILCD/Flow" );

	private Namespace commonNamespace = Namespace.getNamespace( "common", "http://lca.jrc.it/ILCD/Common" );

	@Override
	public Flow parse( JXPathContext context, PrintWriter out ) {

		context.registerNamespace( "ilcd", "http://lca.jrc.it/ILCD/Flow" );

		Flow flow = new Flow();

		// OK, now read in all fields common to all DataSet types
		readCommonFields( flow, DataSetType.FLOW, context );

		// flows don't use standardized common:name tag
		IMultiLangString name = parserHelper.getIMultiLanguageString( "//ilcd:baseName" );
		// logger.error("Flow name is: " + name);
		flow.setName( name );

		IMultiLangString synonyms = parserHelper.getIMultiLanguageString( "//common:synonyms" );
		flow.setSynonyms( synonyms );

		String flowTypeString = parserHelper.getStringValue( "//ilcd:typeOfDataSet" );
		try {
			TypeOfFlowValue flowTypeValue = TypeOfFlowValue.fromValue( flowTypeString );
			flow.setType( flowTypeValue );
		}
		catch ( Exception e ) {
			if ( out != null ) {
				out.println( "flow doesn't have a valid flow type (" + flowTypeString + "); will ignore data set" );
			}
			return null;
		}

		Classification flowCategorization = getFlowCategorization();
		flow.setCategorization( flowCategorization );

		String casNumber = parserHelper.getStringValue( "//ilcd:CASNumber" );
		flow.setCasNumber( casNumber );
		String sumFormula = parserHelper.getStringValue( "//ilcd:sumFormula" );
		flow.setSumFormula( sumFormula );

		String referencePropertyString = parserHelper.getStringValue( "//ilcd:referenceToReferenceFlowProperty" );
		Integer referenceIndex = (referencePropertyString != null ? Integer.parseInt( referencePropertyString ) : null);

		List<FlowPropertyDescription> propertyDescriptions = readPropertyDescriptions( out );

		if ( propertyDescriptions != null ) {
			for ( FlowPropertyDescription propertyDescription : propertyDescriptions ) {
				flow.addPropertDesription( propertyDescription );
				if ( referenceIndex != null && propertyDescription.getInternalId() == referenceIndex )
					flow.setReferenceProperty( propertyDescription );
			}
		}
		return flow;
	}

	private Classification getFlowCategorization() {
		Element flowCategorization = parserHelper.getElement( "//common:elementaryFlowCategorization" );

		if ( flowCategorization == null )
			return null; // product, waste and other flows dont have elementary
						 // flow categorization

		String categorizationName = flowCategorization.getAttributeValue( "name" );
		if ( categorizationName == null ) {
			categorizationName = "ilcd";
		}
		Classification classification = new Classification( categorizationName );
		// logger.debug("found classification with name " + classificationName);

		// OK, now we have to read in the classification classes

		List categoryTags = flowCategorization.getChildren( "category", commonNamespace );
		for ( Object categoryTag : categoryTags ) {
			Element categoryElement = (Element) categoryTag;
			String level = categoryElement.getAttributeValue( "level" );
			String uuidStr = categoryElement.getAttributeValue( "uuid" );
			String id = categoryElement.getAttributeValue( "id" );
			String className = categoryElement.getText();
			ClClass clClass = new ClClass( className, Integer.parseInt( level ) );
			if ( uuidStr != null ) {
				Uuid uuid = new Uuid( uuidStr );
				clClass.setUuid( uuid );
			}
			if ( id != null ) {
				clClass.setClId( id );
			}
			// logger.debug("found class with name=%s, level=%s\n",className,level);
			classification.addClass( clClass );
		}

		return classification;
	}

	private List<FlowPropertyDescription> readPropertyDescriptions( PrintWriter out ) {
		List<FlowPropertyDescription> propertyDescriptions = new ArrayList<FlowPropertyDescription>();

		List<Element> propertyDescriptionElements = parserHelper.getElements( "//ilcd:flowProperty" );

		for ( Element elem : propertyDescriptionElements ) {
			String internalId = elem.getAttributeValue( "dataSetInternalID" );
			GlobalReference flowPropertyRef = commonReader.getGlobalReference( elem.getChild( "referenceToFlowPropertyDataSet", flowNamespace ), out );
			String meanValueString = null;
			Element meanValueElement = elem.getChild( "meanValue", flowNamespace );
			if ( meanValueElement != null )
				meanValueString = meanValueElement.getText();
			String minValueString = null;
			Element minValueElement = elem.getChild( "minValue", flowNamespace );
			if ( minValueElement != null )
				minValueString = minValueElement.getText();
			String maxValueString = null;
			Element maxValueElement = elem.getChild( "maximumValue", flowNamespace );
			if ( maxValueElement != null )
				maxValueString = maxValueElement.getText();
			String distributionTypeString = null;
			Element dTypeElement = elem.getChild( "uncertaintyDistributionType", flowNamespace );
			if ( dTypeElement != null )
				distributionTypeString = dTypeElement.getText();
			String standardDeviationString = null;
			Element stdElement = elem.getChild( "relativeStandardDeviation95In", flowNamespace );
			if ( stdElement != null )
				standardDeviationString = stdElement.getText();
			String derivationTypeString = null;
			Element derivElement = elem.getChild( "dataDerivationTypeStatus", flowNamespace );
			if ( derivElement != null )
				derivationTypeString = derivElement.getText();
			// logger.debug("name of propertyDescription: " + name);
			IMultiLangString description = parserHelper.getIMultiLanguageString( elem, "generalComment", flowNamespace );
			// logger.debug("propertyDescription beschreibung: " +
			// description.getDefaultValue());

			FlowPropertyDescription propertyDescription = new FlowPropertyDescription();
			propertyDescription.setInternalId( Integer.parseInt( internalId ) );
			// logger.debug("parse meanValue: " +meanValue);
			propertyDescription.setFlowPropertyRef( flowPropertyRef );
			if ( meanValueString != null )
				propertyDescription.setMeanValue( Double.parseDouble( meanValueString ) );
			else
				propertyDescription.setMeanValue( 0 );
			if ( maxValueString != null )
				propertyDescription.setMaxValue( Double.parseDouble( maxValueString ) );
			else
				propertyDescription.setMaxValue( 0 );
			if ( minValueString != null )
				propertyDescription.setMinValue( Double.parseDouble( minValueString ) );
			else
				propertyDescription.setMinValue( 0 );
			if ( distributionTypeString != null )
				propertyDescription.setUncertaintyType( distributionTypeString );
			if ( standardDeviationString != null )
				propertyDescription.setStandardDeviation( Float.parseFloat( standardDeviationString ) );
			else
				propertyDescription.setStandardDeviation( 0 );
			if ( derivationTypeString != null )
				propertyDescription.setDerivationType( derivationTypeString );
			propertyDescription.setDescription( description );

			propertyDescriptions.add( propertyDescription );
		}

		return propertyDescriptions;
	}
}
