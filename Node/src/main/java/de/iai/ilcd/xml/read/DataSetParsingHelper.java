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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
public class DataSetParsingHelper {

	private JXPathContext context = null;

	private Namespace xmlNamespace = Namespace.getNamespace( "xml", "http://www.w3.org/XML/1998/namespace" );

	public static Logger logger = LoggerFactory.getLogger( de.iai.ilcd.xml.read.DataSetParsingHelper.class );

	public DataSetParsingHelper( JXPathContext context ) {
		this.context = context;
	}

	public IMultiLangString getIMultiLanguageString( String xpath ) {

		Map<String, String> languageStrings = getChildLanguageStrings( xpath );

		return new MultiLangStringMapAdapter( languageStrings );
	}

	public IMultiLangString getIMultiLanguageString( Element element, String xpath ) {
		Map<String, String> languageStrings = getChildLanguageStrings( element, xpath, null );

		return new MultiLangStringMapAdapter( languageStrings );
	}

	public IMultiLangString getIMultiLanguageString( Element element, String xpath, Namespace ns ) {
		Map<String, String> languageStrings = getChildLanguageStrings( element, xpath, ns );

		if ( languageStrings == null )
			return null;
		return new MultiLangStringMapAdapter( languageStrings );
	}

	// public MultiLanguageString getMultiLanguageString( String xpath ) {
	//
	// Map<String, String> languageStrings = getChildLanguageStrings( xpath );
	//
	// return createLanguageStringFromMap( languageStrings );
	// }
	//
	// public MultiLanguageString getMultiLanguageString( Element element, String xpath ) {
	// Map<String, String> languageStrings = getChildLanguageStrings( element, xpath, null );
	//
	// return createLanguageStringFromMap( languageStrings );
	// }
	//
	// public MultiLanguageString getMultiLanguageString( Element element, String xpath, Namespace ns ) {
	// Map<String, String> languageStrings = getChildLanguageStrings( element, xpath, ns );
	//
	// if ( languageStrings == null )
	// return null;
	// return createLanguageStringFromMap( languageStrings );
	// }

	// public MultiLanguageText getMultiLanguageText( String xpath ) {
	//
	// Map<String, String> languageStrings = getChildLanguageStrings( xpath );
	//
	// return createLanguageTextFromMap( languageStrings );
	// }
	//
	// public MultiLanguageText getMultiLanguageText( Element element, String xpath ) {
	// Map<String, String> languageStrings = getChildLanguageStrings( element, xpath, null );
	//
	// return createLanguageTextFromMap( languageStrings );
	// }
	//
	// public MultiLanguageText getMultiLanguageText( Element element, String xpath, Namespace ns ) {
	// Map<String, String> languageStrings = getChildLanguageStrings( element, xpath, ns );
	//
	// return createLanguageTextFromMap( languageStrings );
	// }

	public String getStringValue( String xpath ) {
		String returnValue = (String) context.getValue( xpath );
		// logger.trace(xpath + ": " + returnValue);
		return returnValue;
	}

	public int getIntValue( String xpath ) {
		int returnValue = 0;
		String value = (String) context.getValue( xpath );
		logger.trace( value );
		returnValue = Integer.parseInt( value );
		// logger.trace(returnValue);
		return returnValue;
	}

	public List<String> getStringValues( String xpath ) {
		List<String> returnList = new ArrayList<String>();

		List nodes = context.selectNodes( xpath );
		Iterator i = nodes.iterator();
		while ( i.hasNext() ) {
			Element elem = (Element) i.next();
			// logger.trace("Element: " + elem.getName());
			String value = (String) elem.getText();
			// logger.trace("Digital file value: " + value);
			if ( value != null )
				returnList.add( value );
		}

		return returnList;
	}

	public List<String> getStringValues( String xpath, String attribute ) {
		List<String> returnList = new ArrayList<String>();

		List nodes = context.selectNodes( xpath );
		Iterator i = nodes.iterator();
		while ( i.hasNext() ) {
			Element elem = (Element) i.next();
			// logger.trace("Element: " + elem.getName());
			String value = (String) elem.getAttributeValue( "uri" );
			// logger.trace("Digital file value: " + value);
			if ( value != null )
				returnList.add( value );
		}

		return returnList;
	}

