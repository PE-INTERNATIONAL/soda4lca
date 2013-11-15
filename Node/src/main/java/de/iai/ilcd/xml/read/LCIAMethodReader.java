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
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.jdom.Element;
import org.jdom.Namespace;

import de.fzk.iai.ilcd.api.binding.generated.common.DataDerivationTypeStatusValues;
import de.fzk.iai.ilcd.api.binding.generated.common.ExchangeDirectionValues;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.AreaOfProtectionValue;
import de.fzk.iai.ilcd.service.model.enums.LCIAImpactCategoryValue;
import de.fzk.iai.ilcd.service.model.enums.TypeOfLCIAMethodValue;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.lciamethod.LCIAMethod;
import de.iai.ilcd.model.lciamethod.LCIAMethodCharacterisationFactor;
import de.iai.ilcd.model.lciamethod.TimeInformation;

/**
 * XML reader for {@link LCIAMethod} xml data files
 */
public class LCIAMethodReader extends DataSetReader {

	/**
	 * Namespace for the {@link LCIAMethod}
	 */
	private final static Namespace NAMESPACE_LCIAMETHOD = Namespace.getNamespace( "ilcd", "http://lca.jrc.it/ILCD/LCIAMethod" );

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSet parse( JXPathContext context, PrintWriter out ) {

		context.registerNamespace( "ilcd", "http://lca.jrc.it/ILCD/LCIAMethod" );

		LCIAMethod method = new LCIAMethod();

		// OK, now read in all fields common to all DataSet types
		this.readCommonFields( method, DataSetType.LCIAMETHOD, context );

		// the impact indicator
		this.processImpactIndicator( method, out );

		// the type
		this.processType( method, out );

		// time information
		this.processTimeInformation( method, out );

		// impact categories
		this.processImpactCategories( method, out );

		// areas of protection
		this.processAreasOfProtection( method, out );

		// factors
		this.processCharacterisationFactors( method, out );

		// methodologies
		this.processMethodologies( method, out );

		// we're done :)
		return method;
	}

	/**
	 * Process the impact indicator and set it for the provided {@link LCIAMethod}
	 * 
	 * @param method
	 *            the method to set the parsed impact indicator for
	 * @param out
	 *            message writer
	 */
	private void processTimeInformation( LCIAMethod method, PrintWriter out ) {
		// get xml element
		Element timeElement = this.parserHelper.getElement( "/ilcd:LCIAMethodDataSet/ilcd:LCIAMethodInformation/ilcd:time" );

		// the time information value
		TimeInformation ti = new TimeInformation();

		// set the value
		if ( timeElement != null ) {

			IMultiLangString referenceYearMLStr = super.parserHelper.getIMultiLanguageString( timeElement, "referenceYear",
					LCIAMethodReader.NAMESPACE_LCIAMETHOD );
			if ( referenceYearMLStr != null ) {
				ti.setReferenceYear( referenceYearMLStr );
			}

			IMultiLangString durationMLStr = super.parserHelper.getIMultiLanguageString( timeElement, "duration", LCIAMethodReader.NAMESPACE_LCIAMETHOD );
			if ( durationMLStr != null ) {
				ti.setDuration( durationMLStr );
			}

		}

		method.setTimeInformation( ti );
	}

	/**
	 * Process the impact indicator and set it for the provided {@link LCIAMethod}
	 * 
	 * @param method
	 *            the method to set the parsed impact indicator for
	 * @param out
	 *            message writer
	 */
	private void processImpactIndicator( LCIAMethod method, PrintWriter out ) {
		// get xml element
		Element impactIndicatorElement = this.parserHelper
				.getElement( "/ilcd:LCIAMethodDataSet/ilcd:LCIAMethodInformation/ilcd:dataSetInformation/ilcd:impactIndicator" );

		// set the value
		if ( impactIndicatorElement != null ) {
			method.setImpactIndicator( impactIndicatorElement.getText() );
		}
	}

	/**
	 * Process the impact indicator and set it for the provided {@link LCIAMethod}
	 * 
	 * @param method
	 *            the method to set the parsed impact indicator for
	 * @param out
	 *            message writer
	 */
	private void processType( LCIAMethod method, PrintWriter out ) {
		// get xml element
		Element typeElement = this.parserHelper
				.getElement( "/ilcd:LCIAMethodDataSet/ilcd:modellingAndValidation/ilcd:LCIAMethodNormalisationAndWeighting/ilcd:typeOfDataSet" );

		// set the value
		if ( typeElement != null ) {
			try {
				method.setType( TypeOfLCIAMethodValue.fromValue( typeElement.getText() ) );
			}
			catch ( IllegalArgumentException e ) {
				if ( out != null ) {
					out.println( "Illegal type of LCIA method value (has been ignored): " + typeElement.getText() );
				}
			}
		}
	}

	/**
	 * Process all methodologies and add them to the provided {@link LCIAMethod}
	 * 
	 * @param method
	 *            the method to add the parsed methodologies to
	 */
	private void processMethodologies( LCIAMethod method, PrintWriter out ) {
		// get all xml elements
		List<Element> lstMethodologyElements = this.parserHelper
				.getElements( "/ilcd:LCIAMethodDataSet/ilcd:LCIAMethodInformation/ilcd:dataSetInformation/ilcd:methodology" );

		// no methodologies: nothing to do!
		if ( lstMethodologyElements == null || lstMethodologyElements.isEmpty() ) {
			return;
		}

		// add all values
		for ( Element methodologyElement : lstMethodologyElements ) {
			method.addMethodology( methodologyElement.getText() );
		}

	}

