package de.iai.ilcd.services;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.NodeChangeLog;

@Component
@Path( "/synchronization" )
public class NodeSynchronizationService {

	private static final Logger LOGGER = LoggerFactory.getLogger( NodeSynchronizationService.class );

	@Autowired
	private RegistryService registryService;

	@Autowired
	private DataSetDeregistrationService dataSetDeregistrationService;

	/**
	 * Informs node about changes in registry
	 * 
	 * @param registryId
	 * @param changeLog
	 * @return
	 */
	@PUT
	@Path( "/inform/{identity}" )
	@Produces( MediaType.APPLICATION_XML )
	public Response synchronizeNode( @PathParam( "identity" ) String registryId, NodeChangeLog changeLog ) {
		LOGGER.info( "=============================Node synchronization signal received" );
		try {
			registryService.applyChange( registryId, changeLog );
		}
		catch ( RestWSUnknownException e ) {
			throw new WebApplicationException( e, Status.BAD_REQUEST );
		}
		return Response.ok().build();
	}

	/**
	 * Informs node about changes in registry
	 * 
	 * @param registryId
	 * @param changedDS
	 * @return
	 */
	@PUT
	@Path( "/informAboutDSDeregistration/{identity}" )
	@Produces( MediaType.APPLICATION_XML )
	public Response synchronizeDS( @PathParam( "identity" ) String registryId, DataSet changedDS ) {
		LOGGER.info( "=============================DS synchronization signal received" );
		try {
			dataSetDeregistrationService.applyDeregistration( registryId, changedDS );
		}
		catch ( RestWSUnknownException e ) {
			throw new WebApplicationException( e, Status.BAD_REQUEST );
		}
		return Response.ok().build();
	}

}
