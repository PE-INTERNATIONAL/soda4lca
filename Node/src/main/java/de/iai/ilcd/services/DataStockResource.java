/*******************************************************************************
 * Copyright (c) 2012 Karlsruhe Institute of Technology (KIT) - Institute for
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

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;

import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.fzk.iai.ilcd.service.client.impl.DatasetTypes;
import de.fzk.iai.ilcd.service.client.impl.ServiceDAO;
import de.fzk.iai.ilcd.service.client.impl.vo.DatasetVODAO;
import de.fzk.iai.ilcd.service.client.impl.vo.dataset.DataSetList;
import de.fzk.iai.ilcd.service.client.impl.vo.dataset.DataSetVO;
import de.fzk.iai.ilcd.service.model.IDataSetListVO;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.adapter.DataSetListAdapter;
import de.iai.ilcd.model.adapter.DataStockListAdapter;
import de.iai.ilcd.model.dao.CommonDataStockDao;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.dao.FlowDao;
import de.iai.ilcd.model.dao.FlowPropertyDao;
import de.iai.ilcd.model.dao.LCIAMethodDao;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.dao.SourceDao;
import de.iai.ilcd.model.dao.UnitGroupDao;
import de.iai.ilcd.model.datastock.AbstractDataStock;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.security.SecurityUtil;
import de.iai.ilcd.security.UserAccessBean;

/**
 * REST web service for data stocks
 */
@Component
@Path( "datastocks" )
public class DataStockResource {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger( DataStockResource.class );

	/**
	 * Get list of data stocks
	 * 
	 * @return an instance of javax.ws.rs.core.StreamingOutput
	 */
	@GET
	@Produces( "application/xml" )
	public StreamingOutput getDataStocks() {
		UserAccessBean user = new UserAccessBean();
		CommonDataStockDao stockDao = new CommonDataStockDao();
		List<AbstractDataStock> stocks = stockDao.getAllReadable( user.getUserObject() );

		final DataStockListAdapter adapter = new DataStockListAdapter( stocks );
		return new StreamingOutput() {

			@Override
			public void write( OutputStream out ) throws IOException, WebApplicationException {
				ServiceDAO dao = new ServiceDAO();
				dao.setRenderSchemaLocation( true );
				try {
					dao.marshal( adapter, out );
				}
				catch ( JAXBException e ) {
					if ( e.getCause().getCause() instanceof SocketException ) {
						DataStockResource.LOGGER.warn( "exception occurred during marshalling - " + e );
					}
					else {
						DataStockResource.LOGGER.error( "error marshalling data", e );
					}
				}
			}
		};

	}

