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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import de.fzk.iai.ilcd.api.dataset.ILCDTypes;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.DigitalFile;
import de.iai.ilcd.model.common.GlobalRefUriAnalyzer;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.dao.SourceDao;
import de.iai.ilcd.model.source.Source;

/**
 * REST Web Service
 */
@Component
@Path( "sources" )
public class SourceResource extends AbstractDataSetResource<Source> {

	public SourceResource() {
		super( DataSetType.SOURCE, ILCDTypes.SOURCE );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetDao<Source, ?, ?> getFreshDaoInstance() {
		return new SourceDao();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getXMLListTemplatePath() {
		return "/xml/sources.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getXMLTemplatePath() {
		return "/xml/source.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHTMLOverviewTemplatePath() {
		return "/html/source_overview.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDataSetTypeName() {
		return "source";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHTMLFullViewTemplatePath() {
		return "/html/source.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean userRequiresFullViewRights() {
		return false;
	}

	/**
	 * Get an external file from the source
	 * 
	 * @param uuid
	 *            UUID of the source
	 * @param fileName
	 *            name of the file to get
	 * @return response for client
	 */
	@GET
	@Path( "{uuid}/{fileName}" )
	@Produces( { "image/*", "application/*" } )
	public Response getExternalFile( @PathParam( "uuid" ) String uuid, @PathParam( "fileName" ) String fileName ) {

		DataSetDao<Source, ?, ?> daoObject = this.getFreshDaoInstance();

		// fix uuid, if not in the right format
		GlobalRefUriAnalyzer analyzer = new GlobalRefUriAnalyzer( uuid );
		uuid = analyzer.getUuidAsString();

		Source source = daoObject.getByUuid( uuid );

		if ( source == null ) {
			throw new WebApplicationException( 404 );
		}

		DigitalFile requestedFile = null;
		for ( DigitalFile file : source.getFiles() ) {
			if ( file.getFileName().equals( fileName ) ) {
				requestedFile = file;
				break;
			}

		}

		if ( requestedFile == null ) {
			throw new WebApplicationException( 404 );
		}

		File file = new File( requestedFile.getAbsoluteFileName() );
		if ( !file.exists() ) {
			throw new WebApplicationException( 404 );
		}

		String mt = new MimetypesFileTypeMap().getContentType( file );
		// if it's a PDF document, set MIME type to application/pdf
		if ( file.getName().toLowerCase().endsWith( ".pdf" ) )
			mt = "application/pdf";

		return Response.ok( file, mt ).build();
	}

	/**
	 * Get the first external file from the source
	 * 
	 * @param uuid
	 *            UUID of the source
	 * @return response for client
	 */
	@GET
	@Path( "{uuid}/digitalfile" )
	@Produces( { "image/*", "application/*" } )
	public Response getExternalFile( @PathParam( "uuid" ) String uuid ) {

		DataSetDao<Source, ?, ?> daoObject = this.getFreshDaoInstance();

		// fix uuid, if not in the right format
		GlobalRefUriAnalyzer analyzer = new GlobalRefUriAnalyzer( uuid );
		uuid = analyzer.getUuidAsString();

		Source source = daoObject.getByUuid( uuid );

		if ( source == null ) {
			throw new WebApplicationException( 404 );
		}

		DigitalFile requestedFile = null;
		for ( DigitalFile file : source.getFiles() ) {
				requestedFile = file;
				break;
		}

		if ( requestedFile == null ) {
			throw new WebApplicationException( 404 );
		}

		File file = new File( requestedFile.getAbsoluteFileName() );
		if ( !file.exists() ) {
			throw new WebApplicationException( 404 );
		}

		String mt = new MimetypesFileTypeMap().getContentType( file );
		// if it's a PDF document, set MIME type to application/pdf
		if ( file.getName().toLowerCase().endsWith( ".pdf" ) )
			mt = "application/pdf";

		return Response.ok( file, mt ).build();
	}
}
