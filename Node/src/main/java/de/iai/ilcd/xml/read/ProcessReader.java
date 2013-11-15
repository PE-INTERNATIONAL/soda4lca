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
import de.fzk.iai.ilcd.service.model.enums.CompletenessValue;
import de.fzk.iai.ilcd.service.model.enums.ComplianceValue;
import de.fzk.iai.ilcd.service.model.enums.DataQualityIndicatorName;
import de.fzk.iai.ilcd.service.model.enums.LCIMethodApproachesValue;
import de.fzk.iai.ilcd.service.model.enums.LCIMethodPrincipleValue;
import de.fzk.iai.ilcd.service.model.enums.LicenseTypeValue;
import de.fzk.iai.ilcd.service.model.enums.MethodOfReviewValue;
import de.fzk.iai.ilcd.service.model.enums.QualityValue;
import de.fzk.iai.ilcd.service.model.enums.ScopeOfReviewValue;
import de.fzk.iai.ilcd.service.model.enums.TypeOfProcessValue;
import de.fzk.iai.ilcd.service.model.enums.TypeOfQuantitativeReferenceValue;
import de.fzk.iai.ilcd.service.model.enums.TypeOfReviewValue;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.process.AccessInformation;
import de.iai.ilcd.model.process.ComplianceSystem;
import de.iai.ilcd.model.process.DataQualityIndicator;
import de.iai.ilcd.model.process.Exchange;
import de.iai.ilcd.model.process.ExchangeDirection;
import de.iai.ilcd.model.process.Geography;
import de.iai.ilcd.model.process.InternalQuantitativeReference;
import de.iai.ilcd.model.process.LCIMethodInformation;
import de.iai.ilcd.model.process.LciaResult;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.process.Review;
import de.iai.ilcd.model.process.ScopeOfReview;
import de.iai.ilcd.model.process.TimeInformation;

/**
 * 
 * @author clemens.duepmeier
 */
public class ProcessReader extends DataSetReader {

	public static Logger logger = LoggerFactory.getLogger( de.iai.ilcd.xml.read.ProcessReader.class );

	private final Namespace processNamespace = Namespace.getNamespace( "ilcd", "http://lca.jrc.it/ILCD/Process" );

	private final Namespace commonNamespace = Namespace.getNamespace( "common", "http://lca.jrc.it/ILCD/Common" );