	public List<String> getStringValues( Element element, String xpath, Namespace ns ) {

		List<Element> referenceElements = element.getChildren( xpath, ns );
		if ( referenceElements == null ) {
			return null;
		}

		List<String> result = new ArrayList<String>();

		for ( Element refElement : referenceElements ) {
			String text = (String) refElement.getText();
			result.add( text );
		}

		return result;
	}

	public String[][] getStringValues( Element element, String xpath, String attribute, String attribute2, Namespace ns ) {

		List<Element> referenceElements = element.getChildren( xpath, ns );
		if ( referenceElements == null ) {
			return null;
		}

		String[][] returnArray = new String[referenceElements.size()][2];

		int i=0;
		
		for ( Element refElement : referenceElements ) {
			String name = (String) refElement.getAttributeValue( attribute );
			String value = (String) refElement.getAttributeValue( attribute2 );
			returnArray[i][0] = name;
			returnArray[i][1] = value;
			i++;
		}

		return returnArray;
	}

	public Element getElement( String xpath ) {
		return (Element) context.selectSingleNode( xpath );
	}

	public List<Element> getElements( String xpath ) {
		List nodes = context.selectNodes( xpath );
		List<Element> elements = new ArrayList<Element>();

		Iterator i = nodes.iterator();
		while ( i.hasNext() ) {
			Element elem = (Element) i.next();
			if ( elem != null ) {
				elements.add( elem );
			}
		}

		return nodes;
	}

	private Map<String, String> getChildLanguageStrings( Element element, String xpath, Namespace ns ) {
		Map<String, String> languageStrings = new HashMap<String, String>();

		if ( element == null )
			return null;
		Iterator i = null;
		if ( ns == null ) {
			if ( element.getChildren( xpath ) == null )
				return null;
			i = element.getChildren( xpath ).iterator();
		}
		else {
			if ( element.getChildren( xpath, ns ) == null )
				return null;
			i = element.getChildren( xpath, ns ).iterator();
		}
		while ( i.hasNext() ) {
			Element childElement = (Element) i.next();
			String lang = "en"; // set default language
			if ( childElement.getAttributeValue( "lang", xmlNamespace ) != null )
				lang = childElement.getAttributeValue( "lang", xmlNamespace ); // OK,
																			   // set
																			   // for
																			   // this
																			   // language
			String stringValue = childElement.getText();
			languageStrings.put( lang, stringValue );
		}

		return languageStrings;
	}

	private Map<String, String> getChildLanguageStrings( String xpath ) {
		Map<String, String> languageStrings = new HashMap<String, String>();

		List nodeList = context.selectNodes( xpath );
		for ( Object object : nodeList ) {
			Element element = (Element) object;
			String lang = "en"; // set default value
			if ( element.getAttributeValue( "lang", xmlNamespace ) != null )
				lang = element.getAttributeValue( "lang", xmlNamespace ); // OK,
																		  // language
																		  // explicitly
																		  // stated
			String stringValue = element.getText();
			languageStrings.put( lang, stringValue );
		}

		return languageStrings;
	}

	// private MultiLanguageString createLanguageStringFromMap( Map<String, String> languageStrings ) {
	//
	// MultiLanguageString langString = new MultiLanguageString();
	//
	// if ( languageStrings == null )
	// return null;
	// for ( String key : languageStrings.keySet() ) {
	// if ( key.equals( "en" ) )
	// langString.setDefaultValue( languageStrings.get( key ) );
	// else
	// langString.addLString( key, languageStrings.get( key ) );
	// }
	// return langString;
	// }
	//
	// private MultiLanguageText createLanguageTextFromMap( Map<String, String> languageStrings ) {
	//
	// MultiLanguageText langText = new MultiLanguageText();
	//
	// for ( String key : languageStrings.keySet() ) {
	// if ( key.equals( "en" ) )
	// langText.setDefaultValue( languageStrings.get( key ) );
	// else
	// langText.addLString( key, languageStrings.get( key ) );
	// }
	// return langText;
	// }
}
