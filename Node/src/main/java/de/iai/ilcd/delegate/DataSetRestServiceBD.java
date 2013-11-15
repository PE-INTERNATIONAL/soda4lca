package de.iai.ilcd.delegate;

import java.util.List;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.rest.CustomStatus;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDeregistrationRequest;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDeregistrationResult;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationAcceptanceDecision;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationResult;
import eu.europa.ec.jrc.lca.commons.rest.dto.JaxbBaseList;
import eu.europa.ec.jrc.lca.commons.rest.dto.SecuredRequestWrapper;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

public class DataSetRestServiceBD extends RestServiceBD {

	private static final String REST_PATH = "rest";

	private DataSetRestServiceBD( String serviceBasicUrl ) {
		super( serviceBasicUrl );
	}

	public static DataSetRestServiceBD getInstance( Registry registry ) {
		return new DataSetRestServiceBD( registry.getBaseUrl() + REST_PATH );
	}

	public List<DataSetRegistrationResult> registerDataSets( List<DataSet> datasets, NodeCredentials credentials ) throws NodeIllegalStatusException,
			AuthenticationException, RestWSUnknownException {
		try {
			WebResource wr = getResource( "/datasetservice/register" );
			ClientResponse cr = wr.post( ClientResponse.class, new SecuredRequestWrapper( credentials, new JaxbBaseList( datasets ) ) );
			if ( cr.getClientResponseStatus().equals( Status.BAD_REQUEST ) ) {
				throw new NodeIllegalStatusException();
			}
			else if ( cr.getClientResponseStatus().equals( Status.UNAUTHORIZED ) ) {
				throw new AuthenticationException();
			}
			JaxbBaseList<DataSetRegistrationResult> list = cr.getEntity( new GenericType<JaxbBaseList<DataSetRegistrationResult>>() {
			} );
			return list.getList();
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
	}

	public List<DataSetRegistrationAcceptanceDecision> checkStatus( List<DataSetDTO> dataSets, NodeCredentials credentials ) throws AuthenticationException,
			RestWSUnknownException {
		try {
			WebResource wr = getResource( "/datasetservice/checkStatus" );
			ClientResponse cr = wr.post( ClientResponse.class, new SecuredRequestWrapper( credentials, new JaxbBaseList( dataSets ) ) );
			if ( cr.getClientResponseStatus().equals( Status.UNAUTHORIZED ) ) {
				throw new AuthenticationException();
			}
			JaxbBaseList<DataSetRegistrationAcceptanceDecision> list = cr.getEntity( new GenericType<JaxbBaseList<DataSetRegistrationAcceptanceDecision>>() {
			} );
			return list.getList();
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
	}

	public DataSet getDataSetData( DataSet dataset ) throws RestWSUnknownException {
		try {
			ClientResponse cr = getResponse( "/datasetservice/dataset/" + dataset.getId() );
			if ( cr.getStatus() == CustomStatus.ENTITY_NOT_FOUND.getCode() ) {
				return null;
			}
			return cr.getEntity( DataSet.class );
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
	}

	public List<DataSetDeregistrationResult> deregisterDataSets( List<DataSetDTO> datasets, String reason, NodeCredentials credentials )
			throws AuthenticationException, RestWSUnknownException {
		DataSetDeregistrationRequest dsDeregistrationReq = new DataSetDeregistrationRequest( reason, datasets );
		try {
			WebResource wr = getResource( "/datasetservice/deregister" );
			ClientResponse cr = wr.post( ClientResponse.class, new SecuredRequestWrapper( credentials, dsDeregistrationReq ) );
			if ( cr.getClientResponseStatus().equals( Status.UNAUTHORIZED ) ) {
				throw new AuthenticationException();
			}
			JaxbBaseList<DataSetDeregistrationResult> list = cr.getEntity( new GenericType<JaxbBaseList<DataSetDeregistrationResult>>() {
			} );
			return list.getList();
		}
		catch ( ClientHandlerException ex ) {
			throw new RestWSUnknownException( ex.getMessage() );
		}
	}

	public ValidationResult verify( DataSet dataset ) {
		try {
			WebResource wr = getResource( "/datasetservice/verify" );
			ClientResponse cr = wr.post( ClientResponse.class, dataset );
			if ( cr.getClientResponseStatus().equals( Status.CONFLICT ) ) {
				return ValidationResult.INVALID;
			}
			else if ( cr.getClientResponseStatus().equals( Status.OK ) ) {
				return ValidationResult.VALID;
			}
			return ValidationResult.CANT_VALIDATE;
		}
		catch ( ClientHandlerException ex ) {
			return ValidationResult.CANT_VALIDATE;
		}
	}

}