	@Override
	public Process parse( JXPathContext context, PrintWriter out ) {

		context.registerNamespace( "ilcd", "http://lca.jrc.it/ILCD/Process" );
		context.registerNamespace( "pm", "http://iai.kit.edu/ILCD/ProductModel" );

		Process process = new Process();

		// OK, now read in all fields common to all DataSet types
		this.readCommonFields( process, DataSetType.PROCESS, context );

		// build the complex name of the process
		IMultiLangString baseName = this.parserHelper.getIMultiLanguageString( "//ilcd:baseName" );
		IMultiLangString treatmentName = this.parserHelper.getIMultiLanguageString( "//ilcd:treatmentStandardsRoutes" );
		IMultiLangString mixAndLocationTypes = this.parserHelper.getIMultiLanguageString( "//ilcd:mixAndLocationTypes" );
		IMultiLangString functionalUnitFlowProperties = this.parserHelper.getIMultiLanguageString( "//ilcd:functionalUnitFlowProperties" );
		process.setBaseName( baseName );
		process.setNameRoute( treatmentName );
		process.setNameLocation( mixAndLocationTypes );
		process.setNameUnit( functionalUnitFlowProperties );

		IMultiLangString synonyms = this.parserHelper.getIMultiLanguageString( "//common:synonyms" );
		process.setSynonyms( synonyms );

		String processType = this.parserHelper.getStringValue( "//ilcd:typeOfDataSet" );
		if ( processType != null ) {
			try {
				TypeOfProcessValue processTypeValue = TypeOfProcessValue.fromValue( processType );
				process.setType( processTypeValue );
			}
			catch ( Exception e ) {
				logger.error( "error setting type", e.getMessage() );
			}
		}

		if ( this.parserHelper.getElements( "//ilcd:variableParameter" ).size() > 0 ) {
			process.setParameterized( true );
		}
		else {
			process.setParameterized( false );
		}

		if ( this.parserHelper.getElements( "/ilcd:processDataSet/ilcd:processInformation/common:other/pm:productModel" ).size() > 0 ) {
			process.setContainsProductModel( true );
		}
		else {
			process.setContainsProductModel( false );
		}

		IMultiLangString useAdvice = this.parserHelper.getIMultiLanguageString( "//ilcd:useAdviceForDataSet" );
		process.setUseAdvice( useAdvice );

		IMultiLangString technicalPurpose = this.parserHelper.getIMultiLanguageString( "//ilcd:technologicalApplicability" );
		process.setTechnicalPurpose( technicalPurpose );

		Element locationElement = this.parserHelper.getElement( "//ilcd:locationOfOperationSupplyOrProduction" );
		if ( locationElement != null ) {
			Geography geography = new Geography();
			String location = locationElement.getAttributeValue( "location" );
			IMultiLangString locationDescription = this.parserHelper.getIMultiLanguageString( locationElement, "descriptionOfRestrictions",
					this.processNamespace );
			geography.setLocation( location );
			geography.setDescription( locationDescription );
			process.setGeography( geography );
		}

		TimeInformation timeInformation = new TimeInformation();
		String referenceYearString = this.parserHelper.getStringValue( "//common:referenceYear" );
		if ( referenceYearString != null && referenceYearString.length() > 0 ) {
			int referenceYear = Integer.parseInt( referenceYearString );
			timeInformation.setReferenceYear( referenceYear );
		}

		String dataSetValidUntilString = this.parserHelper.getStringValue( "//common:dataSetValidUntil" );
		if ( dataSetValidUntilString != null && dataSetValidUntilString.length() > 0 ) {
			int dataSetValidUntil = Integer.parseInt( dataSetValidUntilString );
			timeInformation.setValidUntil( dataSetValidUntil );
		}

		IMultiLangString timeDescription = this.parserHelper.getIMultiLanguageString( "//common:timeRepresentativenessDescription" );
		timeInformation.setDescription( timeDescription );
		process.setTimeInformation( timeInformation );

		GlobalReference formatRef = this.commonReader.getGlobalReference( "//common:referenceToDataSetFormat", out );
		if ( formatRef != null ) {
			String format = formatRef.getShortDescription().getDefaultValue();
			process.setFormat( format );
		}

		GlobalReference ownerOfDataSet = this.commonReader.getGlobalReference( "//common:referenceToOwnershipOfDataSet", out );
		process.setOwnerReference( ownerOfDataSet );

		LCIMethodInformation lciMethodInformation = new LCIMethodInformation();
		String lciMethodPrinciple = this.parserHelper.getStringValue( "//ilcd:LCIMethodPrinciple" );
		LCIMethodPrincipleValue principleValue = LCIMethodPrincipleValue.OTHER;
		try {
			principleValue = LCIMethodPrincipleValue.fromValue( lciMethodPrinciple );
		}
		catch ( IllegalArgumentException ex ) {
			if ( out != null ) {
				out.println( "Warning: lciMethodPrinciple has the illegal value " + lciMethodPrinciple + "; will set it to 'Other'" );
			}
		}

		lciMethodInformation.setMethodPrinciple( principleValue );

		List<String> allocationApproaches = this.parserHelper.getStringValues( "//ilcd:LCIMethodApproaches" );
		for ( String allocationApproach : allocationApproaches ) {
			LCIMethodApproachesValue approachValue = LCIMethodApproachesValue.fromValue( allocationApproach );
			lciMethodInformation.addApproach( approachValue );
		}
		process.setLCIMethodInformation( lciMethodInformation );

		String completeness = this.parserHelper.getStringValue( "//ilcd:completenessProductModel" );

		CompletenessValue completenessValue = CompletenessValue.NO_STATEMENT;
		if ( completeness != null ) {
			try {
				completenessValue = CompletenessValue.fromValue( completeness );
			}
			catch ( IllegalArgumentException e ) {
				if ( out != null ) {
					out.println( "Warning: The field completenessProductModel in the data set has an illegal value" );
				}
			}
		}

		process.setCompleteness( completenessValue );
		List<Review> reviews = this.getReviewInformation( out );
		for ( Review review : reviews ) {
			process.addReview( review );
		}

		AccessInformation accessInformation = new AccessInformation();
		String copyrightString = this.parserHelper.getStringValue( "//common:copyright" );
		if ( copyrightString != null && copyrightString.equals( "true" ) ) {
			accessInformation.setCopyright( true );
		}
		else {
			accessInformation.setCopyright( false );
		}
		String licenseType = this.parserHelper.getStringValue( "//ilcd:licenseType" );
		LicenseTypeValue licenseTypeValue = null;
		if ( licenseType != null ) {
			try {
				licenseTypeValue = LicenseTypeValue.fromValue( licenseType );
			}
			catch ( IllegalArgumentException ex ) {
				if ( out != null ) {
					out.println( "Warning: Licence type {%s} is an illegal license type" );
				}
			}
		}

		accessInformation.setLicenseType( licenseTypeValue );
		IMultiLangString useRestrictions = this.parserHelper.getIMultiLanguageString( "//common:accessRestrictions" );

		accessInformation.setUseRestrictions( useRestrictions );

		process.setAccessInformation( accessInformation );
		// OK, now find all compliance declarations
		List<ComplianceSystem> compliances = this.getCompliances( out );
		for ( ComplianceSystem compliance : compliances ) {
			process.addComplianceSystem( compliance );
		}

		GlobalReference approvedBy = this.commonReader.getGlobalReference( "//common:referenceToDataSetUseApproval", out );

		process.setApprovedBy( approvedBy );
		InternalQuantitativeReference quantitativeRef = this.getQuantitativeReference();

		process.setInternalReference( quantitativeRef );
		// OK, now find all exchanges
		List<Exchange> exchanges = this.getExchanges( out );
		for ( Exchange exchange : exchanges ) {
			process.addExchange( exchange );
		}

		if ( exchanges.size() > 0 ) {
			process.setExchangesIncluded( true );
		}
		// OK, last we need to get LciaResults
		List<LciaResult> results = this.getLciaResults( out );
		for ( LciaResult result : results ) {
			process.addLciaResult( result );
		}

		if ( results.size() > 0 ) {
			process.setResultsIncluded( true );
		}
		return process;
	}

