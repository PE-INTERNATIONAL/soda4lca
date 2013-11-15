package de.iai.ilcd.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.iai.ilcd.model.dao.DataSetRegistrationDataDao;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import de.iai.ilcd.model.registry.DatasetDeregistrationReason;
import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;

@Component( "dataSetDeregistrationPropagator" )
public class DataSetDeregistrationPropagatorImpl implements DataSetDeregistrationPropagator {

	private static final Logger LOGGER = LoggerFactory.getLogger( DataSetDeregistrationPropagatorImpl.class );

	@Autowired
	private RegistryService registryService;

	@Autowired
	private DataSetRegistrationDataDao dataSetRegistrationDataDao;

	@Autowired
	private DataSetDeregistrationService dataSetDeregistrationService;

	/*
	 * scheduled in spring-context.xml
	 */
	public void propagateDeregistration() {
		LOGGER.info( "propagating deregistration" );
		List<Registry> regs = registryService.getNonVirtualRegistriesInWhichRegistered();
		for ( Registry reg : regs ) {
			try {
				propagateDeregistrationInRegistryScope( reg );
			}
			catch ( UnsupportedEncodingException e ) {
				LOGGER.error( "[propagateDeregistration]", e );
			}
			catch ( AuthenticationException e ) {
				LOGGER.error( "[propagateDeregistration]", e );
			}
			catch ( RestWSUnknownException e ) {
				LOGGER.error( "[propagateDeregistration]", e );
			}
		}
	}

	private void propagateDeregistrationInRegistryScope( Registry reg ) throws UnsupportedEncodingException, AuthenticationException, RestWSUnknownException {
		List<DataSetRegistrationData> result = dataSetRegistrationDataDao.getRegisteredPendingDeregistration( reg.getId() );
		if ( result == null || result.isEmpty() ) {
			return;
		}
		Map<DatasetDeregistrationReason, List<DataSetRegistrationData>> groupped = groupByReason( result );
		for ( Entry<DatasetDeregistrationReason, List<DataSetRegistrationData>> en : groupped.entrySet() ) {
			dataSetDeregistrationService.resubmitDeregisterDatasets( en.getValue(), en.getValue().get( 0 ).getReason().getContent(), reg );
		}
	}

	private Map<DatasetDeregistrationReason, List<DataSetRegistrationData>> groupByReason( List<DataSetRegistrationData> regData ) {
		Map<DatasetDeregistrationReason, List<DataSetRegistrationData>> result = new HashMap<DatasetDeregistrationReason, List<DataSetRegistrationData>>();
		for ( DataSetRegistrationData dsr : regData ) {
			if ( !result.containsKey( dsr.getReason() ) ) {
				result.put( dsr.getReason(), new ArrayList<DataSetRegistrationData>() );
			}
			result.get( dsr.getReason() ).add( dsr );
		}
		return result;
	}

}