	/**
	 * Process all impact categories and add them to the provided {@link LCIAMethod}
	 * 
	 * @param method
	 *            the method to add the parsed impact categories to
	 * @param out
	 *            message writer
	 */
	private void processImpactCategories( LCIAMethod method, PrintWriter out ) {
		// get all xml elements
		List<Element> lstImpactCatElements = this.parserHelper
				.getElements( "/ilcd:LCIAMethodDataSet/ilcd:LCIAMethodInformation/ilcd:dataSetInformation/ilcd:impactCategory" );

		// no categories: nothing to do!
		if ( lstImpactCatElements == null || lstImpactCatElements.isEmpty() ) {
			return;
		}

		// add all values
		for ( Element impactElement : lstImpactCatElements ) {
			try {
				method.addImpactCategory( LCIAImpactCategoryValue.fromValue( impactElement.getText() ) );
			}
			catch ( IllegalArgumentException e ) {
				if ( out != null ) {
					out.println( "Illegal impact category value (has been ignored): " + impactElement.getText() );
				}
			}
		}
	}

	/**
	 * Process all areas of protection and add them to the provided {@link LCIAMethod}
	 * 
	 * @param method
	 *            the method to add the parsed areas of protection to
	 * @param out
	 *            message writer
	 */
	private void processAreasOfProtection( LCIAMethod method, PrintWriter out ) {
		// get all xml elements
		List<Element> lstAreaOfProtectElements = this.parserHelper
				.getElements( "/ilcd:LCIAMethodDataSet/ilcd:LCIAMethodInformation/ilcd:dataSetInformation/ilcd:areaOfProtection" );

		// no areas: nothing to do!
		if ( lstAreaOfProtectElements == null || lstAreaOfProtectElements.isEmpty() ) {
			return;
		}

		// add all values
		for ( Element areaElement : lstAreaOfProtectElements ) {
			try {
				method.addAreaOfProtection( AreaOfProtectionValue.fromValue( areaElement.getText() ) );
			}
			catch ( IllegalArgumentException e ) {
				if ( out != null ) {
					out.println( "Illegal area of protection value (has been ignored): " + areaElement.getText() );
				}
			}
		}
	}

	/**
	 * Process all {@link LCIAMethodCharacterisationFactor characterisation factors} and add them to the provided
	 * {@link LCIAMethod}
	 * 
	 * @param method
	 *            the method to add the parsed factors to
	 */
	private void processCharacterisationFactors( LCIAMethod method, PrintWriter out ) {
		// get all xml elements
		List<Element> lstFactorElements = this.parserHelper.getElements( "/ilcd:LCIAMethodDataSet/ilcd:characterisationFactors/ilcd:factor" );

		// no factors: nothing to do!
		if ( lstFactorElements == null || lstFactorElements.isEmpty() ) {
			return;
		}

		// process all xml elements
		for ( Element factorElement : lstFactorElements ) {
			// object to fill
			LCIAMethodCharacterisationFactor factor = new LCIAMethodCharacterisationFactor();

			// reference to flow
			GlobalReference flowReference = this.commonReader.getGlobalReference( factorElement, "referenceToFlowDataSet",
					LCIAMethodReader.NAMESPACE_LCIAMETHOD, out );
			if ( flowReference != null ) {
				factor.setFlowGlobalReference( flowReference );
			}

			// exchange direction
			String exchangeDirStr = factorElement.getChildText( "exchangeDirection", LCIAMethodReader.NAMESPACE_LCIAMETHOD );
			if ( exchangeDirStr != null ) {
				try {
					factor.setExchangeDirection( ExchangeDirectionValues.fromValue( exchangeDirStr ) );
				}
				catch ( IllegalArgumentException iae ) {
					if ( out != null ) {
						out.println( "Illegal exchange direction value (has been ignored): " + exchangeDirStr );
					}
				}
			}

			// mean value
			String meanValStr = factorElement.getChildText( "meanValue", LCIAMethodReader.NAMESPACE_LCIAMETHOD );
			if ( meanValStr != null && !meanValStr.trim().isEmpty() ) {
				try {
					factor.setMeanValue( Float.parseFloat( meanValStr ) );
				}
				catch ( NumberFormatException e ) {
					if ( out != null ) {
						out.println( "Illegal mean value (has been ignored): " + meanValStr );
					}
				}
			}
			else {
				out.println( "no mean value, has been ignored" );
			}

			// data derivation type status
			String dataDerivationTypeStatusStr = factorElement.getChildText( "dataDerivationTypeStatus", LCIAMethodReader.NAMESPACE_LCIAMETHOD );
			if ( dataDerivationTypeStatusStr != null ) {
				try {
					factor.setDataDerivationTypeStatus( DataDerivationTypeStatusValues.fromValue( dataDerivationTypeStatusStr ) );
				}
				catch ( IllegalArgumentException e ) {
					if ( out != null ) {
						out.println( "Illegal data derivation type status (has been ignored): " + dataDerivationTypeStatusStr );
					}
				}
			}

			// add to method
			method.addCharacterisationFactor( factor );
		}

	}

}
