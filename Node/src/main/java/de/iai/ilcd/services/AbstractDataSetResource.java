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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.view.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import de.fzk.iai.ilcd.api.binding.helper.DatasetHelper;
import de.fzk.iai.ilcd.api.dataset.ILCDTypes;
import de.fzk.iai.ilcd.service.client.impl.DatasetTypes;
import de.fzk.iai.ilcd.service.client.impl.ServiceDAO;
import de.fzk.iai.ilcd.service.client.impl.vo.DataStockVO;
import de.fzk.iai.ilcd.service.client.impl.vo.DatasetVODAO;
import de.fzk.iai.ilcd.service.client.impl.vo.dataset.DataSetList;
import de.fzk.iai.ilcd.service.client.impl.vo.dataset.DataSetVO;
import de.fzk.iai.ilcd.service.model.IContactListVO;
import de.fzk.iai.ilcd.service.model.IDataSetListVO;
import de.fzk.iai.ilcd.service.model.IFlowListVO;
import de.fzk.iai.ilcd.service.model.IFlowPropertyListVO;
import de.fzk.iai.ilcd.service.model.ILCIAMethodListVO;
import de.fzk.iai.ilcd.service.model.IProcessListVO;
import de.fzk.iai.ilcd.service.model.ISourceListVO;
import de.fzk.iai.ilcd.service.model.IUnitGroupListVO;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.adapter.DataSetListAdapter;
import de.iai.ilcd.model.adapter.DataStockListAdapter;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.common.GlobalRefUriAnalyzer;
import de.iai.ilcd.model.common.exception.FormatException;
import de.iai.ilcd.model.dao.CommonDataStockDao;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.datastock.AbstractDataStock;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.model.datastock.RootDataStock;
import de.iai.ilcd.security.SecurityUtil;
import de.iai.ilcd.webgui.controller.DirtyFlagBean;
import de.iai.ilcd.webgui.controller.ui.AvailableStockHandler;

/**
 * REST web service for data sets
 */
public abstract class AbstractDataSetResource<T extends DataSet> {

	private static final Logger LOGGER = LoggerFactory.getLogger( AbstractDataSetResource.class );

	/**
	 * URL parameter key for view type
	 */
	public final static String PARAM_VIEW = "view";

	/**
	 * URL parameter key for format type
	 */
	public final static String PARAM_FORMAT = "format";

	/**
	 * URL parameter key for version
	 */
	public final static String PARAM_VERSION = "version";

	/**
	 * URL parameter value for HTML format type
	 */
	public final static String FORMAT_HTML = "html";

	/**
	 * URL parameter value for XML format type
	 */
	public final static String FORMAT_XML = "xml";

	/**
	 * URL parameter value for overview view type
	 */
	public final static String VIEW_OVERVIEW = "overview";

	/**
	 * URL parameter value for full view type
	 */
	public final static String VIEW_FULL = "full";

	/**
	 * URL parameter value for meta data view type
	 */
	public final static String VIEW_METADATA = "metadata";

	/**
	 * Context, required for the velocity rendering
	 */
	@Context
	private UriInfo context;

	/**
	 * Headers, currently unused
	 */
	@SuppressWarnings( "unused" )
	@Context
	private HttpHeaders headers;

	/**
	 * The request, required for request parameter evaluation
	 */
	@Context
	private HttpServletRequest request;

	/**
	 * The type in API definition, required for {@link #generateFullDataSetAsHtml(DataSet)}
	 */
	private final ILCDTypes apiType;

	/**
	 * The type in model definition, required for
	 */
	private final DataSetType modelType;

	/**
	 * Create an abstract data set resource
	 * 
	 * @param dao
	 *            the data access object to work with
	 */
	public AbstractDataSetResource( DataSetType modelType, ILCDTypes apiType ) {
		this.apiType = apiType;
		this.modelType = modelType;
	}

	/**
	 * Create a fresh data access object to work with
	 * 
	 * @return fresh data access object
	 */
	protected abstract DataSetDao<T, ?, ?> getFreshDaoInstance();

	/**
	 * Get the path to the template for the XML data set list view This is typically something like
	 * <code>/xml/<i>$datasettype$</i>s.vm</code>
	 * 
	 * @deprecated
	 * @return path to the template for the XML data set list view
	 */
	@Deprecated
	protected abstract String getXMLListTemplatePath();

	/**
	 * Get the path to the template for the XML single data set view This is typically something like
	 * <code>/xml/<i>$datasettype$</i>.vm</code>
	 * 
	 * @deprecated
	 * @return path to the template for the XML single data set view
	 */
	@Deprecated
	protected abstract String getXMLTemplatePath();

	/**
	 * Get the path to the template for the HTML full view page This is typically something like
	 * <code>/html/<i>$datasettype$</i>.vm</code>
	 * 
	 * @return path to the template for the HTML full view page
	 */
	protected abstract String getHTMLFullViewTemplatePath();

