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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.xml.JDOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.model.common.Classification;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.common.Uuid;
import de.iai.ilcd.model.common.XmlFile;
import de.iai.ilcd.model.common.exception.FormatException;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;

/**
 * 
 * @author clemens.duepmeier
 */
public abstract class DataSetReader {

	private static final Logger logger = LoggerFactory.getLogger( DataSetReader.class );

	protected DataSetParsingHelper parserHelper = null;

	protected CommonConstructsReader commonReader = null;

	protected String dataSetFileName = null;

	PrintWriter out = null;

	public DataSet readFromFile( String fileName, PrintWriter out ) throws FileNotFoundException, IOException, UnsupportedEncodingException {

		File file = new File( fileName );
		return readDataSetFromFile( file, out );

	}

	public String getDataSetFileName() {
		return dataSetFileName;
	}

	public DataSet readDataSetFromFile( File file, PrintWriter out ) throws FileNotFoundException, IOException, UnsupportedEncodingException {

		dataSetFileName = file.getAbsolutePath();
		this.out = out;

		DataSet dataSet = null;

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream( file );
		}
		catch ( Exception e ) {
			out.println( "Warning: Cannot find file " + file.getName() );
			throw new FileNotFoundException( "File " + file.getName() + " cannot be found" );
		}
		dataSet = readDataSetFromStream( inputStream, out );
		inputStream.close();

		return dataSet;
	}

	// TODO: this method does not work with Source data sets, because source
	// data sets need to find referenced digital files
	// and this assumes that the files can be found via the path of the source
	// data set. But this path is not given with
	// only the InputStream
	public DataSet readDataSetFromStream( InputStream inStream, PrintWriter out ) throws IOException, UnsupportedEncodingException {
		byte[] contents = null;
		try {
			contents = IOUtils.toByteArray( inStream );
		}
		catch ( IOException e ) {
			logger.error( "cannot read the whole input stream into byte array" );
			throw e;
		}
		DataSet dataSet = this.parseStream( new ByteArrayInputStream( contents ) );

		XmlFile xmlFile = new XmlFile();
		String xml;
		try {
			xml = new String( contents, "UTF-8" );
		}
		catch ( UnsupportedEncodingException e ) {
			logger.error( "cannot parse stream input into an UTF-8 string" );
			logger.error( "Exception is: ", e );
			throw e;
		}
		// if BOM is in front of ByteArray, we may have some unknown characters in front of <xml because
		// UTF-8 conversion in Standard Java has problems with BOM, let's just filter these nasty characters
		if ( !xml.startsWith( "<" ) )
			xml = xml.trim().replaceFirst( "^([\\W]+)<", "<" ); // remove junk at front of dataset
		xmlFile.setContent( xml );
		dataSet.setXmlFile( xmlFile );

		return dataSet;
	}

	private DataSet parseStream( InputStream inputStream ) {

		JDOMParser parser = new JDOMParser();
		parser.setValidating( false );
		Object doc = parser.parseXML( inputStream );
		JXPathContext context = JXPathContext.newContext( doc );
		context.setLenient( true );
		context.registerNamespace( "common", "http://lca.jrc.it/ILCD/Common" );

		parserHelper = new DataSetParsingHelper( context );
		commonReader = new CommonConstructsReader( parserHelper );

		return parse( context, out );
	}

	protected abstract DataSet parse( JXPathContext context, PrintWriter out );

	public void readCommonFields( DataSet dataset, DataSetType dataSetType, JXPathContext context ) {

		IMultiLangString name = parserHelper.getIMultiLanguageString( "//common:name" );
		Uuid uuid = commonReader.getUuid();
		IMultiLangString generalComment = parserHelper.getIMultiLanguageString( "//common:generalComment" );
		String permanentUri = parserHelper.getStringValue( "//common:permanentDataSetURI" );
		String versionString = parserHelper.getStringValue( "//common:dataSetVersion" );

		dataset.setName( name );
		dataset.setUuid( uuid );
		dataset.setDescription( generalComment );
		dataset.setPermanentUri( permanentUri );
		DataSetVersion version = new DataSetVersion();
		try {
			if ( versionString != null )
				version = DataSetVersion.parse( versionString );
			else {
				if ( out != null )
					out.println( "Warning: This data set has no version number; version will be set to 00.00.000" );
			}
		}
		catch ( FormatException ex ) {
			if ( out != null ) {
				out.println( "Warning: The version number has an invalid format. See below for details." );
				out.println( "Exception output: " + ex.getMessage() );
			}
		}
		dataset.setVersion( version );

		// get the list of classifications and add them
		List<Classification> classifications = commonReader.getClassifications( dataSetType );
		for ( Classification classification : classifications ) {
			// set default classification
			if ( classification.getName().equalsIgnoreCase( "ilcd" ) ) {
				dataset.setClassification( classification );
			}
		}
		if ( dataset.getClassification().getClasses().isEmpty() && classifications.size() > 0 ) {
			dataset.setClassification( classifications.get( 0 ) ); // we need a default classification system, even
			// if it's not the ILCD classification
		}
	}
}