	/**
	 * Get the processes of a specified data stock
	 * 
	 * @param uuid
	 *            UUID of the data stock
	 * @param startIndex
	 *            start index of the list
	 * @param pageSize
	 *            page size of the list
	 * @return created response
	 */
	@GET
	@Path( "{uuid}/processes" )
	@Produces( "application/xml, text/plain" )
	public Response getProcesses( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {
		return this.getDataSets( uuid, new ProcessDao(), DatasetTypes.PROCESSES, startIndex, pageSize );
	}

	/**
	 * Get the flows of a specified data stock
	 * 
	 * @param uuid
	 *            UUID of the data stock
	 * @param startIndex
	 *            start index of the list
	 * @param pageSize
	 *            page size of the list
	 * @return created response
	 */
	@GET
	@Path( "{uuid}/flows" )
	@Produces( "application/xml, text/plain" )
	public Response getFlows( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {
		return this.getDataSets( uuid, new FlowDao(), DatasetTypes.FLOWS, startIndex, pageSize );
	}

	/**
	 * Get the flow properties of a specified data stock
	 * 
	 * @param uuid
	 *            UUID of the data stock
	 * @param startIndex
	 *            start index of the list
	 * @param pageSize
	 *            page size of the list
	 * @return created response
	 */
	@GET
	@Path( "{uuid}/flowproperties" )
	@Produces( "application/xml, text/plain" )
	public Response getFlowProperties( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {
		return this.getDataSets( uuid, new FlowPropertyDao(), DatasetTypes.FLOWPROPERTIES, startIndex, pageSize );
	}

	/**
	 * Get the unit groups of a specified data stock
	 * 
	 * @param uuid
	 *            UUID of the data stock
	 * @param startIndex
	 *            start index of the list
	 * @param pageSize
	 *            page size of the list
	 * @return created response
	 */
	@GET
	@Path( "{uuid}/unitgroups" )
	@Produces( "application/xml, text/plain" )
	public Response getUnitGroups( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {
		return this.getDataSets( uuid, new UnitGroupDao(), DatasetTypes.UNITGROUPS, startIndex, pageSize );
	}

	/**
	 * Get the sources of a specified data stock
	 * 
	 * @param uuid
	 *            UUID of the data stock
	 * @param startIndex
	 *            start index of the list
	 * @param pageSize
	 *            page size of the list
	 * @return created response
	 */
	@GET
	@Path( "{uuid}/sources" )
	@Produces( "application/xml, text/plain" )
	public Response getSources( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {
		return this.getDataSets( uuid, new SourceDao(), DatasetTypes.SOURCES, startIndex, pageSize );
	}

	/**
	 * Get the contacts of a specified data stock
	 * 
	 * @param uuid
	 *            UUID of the data stock
	 * @param startIndex
	 *            start index of the list
	 * @param pageSize
	 *            page size of the list
	 * @return created response
	 */
	@GET
	@Path( "{uuid}/contacts" )
	@Produces( "application/xml, text/plain" )
	public Response getContacts( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {
		return this.getDataSets( uuid, new SourceDao(), DatasetTypes.CONTACTS, startIndex, pageSize );
	}

	/**
	 * Get the LCIA methods of a specified data stock
	 * 
	 * @param uuid
	 *            UUID of the data stock
	 * @param startIndex
	 *            start index of the list
	 * @param pageSize
	 *            page size of the list
	 * @return created response
	 */
	@GET
	@Path( "{uuid}/lciamethods" )
	@Produces( "application/xml, text/plain" )
	public Response getLCIAMethods( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {
		return this.getDataSets( uuid, new LCIAMethodDao(), DatasetTypes.LCIAMETHODS, startIndex, pageSize );
	}

	/**
	 * Get data sets of a specified data stock
	 * 
	 * @param uuid
	 *            UUID of the data stock
	 * @param dao
	 *            the data access object to use for retrieving of the data sets
	 * @param dsType
	 *            the API data set type
	 * @param startIndex
	 *            start index of the list
	 * @param pageSize
	 *            page size of the list
	 * @return created response
	 */
	private <E extends IDataSetListVO> Response getDataSets( final String uuid, final DataSetDao<?, E, ?> dao, final DatasetTypes dsType, final int startIndex,
			final int pageSize ) {
		CommonDataStockDao dsDao = new CommonDataStockDao();
		IDataStockMetaData dsMeta = dsDao.getDataStockByUuid( uuid );

		if ( dsMeta == null ) {
			return Response.status( Status.NOT_FOUND ).entity( "Invalid data stock UUID" ).type( "text/plain" ).build();
		}

		try {
			SecurityUtil.assertCanRead( dsMeta );

			IDataStockMetaData[] dsMetas = new IDataStockMetaData[] { dsMeta };

			final int totalCount = (int) dao.searchResultCount( null, true, dsMetas );
			final List<E> list = dao.search( null, true, dsMetas );

			final StreamingOutput sout = new StreamingOutput() {

				@Override
				public void write( OutputStream out ) throws IOException, WebApplicationException {
					DataSetList resultList = new DataSetListAdapter( list );

					String baseUrl = ConfigurationService.INSTANCE.getNodeInfo().getBaseURL();

					// set xlink:href attribute for each dataset
					for ( DataSetVO ds : resultList.getDataSet() ) {
						StringBuffer buf = new StringBuffer( baseUrl );
						buf.append( dsType.getValue() );
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
							DataStockResource.LOGGER.warn( "exception occurred during marshalling - " + e );
						}
						else {
							DataStockResource.LOGGER.error( "error marshalling data", e );
						}
					}
				}
			};
			return Response.ok( sout ).type( "application/xml" ).build();
		}
		catch ( AuthorizationException aEx ) {
			return Response.status( Status.UNAUTHORIZED ).entity( aEx.getMessage() ).type( "text/plain" ).build();
		}
	}

}
