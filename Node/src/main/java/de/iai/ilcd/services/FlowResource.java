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

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.springframework.stereotype.Component;

import de.fzk.iai.ilcd.api.dataset.ILCDTypes;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.dao.FlowDao;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.process.ExchangeDirection;
import de.iai.ilcd.model.process.Process;

/**
 * REST Web Service for Flow
 */
@Component
@Path( "flows" )
public class FlowResource extends AbstractDataSetResource<Flow> {

	public FlowResource() {
		super( DataSetType.FLOW, ILCDTypes.FLOW );
	}

	/**
	 * Get availability of producers for specified flow (processes with this flow as {@link ExchangeDirection#OUTPUT
	 * output} flow)
	 * 
	 * @param uuid
	 *            UUID of the flow
	 * @return empty {@link Response} with status {@link Status#OK OK} if producers for specified UUID are available,
	 *         else with status {@link Status#NO_CONTENT NO_CONTENT}
	 */
	@HEAD
	@Path( "{uuid}/producers" )
	@Produces( "text/plain" )
	public Response getAvailabilityOfProducers( @PathParam( "uuid" ) String uuid ) {
		return this.getAvailabilityOfInOutputProcesses( uuid, ExchangeDirection.OUTPUT );
	}

	/**
	 * Get availability of consumers for specified flow (processes with this flow as {@link ExchangeDirection#INPUT
	 * input} flow)
	 * 
	 * @param uuid
	 *            UUID of the flow
	 * @return empty {@link Response} with status {@link Status#OK OK} if consumers for specified UUID are available,
	 *         else with status {@link Status#NO_CONTENT NO_CONTENT}
	 */
	@HEAD
	@Path( "{uuid}/consumers" )
	@Produces( "text/plain" )
	public Response getAvailabilityOfConsumers( @PathParam( "uuid" ) String uuid ) {
		return this.getAvailabilityOfInOutputProcesses( uuid, ExchangeDirection.INPUT );
	}

	/**
	 * Get HEAD response for input (consumer) or output (producer) request
	 * 
	 * @param flowUuid
	 *            UUID of the flow in question
	 * @param direction
	 *            the direction of the exchange
	 * @return empty {@link Response} with status {@link Status#OK OK} if processes for specified UUID and direction are
	 *         available, else with status {@link Status#NO_CONTENT NO_CONTENT}
	 * @see #getAvailabilityOfProducers(String)
	 * @see #getAvailabilityOfConsumers(String)
	 */
	private Response getAvailabilityOfInOutputProcesses( String flowUuid, ExchangeDirection direction ) {
		ProcessDao dao = new ProcessDao();
		Response.Status status;
		if ( dao.getProcessesForExchangeFlowCount( flowUuid, direction ) > 0 ) {
			status = Response.Status.OK;
		}
		else {
			status = Response.Status.NO_CONTENT;
		}
		return Response.status( status ).type( "text/plain" ).build();
	}

	/**
	 * Get availability of producers for specified flow (processes with this flow as {@link ExchangeDirection#OUTPUT
	 * output} flow)
	 * 
	 * @param uuid
	 *            UUID of the flow
	 * @return empty {@link Response} with status {@link Status#OK OK} if producers for specified UUID are available,
	 *         else with status {@link Status#NO_CONTENT NO_CONTENT}
	 */
	@GET
	@Path( "{uuid}/producers" )
	@Produces( "application/xml" )
	public StreamingOutput getProducers( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {

		return this.getInOutputProcesses( uuid, ExchangeDirection.OUTPUT, startIndex, pageSize );
	}

	/**
	 * Get availability of consumers for specified flow (processes with this flow as {@link ExchangeDirection#INPUT
	 * input} flow)
	 * 
	 * @param uuid
	 *            UUID of the flow
	 * @return empty {@link Response} with status {@link Status#OK OK} if consumers for specified UUID are available,
	 *         else with status {@link Status#NO_CONTENT NO_CONTENT}
	 */
	@GET
	@Path( "{uuid}/consumers" )
	@Produces( "application/xml" )
	public StreamingOutput getConsumers( @PathParam( "uuid" ) String uuid, @DefaultValue( "0" ) @QueryParam( "startIndex" ) final int startIndex,
			@DefaultValue( "500" ) @QueryParam( "pageSize" ) final int pageSize ) {

		return this.getInOutputProcesses( uuid, ExchangeDirection.INPUT, startIndex, pageSize );
	}

	/**
	 * Get GET response for input (consumer) or output (producer) request
	 * 
	 * @param flowUuid
	 *            UUID of the flow
	 * @param direction
	 *            the direction of the exchange
	 * @param startIndex
	 *            index of the first item (for pagination)
	 * @param pageSize
	 *            pagination page size
	 * @return created output
	 * @see #getStreamingOutputForDatasetListVOList(List, int, int, int)
	 */
	private StreamingOutput getInOutputProcesses( String flowUuid, ExchangeDirection direction, int startIndex, int pageSize ) {
		ProcessDao dao = new ProcessDao();
		long count = dao.getProcessesForExchangeFlowCount( flowUuid, direction );
		List<Process> lst = dao.getProcessesForExchangeFlow( flowUuid, direction, startIndex, pageSize );
		return super.getStreamingOutputForDatasetListVOList( lst, (int) count, startIndex, pageSize );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetDao<Flow, ?, ?> getFreshDaoInstance() {
		return new FlowDao();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getXMLListTemplatePath() {
		return "/xml/flows.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getXMLTemplatePath() {
		return "/xml/flow.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHTMLOverviewTemplatePath() {
		return "/html/flow_overview.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDataSetTypeName() {
		return "flow";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHTMLFullViewTemplatePath() {
		return "/html/flow.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean userRequiresFullViewRights() {
		return false;
	}

}