	private List<Review> getReviewInformation( PrintWriter out ) {

		List<Review> reviews = new ArrayList<Review>();

		List<Element> reviewElements = this.parserHelper.getElements( "//ilcd:review" );

		for ( Element reviewElement : reviewElements ) {
			Review review = new Review();
			String reviewType = reviewElement.getAttributeValue( "type" );
			TypeOfReviewValue reviewTypeValue = null;
			try {
				reviewTypeValue = TypeOfReviewValue.fromValue( reviewType );
			}
			catch ( IllegalArgumentException ex ) {
				// no value assigned
				if ( out != null ) {
					out.println( "Warning: One of the review information has an illegal review type value or no type value at all" );
				}
			}
			review.setType( reviewTypeValue );
			// logger.debug("found review with type: " + reviewType);
			List<Element> scopeElements = reviewElement.getChildren( "scope", this.commonNamespace );

			for ( Element scopeElement : scopeElements ) {
				String scopeName = scopeElement.getAttributeValue( "name" );
				// logger.debug("Scope name: " + scopeName);
				ScopeOfReviewValue scopeValue = null;
				try {
					scopeValue = ScopeOfReviewValue.fromValue( scopeName );
				}
				catch ( IllegalArgumentException ex ) {
					// no value assigned
					if ( out != null ) {
						out.println( "Warning: Review section contains scope definition without scope name; will ignore this scope section" );
					}
					continue;
				}
				ScopeOfReview scope = new ScopeOfReview( scopeValue );
				List<Element> scopeMethodsElements = scopeElement.getChildren( "method", this.commonNamespace );

				for ( Element scopeMethodElement : scopeMethodsElements ) {
					String methodName = scopeMethodElement.getAttributeValue( "name" );
					// logger.debug("scope of review method name: " +
					// methodName);
					MethodOfReviewValue methodValue = null;
					try {
						methodValue = MethodOfReviewValue.fromValue( methodName );
					}
					catch ( IllegalArgumentException ex ) {
						// no value assigned
						if ( out != null ) {
							out.println( "Warning: The scope section of type " + scopeValue.toString() + " contains an unknown method; will ignore this method" );
						}
						continue;
					}
					scope.addMethod( methodValue );
				}
				review.addScope( scope );
			}

			IMultiLangString reviewDetails = this.parserHelper.getIMultiLanguageString( reviewElement, "reviewDetails", this.commonNamespace );

			if ( reviewDetails != null ) {
				review.setReviewDetails( reviewDetails );
			}

			IMultiLangString otherReviewDetails = this.parserHelper.getIMultiLanguageString( reviewElement, "otherReviewDetails", this.commonNamespace );

			if ( otherReviewDetails != null ) {
				review.setOtherReviewDetails( otherReviewDetails );
			}
			List<GlobalReference> reviewers = this.commonReader.getGlobalReferences( reviewElement, "referenceToNameOfReviewerAndInstitution",
					this.commonNamespace, out );

			for ( GlobalReference reviewer : reviewers ) {
				review.addReferenceToReviewers( reviewer );
			}

			GlobalReference reviewReport = this.commonReader.getGlobalReference( reviewElement, "referenceToCompleteReviewReport", this.commonNamespace, out );
			review.setReferenceToReport( reviewReport );

			// data quality indicators
			Element dqiElement = reviewElement.getChild( "dataQualityIndicators", this.commonNamespace );

			if ( dqiElement != null ) {
				String[][] dataQualityIndicators = this.parserHelper
						.getStringValues( dqiElement, "dataQualityIndicator", "name", "value", this.commonNamespace );

				for ( int i = 0; i < dataQualityIndicators.length; i++ ) {
					DataQualityIndicator dqi = new DataQualityIndicator( DataQualityIndicatorName.fromValue( dataQualityIndicators[i][0] ), QualityValue
							.fromValue( dataQualityIndicators[i][1] ) );
					review.addQualityIndicator( dqi );
				}

			}

			reviews.add( review );
		}

		return reviews;

	}