	/**
	 * Get the path to the template for the HTML overview page This is typically something like
	 * <code>/html/<i>$datasettype$</i>_overview.vm</code>
	 * 
	 * @return path to the template for the HTML overview page
	 */
	protected abstract String getHTMLOverviewTemplatePath();

	/**
	 * Get the name of the data set type for the creation of error/info messages
	 * 
	 * @return name of the data set type for the creation of error/info messages
	 */
	protected abstract String getDataSetTypeName();

	/**
	 * Flag to set, if full view rights shall be checked in user bean
	 * 
	 * @return <code>true</code> if full view rights shall be checked, else <code>false</code>
	 */
	protected abstract boolean userRequiresFullViewRights();

	/**
	 * Retrieves representation of an instance of de.iai.ilcd.services.ContactResource
	 * 
	 * @return an instance of javax.ws.rs.core.StreamingOutput
	 */
	@GET
	@Produces( "application/xml" )
	public StreamingOutput getDataSets( @DefaultValue( "false" ) @QueryParam( "search" ) final boolean search,
			@DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex, @DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {

		List<? extends IDataSetListVO> dataSets;
		int count;

		DataSetDao<T, ?, ?> daoObject = this.getFreshDaoInstance();

		if ( search ) {
			ParameterTool params = new ParameterTool( this.request );

			AvailableStockHandler availableStocksHandler = new AvailableStockHandler();
			availableStocksHandler.setDirty( new DirtyFlagBean() );

			IDataStockMetaData[] availableStocks = availableStocksHandler.getAllStocksMeta().toArray( new IDataStockMetaData[0] );

			count = (int) daoObject.searchResultCount( params, true, availableStocks );
			dataSets = daoObject.search( params, startIndex, pageSize, true, availableStocks );
		}
		else {
			count = daoObject.getAllCount().intValue();
			dataSets = daoObject.getDataSets( startIndex, pageSize );
		}

		return this.getStreamingOutputForDatasetListVOList( dataSets, count, startIndex, pageSize );
	}

	/**
	 * Create a streaming output for a list of data sets
	 * 
	 * @param list
	 *            the list with the data
	 * @param totalCount
	 *            the total count of result items (for pagination)
	 * @param startIndex
	 *            index of the first item in the passed list in the total data (for pagination)
	 * @param pageSize
	 *            pagination page size
	 * @return output for the client (which will be marshaled via JAXB)
	 */
	protected StreamingOutput getStreamingOutputForDatasetListVOList( final List<? extends IDataSetListVO> list, final int totalCount, final int startIndex,
			final int pageSize ) {
		return new StreamingOutput() {

			@Override
			public void write( OutputStream out ) throws IOException, WebApplicationException {
				DataSetList resultList = new DataSetListAdapter( list );

				String baseUrl = ConfigurationService.INSTANCE.getNodeInfo().getBaseURL();

				// set xlink:href attribute for each dataset
				for ( DataSetVO ds : resultList.getDataSet() ) {
					StringBuffer buf = new StringBuffer( baseUrl );
					buf.append( AbstractDataSetResource.this.getURLSuffix( ds ) );
					buf.append( "/" );
					buf.append( ds.getUuidAsString() );
					ds.setHref( buf.toString() );
				}

				resultList.setTotalSize( totalCount );
				resultList.setPageSize( pageSize );
				resultList.setStartIndex( startIndex );

				DatasetVODAO dao = new DatasetVODAO();

				try {
					dao.marshal( resultList, out );
				}
				catch ( JAXBException e ) {
					if ( e.getCause().getCause() instanceof SocketException ) {
						LOGGER.warn( "exception occurred during marshalling - " + e );
					}
					else {
						LOGGER.error( "error marshalling data", e );
					}
				}
			}
		};
	}

	/**
	 * Get a data set by UUID string
	 * 
	 * @param uuid
	 *            UUID string
	 * @return XML or HTML response for client
	 */
	@GET
	@Path( "{uuid}" )
	@Produces( "application/xml, text/html" )
	public Response getDataSet( @PathParam( "uuid" ) String uuid ) {

		try {

			MultivaluedMap<String, String> queryParams = this.context.getQueryParameters( true );
			String view = queryParams.getFirst( AbstractDataSetResource.PARAM_VIEW );
			if ( view == null ) {
				view = AbstractDataSetResource.VIEW_FULL;
			}
			String format = queryParams.getFirst( AbstractDataSetResource.PARAM_FORMAT );
			if ( format == null ) {
				format = AbstractDataSetResource.FORMAT_HTML;
			}

			DataSetVersion version = null;
			String versionStr = queryParams.getFirst( AbstractDataSetResource.PARAM_VERSION );
			if ( versionStr != null ) {
				try {
					version = DataSetVersion.parse( versionStr );
				}
				catch ( FormatException e ) {
					// nothing
				}
			}

			// fix uuid, if not in the right format
			GlobalRefUriAnalyzer analyzer = new GlobalRefUriAnalyzer( uuid );
			uuid = analyzer.getUuidAsString();

			DataSetDao<T, ?, ?> daoObject = this.getFreshDaoInstance();

			T dataset;
			if ( version != null ) {
				dataset = daoObject.getByUuidAndVersion( uuid, version );
			}
			else {
				dataset = daoObject.getByUuid( uuid );
			}
			if ( dataset == null ) {
				String errorTitle = this.getDataSetTypeName() + " data set not found";
				String errorMessage = "A " + this.getDataSetTypeName() + " data set with the uuid " + uuid + " cannot be found in the database";
				if ( format.equals( AbstractDataSetResource.FORMAT_HTML ) ) {
					return Response.status( Response.Status.NOT_FOUND ).entity( VelocityUtil.errorPage( this.context, errorTitle, errorMessage ) ).type(
							"text/html" ).build();
				}
				else {
					return Response.status( Response.Status.NOT_FOUND ).entity( errorMessage ).type( "text/plain" ).build();
				}
			}

			// handle full and metadata mode by returning the XML file itself
			if ( view.equals( AbstractDataSetResource.VIEW_FULL ) || view.equals( AbstractDataSetResource.VIEW_METADATA ) ) {
				if ( format.equals( "html" ) ) {
					SecurityUtil.assertCanRead( dataset );
					return Response.ok( this.generateFullDataSetAsHtml( dataset ), MediaType.TEXT_HTML_TYPE ).build();
				}
				// return the xml file itself
				else {
					SecurityUtil.assertCanExport( dataset );
					ContentDisposition cd = ContentDisposition.type( "attachment" ).fileName( uuid + ".xml" ).build();
					return Response.ok( dataset.getXmlFile().getContent(), MediaType.APPLICATION_XML ).header( "Content-Disposition", cd ).build();
					// replaceFirst("<\\?xml-stylesheet.*\\?>", "")
				}
			}

			SecurityUtil.assertCanRead( dataset );
			// in all other cases we return the overview view as XML file
			return Response.ok( this.generateOverview( dataset, AbstractDataSetResource.FORMAT_XML ), MediaType.APPLICATION_XML ).build();
		}
		catch ( AuthorizationException e ) {
			return Response.status( Response.Status.UNAUTHORIZED ).entity( e.getMessage() ).type( "text/plain" ).build();
		}
	}

	protected String getURLSuffix( DataSetVO vo ) {
		if ( vo instanceof IProcessListVO ) {
			return DatasetTypes.PROCESSES.getValue();
		}
		else if ( vo instanceof IFlowListVO ) {
			return DatasetTypes.FLOWS.getValue();
		}
		else if ( vo instanceof IFlowPropertyListVO ) {
			return DatasetTypes.FLOWPROPERTIES.getValue();
		}
		else if ( vo instanceof IUnitGroupListVO ) {
			return DatasetTypes.UNITGROUPS.getValue();
		}
		else if ( vo instanceof ILCIAMethodListVO ) {
			return DatasetTypes.LCIAMETHODS.getValue();
		}
		else if ( vo instanceof ISourceListVO ) {
			return DatasetTypes.SOURCES.getValue();
		}
		else if ( vo instanceof IContactListVO ) {
			return DatasetTypes.CONTACTS.getValue();
		}
		else {
			return null;
		}
	}

	/**
	 * Generate overview. currently called statically with <code>type == xml</code>.
	 * 
	 * @param dataset
	 *            data set to generate overview for
	 * @param type
	 *            type of data set
	 * @return overview source to return to client
	 */
	private String generateOverview( T dataset, String type ) {

		VelocityContext velocityContext = VelocityUtil.getServicesContext( this.context );
		velocityContext.put( "dataset", dataset );

		String template = "";
		if ( type.equals( AbstractDataSetResource.FORMAT_XML ) ) {
			template = this.getXMLTemplatePath();
		}
		else {
			template = this.getHTMLOverviewTemplatePath();
		}

		return VelocityUtil.parseTemplate( template, velocityContext );
	}

	/**
	 * Generate fill data set as HTML view
	 * 
	 * @param dataset
	 *            data set to generate HTML view for
	 * @return HTML code to return to client
	 */
	private String generateFullDataSetAsHtml( T dataset ) {
		VelocityContext velocityContext = VelocityUtil.getServicesContext( this.context );
		velocityContext.put( "dataset", dataset );

		de.fzk.iai.ilcd.api.dataset.DataSet xmlDataset = null;
		try {
			DatasetHelper helper = new DatasetHelper();
			String xmlFile = dataset.getXmlFile().getContent();
			InputStream bais = new ByteArrayInputStream( xmlFile.getBytes( "UTF-8" ) );
			xmlDataset = helper.unmarshal( bais, this.apiType );
		}
		catch ( JAXBException ex ) {
			AbstractDataSetResource.LOGGER.error( "cannot unmarshal xml information from " + this.getDataSetTypeName() );
			AbstractDataSetResource.LOGGER.error( "stack trace is: ", ex );
		}
		catch ( UnsupportedEncodingException ex ) {
			AbstractDataSetResource.LOGGER.error( "cannot unmarshal xml information from " + this.getDataSetTypeName() );
			AbstractDataSetResource.LOGGER.error( "stack trace is: ", ex );
		}
		velocityContext.put( "xmlDataset", xmlDataset );

		return VelocityUtil.parseTemplate( this.getHTMLFullViewTemplatePath(), velocityContext );
	}

	/**
	 * PUT method for updating or creating an instance of ContactResource
	 * 
	 * @param content
	 *            representation for the resource
	 * @return an HTTP response with content of the updated or created resource.
	 */
	@PUT
	@Consumes( "application/xml" )
	public void putXml( String content ) {
	}

	/**
	 * POST method for importing new process data set sent by form
	 * 
	 * @param fileInputStream
	 *            XML data to import (from form)
	 * @param stockUuid
	 *            UUID of the root stock to import to (or <code>null</code> for default)
	 * @return response for client
	 */
	@POST
	@Produces( "text/plain" )
	@Consumes( "multipart/form-data" )
	public Response importByFileUpload( @FormDataParam( "file" ) InputStream fileInputStream, @FormDataParam( "stock" ) String stockUuid ) {
		return this.importXml( fileInputStream, stockUuid );
	}

	/**
	 * POST method for importing new process data set sent directly
	 * 
	 * @param fileInputStream
	 *            XML data to import
	 * @param stockUuid
	 *            UUID of the root stock to import to (or <code>null</code> for default
	 * @return response for client
	 */
	@POST
	@Produces( "text/plain" )
	@Consumes( "application/xml" )
	public Response importByXml( InputStream fileInputStream, @HeaderParam( "stock" ) String stockUuid ) {
		return this.importXml( fileInputStream, stockUuid );
	}

	/**
	 * Process uploaded XML as input stream
	 * 
	 * @param inputStream
	 *            stream with XML
	 * @param stockUuid
	 *            UUID of the root stock to import to (or <code>null</code> for default
	 * @return response for client
	 */
	private Response importXml( InputStream inputStream, String stockUuid ) {
		try {
			final CommonDataStockDao dao = new CommonDataStockDao();
			AbstractDataStock ads;
			if ( StringUtils.isBlank( stockUuid ) ) {
				ads = dao.getById( IDataStockMetaData.DEFAULT_DATASTOCK_ID );
			}
			else {
				ads = dao.getDataStockByUuid( stockUuid );
				if ( !(ads instanceof RootDataStock) ) {
					return Response.status( Response.Status.PRECONDITION_FAILED ).entity( "Data sets can only be imported into root data stocks!" ).type(
							"text/plain" ).build();
				}
			}
			if ( ads == null ) {
				return Response.status( Response.Status.BAD_REQUEST ).entity( "Invalid root data stock UUID specified" ).type( "text/plain" ).build();
			}
			SecurityUtil.assertCanImport( ads );

			PostResourceHelper postHelper = new PostResourceHelper();
			final Response resp = postHelper.importByFileUpload( this.modelType, inputStream, (RootDataStock) ads );
			if ( resp.getStatus() != Status.OK.getStatusCode() ) {
				return resp;
			}
			else {
				final DataStockVO dsVo = DataStockListAdapter.getServiceApiVo( ads );
				final StreamingOutput sout = new StreamingOutput() {

					@Override
					public void write( OutputStream out ) throws IOException, WebApplicationException {
						ServiceDAO sDao = new ServiceDAO();
						try {
							sDao.marshal( dsVo, out );
						}
						catch ( JAXBException e ) {
							if ( e.getCause().getCause() instanceof SocketException ) {
								AbstractDataSetResource.LOGGER.warn( "exception occurred during marshalling - " + e );
							}
							else {
								AbstractDataSetResource.LOGGER.error( "error marshalling data", e );
							}
						}
					}
				};
				return Response.ok( sout ).type( "application/xml" ).build();
			}

		}
		catch ( IllegalArgumentException e ) {
			return Response.status( Response.Status.BAD_REQUEST ).entity( e.getMessage() ).type( "text/plain" ).build();
		}
		catch ( AuthorizationException e ) {
			return Response.status( Response.Status.FORBIDDEN ).entity( e.getMessage() ).type( "text/plain" ).build();
		}
	}

}
