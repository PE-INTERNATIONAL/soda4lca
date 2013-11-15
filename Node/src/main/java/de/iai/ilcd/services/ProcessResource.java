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

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.fzk.iai.ilcd.api.dataset.ILCDTypes;
import de.fzk.iai.ilcd.service.client.impl.DatasetTypes;
import de.fzk.iai.ilcd.service.client.impl.vo.DatasetVODAO;
import de.fzk.iai.ilcd.service.client.impl.vo.dataset.DataSetList;
import de.fzk.iai.ilcd.service.model.IDataSetListVO;
import de.fzk.iai.ilcd.service.model.enums.TypeOfFlowValue;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.adapter.DataSetListAdapter;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.dao.ExchangeDao;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.process.Exchange;
import de.iai.ilcd.model.process.ExchangeDirection;
import de.iai.ilcd.model.process.Process;

/**
 * REST web service for Processes
 */
@Component
@Path( "processes" )
public class ProcessResource extends AbstractDataSetResource<Process> {

	private static final Logger LOGGER = LoggerFactory.getLogger( ProcessResource.class );

	public ProcessResource() {
		super( DataSetType.PROCESS, ILCDTypes.PROCESS );
	}

	/**
	 * Get exchanges for process
	 * 
	 * @param uuid
	 *            the UUID of the process
	 * @param direction
	 *            the direction, mapping is: &quot;in&quot; &rArr; {@link ExchangeDirection#INPUT}, &quot;out&quot;
	 *            &rArr; {@link ExchangeDirection#OUTPUT}. <code>null</code> is permitted (both directions matched)
	 * @param type
	 *            the type of the flow, mapped via {@link TypeOfFlowValue#valueOf(String)}. <code>null</code> is
	 *            permitted (all types matched)
	 * @return the list of the matched exchanges
	 */
	@GET
	@Path( "{uuid}/exchanges" )
	@Produces( "application/xml" )
	public StreamingOutput getExchanges( @PathParam( "uuid" ) String uuid, @QueryParam( "direction" ) final String direction,
			@QueryParam( "type" ) final String type ) {

		ExchangeDirection eDir = null;
		if ( "in".equals( direction ) ) {
			eDir = ExchangeDirection.INPUT;
		}
		else if ( "out".equals( direction ) ) {
			eDir = ExchangeDirection.OUTPUT;
		}

		TypeOfFlowValue fType = null;
		try {
			if ( type != null ) {
				fType = TypeOfFlowValue.valueOf( type );
			}
		}
		catch ( Exception e ) {
			// Nothing to do, null is already set before try
		}

		ExchangeDao eDao = new ExchangeDao();
		List<Exchange> exchanges = eDao.getExchangesForProcess( uuid, null, eDir, fType );
		List<IDataSetListVO> dataSets = new ArrayList<IDataSetListVO>();
		final String baseFlowUrl = ConfigurationService.INSTANCE.getNodeInfo().getBaseURL() + DatasetTypes.FLOWS.getValue() + "/";
		for ( Exchange e : exchanges ) {
			Flow f = e.getFlow();
			if ( f == null ) {
				final GlobalReference ref = e.getFlowReference();
				f = new Flow();
				f.setName( ref.getShortDescription() );
				f.setHref( ref.getHref() );
			}
			else {
				f.setHref( baseFlowUrl + f.getUuidAsString() );
			}
			dataSets.add( f );
		}

		final DataSetList dsList = new DataSetListAdapter( dataSets );

		dsList.setTotalSize( dataSets.size() );
		dsList.setPageSize( dataSets.size() );
		dsList.setStartIndex( 0 );

		return new StreamingOutput() {

			@Override
			public void write( OutputStream out ) throws IOException, WebApplicationException {
				DatasetVODAO dao = new DatasetVODAO();
				try {
					dao.marshal( dsList, out );
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
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetDao<Process, ?, ?> getFreshDaoInstance() {
		return new ProcessDao();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getXMLListTemplatePath() {
		return "/xml/processes.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getXMLTemplatePath() {
		return "/xml/process.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHTMLFullViewTemplatePath() {
		return "/html/process.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHTMLOverviewTemplatePath() {
		return "/html/process_overview.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDataSetTypeName() {
		return "process.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean userRequiresFullViewRights() {
		return true;
	}

}
