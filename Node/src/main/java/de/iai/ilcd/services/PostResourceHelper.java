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

package de.iai.ilcd.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.datastock.RootDataStock;
import de.iai.ilcd.xml.read.DataSetImporter;

/**
 * 
 * @author clemens.duepmeier
 */
public class PostResourceHelper {

	private static final Logger logger = LoggerFactory.getLogger( ProcessResource.class );

	public Response importByFileUpload( DataSetType type, InputStream fileInputStream, RootDataStock rds ) {

		String tempFileName = ConfigurationService.INSTANCE.getUniqueUploadFileName( "IMPORT", "xml" );

		try { // we have to copy the uploaded file to a temporary space first; maybe later we find a better solution
			FileOutputStream fos = new FileOutputStream( tempFileName );
			IOUtils.copy( fileInputStream, fos );
			fileInputStream.close();
			fos.close();
		}
		catch ( IOException e ) {
			logger.error( "Cannot save an uploaded file to temporary storage" );
			logger.error( "exception is: ", e );
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( "Cannot save uploaded file to temporary storage" ).build();
		}

		DataSetImporter importer = new DataSetImporter();
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter( writer );

		try {
			if ( importer.importFile( type, tempFileName, out, rds ) == true ) {
				logger.info( "data set successfully imported." );
				logger.info( "{}", writer.getBuffer() );
				out.println( "data set successfully imported." );
				out.flush();
				return Response.ok( writer.getBuffer().toString(), MediaType.TEXT_PLAIN ).build();
			}
			else {
				logger.error( "Cannot import data set" );
				logger.error( "output is: {}", writer.getBuffer() );
				return Response.status( Response.Status.NOT_ACCEPTABLE ).type( MediaType.TEXT_PLAIN ).entity( writer.getBuffer().toString() ).build();
			}
		}
		catch ( Exception e ) {
			logger.error( "cannot import data set" );
			logger.error( "exception is: ", e );
			return Response.status( Response.Status.BAD_REQUEST ).type( MediaType.TEXT_PLAIN ).entity( writer.getBuffer().toString() ).build();
		}
	}
}
