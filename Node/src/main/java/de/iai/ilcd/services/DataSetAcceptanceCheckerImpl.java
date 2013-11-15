package de.iai.ilcd.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.iai.ilcd.delegate.DataSetRestServiceBD;
import de.iai.ilcd.delegate.NodeRestServiceBD;
import de.iai.ilcd.model.dao.DataSetRegistrationDataDao;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import de.iai.ilcd.model.registry.DataSetRegistrationDataStatus;
import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationAcceptanceDecision;
import eu.europa.ec.jrc.lca.commons.security.SecurityContext;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;

@Component( "dataSetAcceptanceChecker" )
public class DataSetAcceptanceCheckerImpl implements DataSetAcceptanceChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger( DataSetAcceptanceCheckerImpl.class );

	@Autowired
	private RegistryService registryService;

	@Autowired
	private DataSetRegistrationDataDao dataSetRegistrationDataDao;

	@Autowired
	private SecurityContext securityContext;

	/*
	 * scheduled in spring-context.xml
	 */
	public void checkAcceptance() {
		LOGGER.info( "checking dataset acceptance" );
		List<Registry> regs = registryService.getNonVirtualRegistriesInWhichRegistered();
		for ( Registry reg : regs ) {
			checkAcceptanceInRegistryScope( reg );
		}
	}

	private void checkAcceptanceInRegistryScope( Registry reg ) {
		List<DataSetRegistrationData> notAccepted = dataSetRegistrationDataDao.getRegisteredNotAccepted( reg.getId() );
		if ( notAccepted.isEmpty() ) {
			return;
		}
		try {
			reg.getNodeCredentials().decrypt( securityContext.getPrivateKey() );
			NodeCredentials credentials = reg.getNodeCredentials().getEncryptedCopyWithNonce( NodeRestServiceBD.getInstance( reg ).getPublicKey() );
			List<DataSetRegistrationAcceptanceDecision> status = DataSetRestServiceBD.getInstance( reg ).checkStatus( getDsDTOs( notAccepted ), credentials );
			updateRegistrationData( status, notAccepted );
		}
		catch ( RestWSUnknownException e ) {
			LOGGER.error( "[checkAcceptanceInRegistryScope]", e );
		}
		catch ( AuthenticationException e ) {
			LOGGER.error( "[checkAcceptanceInRegistryScope]", e );
		}
	}

	private List<DataSetDTO> getDsDTOs( List<DataSetRegistrationData> dsRegData ) {
		List<DataSetDTO> dtos = new ArrayList<DataSetDTO>();
		for ( DataSetRegistrationData dsr : dsRegData ) {
			dtos.add( new DataSetDTO( dsr.getUuid(), dsr.getVersion().getVersionString() ) );
		}
		return dtos;
	}

	private void updateRegistrationData( List<DataSetRegistrationAcceptanceDecision> status, List<DataSetRegistrationData> dsRegData ) {
		for ( int i = 0; i < dsRegData.size(); i++ ) {
			if ( status.get( i ) == DataSetRegistrationAcceptanceDecision.REJECTED ) {
				dataSetRegistrationDataDao.remove( dsRegData.get( i ).getId() );
			}
			else if ( status.get( i ) == DataSetRegistrationAcceptanceDecision.ACCEPTED ) {
				DataSetRegistrationData dsr = dsRegData.get( i );
				dsr.setStatus( DataSetRegistrationDataStatus.ACCEPTED );
				dataSetRegistrationDataDao.saveOrUpdate( dsr );
			}
		}
	}
}