	private List<Exchange> getExchanges( PrintWriter out ) {
		List<Exchange> exchanges = new ArrayList<Exchange>();

		List<Element> exchangeElements = this.parserHelper.getElements( "//ilcd:exchange" );

		try {
			for ( Element exchangeElement : exchangeElements ) {
				Exchange exchange = new Exchange();

				String internalIdString = exchangeElement.getAttributeValue( "dataSetInternalID" );
				exchange.setInternalId( Integer.parseInt( internalIdString ) );

				GlobalReference flowReference = this.commonReader.getGlobalReference( exchangeElement, "referenceToFlowDataSet", this.processNamespace, out );
				exchange.setFlowReference( flowReference );

				// logger.debug("found exchange " +
				// flowReference.getShortDescription().getDefaultValue() +
				// " mit internal id " + internalIdString);

				String exchangeDirectionString = exchangeElement.getChildText( "exchangeDirection", this.processNamespace );

				if ( exchangeDirectionString.equals( "Output" ) ) {
					exchange.setExchangeDirection( ExchangeDirection.OUTPUT );

				}
				else {
					exchange.setExchangeDirection( ExchangeDirection.INPUT );

				}

				String meanAmountString = exchangeElement.getChildText( "meanAmount", this.processNamespace );

				if ( meanAmountString != null ) {
					float meanAmount = Float.parseFloat( meanAmountString );
					exchange.setMeanAmount( meanAmount );

				}

				String resultingAmountString = exchangeElement.getChildText( "resultingAmount", this.processNamespace );

				if ( resultingAmountString != null ) {
					float resultingAmount = Float.parseFloat( resultingAmountString );
					exchange.setResultingAmount( resultingAmount );

				}

				String minimumAmountString = exchangeElement.getChildText( "minimumAmount", this.processNamespace );

				if ( minimumAmountString != null ) {
					float minimumAmount = Float.parseFloat( minimumAmountString );
					exchange.setMinimumAmount( minimumAmount );

				}

				String maximumAmountString = exchangeElement.getChildText( "maximumAmount", this.processNamespace );

				if ( maximumAmountString != null ) {
					float maximumAmount = Float.parseFloat( maximumAmountString );
					exchange.setMaximumAmount( maximumAmount );

				}

				String location = exchangeElement.getChildText( "location", this.processNamespace );
				exchange.setLocation( location );
				String functionType = exchangeElement.getChildText( "functionType", this.processNamespace );
				exchange.setFunctionType( functionType );
				String referenceToVariable = exchangeElement.getChildText( "referenceToVariable", this.processNamespace );

				if ( referenceToVariable != null ) {
					exchange.setReferenceToVariable( referenceToVariable );

				}
				String uncertaintyType = exchangeElement.getChildText( "uncertaintyDistributionType", this.processNamespace );
				exchange.setUncertaintyDistribution( uncertaintyType );
				String standardDeviation = exchangeElement.getChildText( "relativeStandardDeviation95In", this.processNamespace );

				if ( standardDeviation != null ) {
					try {
						exchange.setStandardDeviation( Float.parseFloat( standardDeviation ) );

					}
					catch ( NumberFormatException e ) {
						// wrong format ignore
					}
				}
				String allocations = exchangeElement.getChildText( "allocations", this.processNamespace );
				exchange.setAllocation( allocations );
				String dataSourceType = exchangeElement.getChildText( "dataSourceType", this.processNamespace );
				exchange.setDataSource( dataSourceType );
				String derivationType = exchangeElement.getChildText( "dataDerivationTypeStatus", this.processNamespace );
				exchange.setDerivationType( derivationType );
				IMultiLangString comment = this.parserHelper.getIMultiLanguageString( exchangeElement, "generalComment", this.processNamespace );
				exchange.setComment( comment );

				exchanges.add( exchange );

			}
		}
		catch ( Exception e ) {
			return null;
		}

		return exchanges;

	}

