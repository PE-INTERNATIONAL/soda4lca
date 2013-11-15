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

import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.iai.ilcd.model.common.DigitalFile;
import de.iai.ilcd.model.dao.SourceDao;
import de.iai.ilcd.model.source.Source;

/**
 * REST Web Service
 * 
 * @author clemens.duepmeier
 */

@Component
@Path( "external_docs" )
public class FileResource {

	public static Logger logger = LoggerFactory.getLogger( de.iai.ilcd.services.FileResource.class );

	@Context
	private UriInfo context;

	/** Creates a new instance of FileResource */
	public FileResource() {
	}

	@GET
	@Path( "{sourceId}/{fileName}" )
	@Produces( { "image/*", "application/*" } )
	public Response getExternalFile( @PathParam( "sourceId" ) String sourceId, @PathParam( "fileName" ) String fileName ) {

		SourceDao sourceDao = new SourceDao();
		Source source = sourceDao.getByDataSetId( sourceId );

		if ( source == null )
			throw new WebApplicationException( 404 );

		DigitalFile requestedFile = null;
		for ( DigitalFile file : source.getFiles() ) {
			if ( file.getFileName().equals( fileName ) ) {
				requestedFile = file;
				break;
			}

		}

		if ( requestedFile == null )
			throw new WebApplicationException( 404 );

		// logger.trace("I am here with file: "
		// +requestedFile.getAbsoluteFileName());

		File file = new File( requestedFile.getAbsoluteFileName() );
		if ( !file.exists() )
			throw new WebApplicationException( 404 );

		String mt = new MimetypesFileTypeMap().getContentType( file );
		return Response.ok( file, mt ).build();
	}

	/**
	 * PUT method for updating or creating an instance of FileResource
	 * 
	 * @param content
	 *            representation for the resource
	 * @return an HTTP response with content of the updated or created resource.
	 */
	@PUT
	@Consumes( "application/xml" )
	public void putXml( String content ) {
	}
}
