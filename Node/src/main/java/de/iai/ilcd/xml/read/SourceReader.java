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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.PublicationTypeValue;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.DigitalFile;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.source.Source;
import de.iai.ilcd.util.CaseInsensitiveFilenameFilter;
import de.schlichtherle.io.File;

/**
 * 
 * @author clemens.duepmeier
 */
public class SourceReader extends DataSetReader {

	private static Logger logger = LoggerFactory.getLogger( SourceReader.class );

	@Override
	public Source parse( JXPathContext context, PrintWriter out ) {

		context.registerNamespace( "ilcd", "http://lca.jrc.it/ILCD/Source" );

		Source source = new Source();

		// OK, now read in all fields common to all DataSet types
		readCommonFields( source, DataSetType.SOURCE, context );

		IMultiLangString shortName = parserHelper.getIMultiLanguageString( "//common:shortName" );
		IMultiLangString citation = parserHelper.getIMultiLanguageString( "//ilcd:sourceCitation" );
		String publicationType = parserHelper.getStringValue( "//ilcd:publicationType" );
		PublicationTypeValue publicationTypeValue = PublicationTypeValue.UNDEFINED;
		if ( publicationType != null ) {
			try {
				publicationTypeValue = PublicationTypeValue.fromValue( publicationType );
			}
			catch ( Exception e ) {
				if ( out != null ) {
					out.println( "Warning: the field publicationType has an illegal value " + publicationType );
				}
			}
		}

		IMultiLangString description = parserHelper.getIMultiLanguageString( "//ilcd:sourceDescriptionOrComment" );

		source.setShortName( shortName );
		source.setName( shortName );
		source.setCitation( citation );
		source.setPublicationType( publicationTypeValue );
		source.setDescription( description );

		List<String> digitalFileReferences = parserHelper.getStringValues( "//ilcd:referenceToDigitalFile", "uri" );
		for ( String digitalFileReference : digitalFileReferences ) {
			logger.info( "raw source data set file name is {}", getDataSetFileName() );
			String baseDirectory = FilenameUtils.getFullPath( getDataSetFileName() );
			// get rid of any leading/trailing white space
			digitalFileReference = digitalFileReference.trim();
			String absoluteFileName = FilenameUtils.concat( baseDirectory, digitalFileReference );
			absoluteFileName = FilenameUtils.normalize( absoluteFileName );
			logger.info( "normalized form of digital file name is {}", absoluteFileName );
			logger.debug( "reference to digital file: " + absoluteFileName );
			File file = new File( absoluteFileName );
			DigitalFile digitalFile = new DigitalFile();
			logger.debug( "canread:" + (file.canRead()) );
			String fileName = null;
			if ( file.canRead() ) {
				fileName = absoluteFileName; // file will be imported and Name
											 // stripped after importing
			}
			else if ( absoluteFileName.toLowerCase().endsWith( ".jpg" ) || absoluteFileName.toLowerCase().endsWith( ".jpeg" )
					|| absoluteFileName.toLowerCase().endsWith( ".gif" ) || absoluteFileName.toLowerCase().endsWith( ".pdf" ) ) {
				// in case we're on a case sensitive filesystem and the case of the extension is not correct, we'll try
				// to fix this
				// TO DO this could be a little more elegant

				java.io.File parentDir = file.getParentFile();
				String fileNotFound = file.getName();
				String[] matches = parentDir.list( new CaseInsensitiveFilenameFilter( fileNotFound ) );
				if ( matches != null && matches.length == 1 ) {
					fileName = parentDir + File.separator + matches[0];
					logger.debug( fileName );
				}
				else {
					fileName = digitalFileReference;
				}
			}
			else {
				logger.debug( "file could not be read from " + file.getAbsolutePath() );
				fileName = digitalFileReference; // Not a local filename, likely
												 // be an URL to external
												 // resource
			} // logger.debug("found reference to digital file: " + fileName);
			logger.info( "set filename of digital file to {}", fileName );
			digitalFile.setFileName( fileName );
			source.addFile( digitalFile );
		}

		List<GlobalReference> contacts = commonReader.getGlobalReferences( "//ilcd:referenceToContact", out );
		for ( GlobalReference contact : contacts ) {
			source.addContact( contact );
		}

		return source;
	}
}