	private List<ComplianceSystem> getCompliances( PrintWriter out ) {
		List<ComplianceSystem> compliances = new ArrayList<ComplianceSystem>();

		List<Element> complianceElements = this.parserHelper.getElements( "//ilcd:compliance" );

		for ( Element complianceElement : complianceElements ) {
			ComplianceSystem compliance = new ComplianceSystem();
			GlobalReference complianceSystemRef = this.commonReader.getGlobalReference( complianceElement, "referenceToComplianceSystem", this.commonNamespace,
					out );
			compliance.setSourceReference( complianceSystemRef );
			// logger.debug("create new compliance Entry for: " +
			// complianceSystemRef.getShortDescription().getDefaultValue());
			String overallCompliance = complianceElement.getChildText( "approvalOfOverallCompliance", this.commonNamespace );
			ComplianceValue overallComplianceValue;

			try {
				overallComplianceValue = ComplianceValue.fromValue( overallCompliance );

			}
			catch ( Exception e ) {
				overallComplianceValue = ComplianceValue.NOT_DEFINED;

			}
			compliance.setOverallCompliance( overallComplianceValue );
			String nomenclatureCompliance = complianceElement.getChildText( "nomenclatureCompliance", this.commonNamespace );
			ComplianceValue nomenclatureComplianceValue;

			try {
				nomenclatureComplianceValue = ComplianceValue.fromValue( nomenclatureCompliance );

			}
			catch ( Exception e ) {
				nomenclatureComplianceValue = ComplianceValue.NOT_DEFINED;

			}
			compliance.setNomenclatureCompliance( nomenclatureComplianceValue );
			String methodologicalCompliance = complianceElement.getChildText( "methodologicalCompliance", this.commonNamespace );
			ComplianceValue methodologicalComplianceValue;

			try {
				methodologicalComplianceValue = ComplianceValue.fromValue( methodologicalCompliance );

			}
			catch ( Exception e ) {
				methodologicalComplianceValue = ComplianceValue.NOT_DEFINED;

			}
			compliance.setMethodologicalCompliance( methodologicalComplianceValue );
			String reviewCompliance = complianceElement.getChildText( "reviewCompliance", this.commonNamespace );
			ComplianceValue reviewComplianceValue;

			try {
				reviewComplianceValue = ComplianceValue.fromValue( reviewCompliance );

			}
			catch ( Exception e ) {
				reviewComplianceValue = ComplianceValue.NOT_DEFINED;

			}
			compliance.setReviewCompliance( reviewComplianceValue );
			String documentationCompliance = complianceElement.getChildText( "documentationCompliance", this.commonNamespace );
			ComplianceValue documentationComplianceValue;

			try {
				documentationComplianceValue = ComplianceValue.fromValue( documentationCompliance );

			}
			catch ( Exception e ) {
				documentationComplianceValue = ComplianceValue.NOT_DEFINED;

			}
			compliance.setDocumentationCompliance( documentationComplianceValue );
			String qualityCompliance = complianceElement.getChildText( "qualityCompliance", this.commonNamespace );
			ComplianceValue qualityComplianceValue;

			try {
				qualityComplianceValue = ComplianceValue.fromValue( qualityCompliance );

			}
			catch ( Exception e ) {
				qualityComplianceValue = ComplianceValue.NOT_DEFINED;

			}
			compliance.setQualityCompliance( qualityComplianceValue );

			compliances.add( compliance );

		}

		return compliances;

	}

