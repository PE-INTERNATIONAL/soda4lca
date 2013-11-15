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
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.GlobalReferenceTypeValue;
import de.iai.ilcd.model.common.ClClass;
import de.iai.ilcd.model.common.Classification;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.common.GlobalRefUriAnalyzer;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.common.Uuid;
import de.iai.ilcd.model.common.exception.FormatException;

/**
 * 
 * @author clemens.duepmeier
 */
public class CommonConstructsReader {

	private DataSetParsingHelper parsingHelper = null;

	private Namespace commonNamespace = Namespace.getNamespace( "common", "http://lca.jrc.it/ILCD/Common" );

	public CommonConstructsReader( DataSetParsingHelper parsingHelper ) {
		this.parsingHelper = parsingHelper;
	}

	public Uuid getUuid() {
		String xpath = "//common:UUID";

		String uuidString = parsingHelper.getStringValue( xpath );

		return new Uuid( uuidString );
	}

	public List<Classification> getClassifications( DataSetType dataSetType ) {
		List<Element> classificationSections = parsingHelper.getElements( "//common:classification" );

		List<Classification> classifications = new LinkedList<Classification>();
		for ( Element classificationSection : classificationSections ) {
			String classificationName = classificationSection.getAttributeValue( "name" );
			if ( classificationName == null ) {
				classificationName = "ilcd";
			}
			Classification classification = new Classification( classificationName );
			// logger.debug("found classification with name " + classificationName);

			// OK, now we have to read in the classification classes

			List classTags = classificationSection.getChildren( "class", commonNamespace );
			for ( Object classTag : classTags ) {
				Element classElement = (Element) classTag;
				String level = classElement.getAttributeValue( "level" );
				String uuidStr = classElement.getAttributeValue( "uuid" );
				String id = classElement.getAttributeValue( "id" );
				String className = classElement.getText();
				ClClass clClass = new ClClass( className, Integer.parseInt( level ) );
				clClass.setDataSetType( dataSetType );
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
			classifications.add( classification );
		}

		return classifications;
	}

	public GlobalReference getGlobalReference( String xpath, PrintWriter out ) {

		Element referenceElement = parsingHelper.getElement( xpath );
		if ( referenceElement == null )
			return null;
		GlobalReference globalRef = createGlobalReference( referenceElement, out );

		return globalRef;
	}

	public GlobalReference getGlobalReference( Element element, String subElementName, Namespace nameSpace, PrintWriter out ) {
		Element referenceElement = element.getChild( subElementName, nameSpace );
		if ( referenceElement == null )
			return null;
		GlobalReference globalRef = createGlobalReference( referenceElement, out );

		return globalRef;
	}

	public GlobalReference getGlobalReference( Element referenceElement, PrintWriter out ) {
		return createGlobalReference( referenceElement, out );
	}

	public List<GlobalReference> getGlobalReferences( String xpath, PrintWriter out ) {
		List<GlobalReference> references = new ArrayList<GlobalReference>();

		List<Element> referenceElements = parsingHelper.getElements( xpath );

		for ( Element referenceElement : referenceElements ) {
			GlobalReference reference = createGlobalReference( referenceElement, out );
			references.add( reference );
		}
		return references;
	}

	public List<GlobalReference> getGlobalReferences( Element element, String subElementName, Namespace nameSpace, PrintWriter out ) {
		List<GlobalReference> references = new ArrayList<GlobalReference>();

		List<Element> referenceElements = element.getChildren( subElementName, nameSpace );

		for ( Element referenceElement : referenceElements ) {
			GlobalReference reference = createGlobalReference( referenceElement, out );
			references.add( reference );
		}

		return references;
	}

	private GlobalReference createGlobalReference( Element referenceElement, PrintWriter out ) {

		GlobalReference globalRef = new GlobalReference();

		String uri = referenceElement.getAttributeValue( "uri" );
		String type = referenceElement.getAttributeValue( "type" );
		String versionString = referenceElement.getAttributeValue( "version" );
		String refObjectId = referenceElement.getAttributeValue( "refObjectId" );
		IMultiLangString shortDescription = parsingHelper.getIMultiLanguageString( referenceElement, "shortDescription", commonNamespace );

		globalRef.setUri( uri );
		if ( out != null && uri == null )
			out.println( "Warning: global reference for " + referenceElement.getName() + " has no value" );

		GlobalReferenceTypeValue dataSetType = null;
		try {
			dataSetType = GlobalReferenceTypeValue.fromValue( type );
		}
		catch ( Exception e ) {
			if ( out != null )
				out.println( "Warning: global reference for " + referenceElement.getName() + " has no type attribute value" );
		}
		globalRef.setType( dataSetType );

		DataSetVersion version = null;
		try {
			if ( versionString != null )
				version = DataSetVersion.parse( versionString );
		}
		catch ( FormatException ex ) {
			if ( out != null )
				out.println( "Warning: global reference for " + referenceElement.getName() + " has version attribute value which isn't a version number" );
		}
		globalRef.setVersion( version );

		if ( refObjectId != null ) {
			globalRef.setUuid( new Uuid( refObjectId ) );
		}
		else {
			// refObjectId does not deliver valid UUID, let' try to extract it from uri
			if ( out != null ) {
				out.println( "Warning: global reference for " + referenceElement.getName() + "has no valid refObjectId set; will try to extract it from uri" );
			}
			if ( uri != null ) {
				GlobalRefUriAnalyzer uriAnalyzer = new GlobalRefUriAnalyzer( uri );
				if ( uriAnalyzer.getUuid() != null )
					globalRef.setUuid( uriAnalyzer.getUuid() );
			}
		}
		// if version is not set yet, set it to a 0 version
		if ( globalRef.getVersion() == null )
			globalRef.setVersion( new DataSetVersion() );

		globalRef.setShortDescription( shortDescription );

		if ( globalRef.getType().equals( GlobalReferenceTypeValue.SOURCE_DATA_SET ) ) {
			List<String> subRefs = parsingHelper.getStringValues( referenceElement, "subReference", this.commonNamespace );
			if ( subRefs != null )
				globalRef.getSubReferences().addAll( subRefs );
		}

		return globalRef;
	}
}