	private InternalQuantitativeReference getQuantitativeReference() {
		InternalQuantitativeReference quantitativeRef = new InternalQuantitativeReference();

		Element refElement = this.parserHelper.getElement( "//ilcd:quantitativeReference" );

		if ( refElement != null ) {
			String type = refElement.getAttributeValue( "type" );
			TypeOfQuantitativeReferenceValue typeValue = TypeOfQuantitativeReferenceValue.fromValue( type );
			quantitativeRef.setType( typeValue );

			List<Element> flowReferences = refElement.getChildren( "referenceToReferenceFlow", this.processNamespace );

			for ( Element flowRefElement : flowReferences ) {
				String refString = flowRefElement.getText();
				// logger.debug("found reference to reference flow: " +
				// refString);

				if ( refString != null ) {
					quantitativeRef.addReferenceId( Integer.parseInt( refString ) );

				}
			}
		}

		IMultiLangString functionalUnitOrOther = this.parserHelper.getIMultiLanguageString( refElement, "functionalUnitOrOther", this.processNamespace );

		if ( functionalUnitOrOther != null ) {
			quantitativeRef.setOtherReference( functionalUnitOrOther );

		}

		return quantitativeRef;

	}

	private List<LciaResult> getLciaResults( PrintWriter out ) {
		List<LciaResult> results = new ArrayList<LciaResult>();

		List<Element> resultElements = this.parserHelper.getElements( "//ilcd:LCIAResult" );

		for ( Element resultElement : resultElements ) {
			LciaResult result = new LciaResult();

			GlobalReference methodReference = this.commonReader.getGlobalReference( resultElement, "referenceToLCIAMethodDataSet", this.processNamespace, out );
			result.setMethodReference( methodReference );

			String meanAmountString = resultElement.getChildText( "meanAmount", this.processNamespace );

			if ( meanAmountString != null ) {
				float meanAmount = Float.parseFloat( meanAmountString );
				result.setMeanAmount( meanAmount );

			}

			String uncertaintyType = resultElement.getChildText( "uncertaintyDistributionType", this.processNamespace );
			result.setUncertaintyDistribution( uncertaintyType );
			String standardDeviation = resultElement.getChildText( "relativeStandardDeviation95In", this.processNamespace );

			if ( standardDeviation != null ) {
				try {
					result.setStandardDeviation( Float.parseFloat( standardDeviation ) );

				}
				catch ( NumberFormatException e ) {
					// wrong format ignore
				}
			}

			IMultiLangString comment = this.parserHelper.getIMultiLanguageString( resultElement, "generalComment", this.processNamespace );
			result.setComment( comment );

			results.add( result );

		}

		return results;

	}
}
