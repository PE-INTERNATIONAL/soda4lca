package de.iai.ilcd.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.iai.ilcd.delegate.NodeRestServiceBD;
import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.registry.RegistryStatus;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnexpectedStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;

@Component( "acceptanceChecker" )
public class AcceptanceCheckerImpl implements AcceptanceChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger( AcceptanceCheckerImpl.class );

	@Autowired
	private RegistryService registryService;

	@Autowired
	private NodeRegistrationService nodeRegistrationService;

	@Autowired
	private Synchronizator synchronizator;

	/*
	 * scheduled in spring-context.xml
	 */
	public void checkAcceptance() {
		LOGGER.info( "checking node acceptance" );
		List<Registry> regs = registryService.getRegistriesToCheckAcceptance();
		for ( Registry reg : regs ) {
			RegistryStatus status;
			try {
				status = NodeRestServiceBD.getInstance( reg ).checkAcceptance( registryService.getNodeForRegistry( reg ) );
				reg.setStatus( status );
				if ( status == RegistryStatus.NOT_REGISTERED ) {
					nodeRegistrationService.clearRegistrationData( reg );
				}
				else if ( status == RegistryStatus.REGISTERED ) {
					synchronizator.synchronizeNodes( reg );
				}
				registryService.saveOrUpdate( reg );
			}
			catch ( AuthenticationException e ) {
				LOGGER.error( "[confirmActivity - wake]", e );
			}
			catch ( RestWSUnknownException e ) {
				LOGGER.error( "[checkAcceptance]", e );
			}
			catch ( RestWSUnexpectedStatusException e ) {
				LOGGER.error( "[confirmActivity - wake]", e );
			}
		}
	}

}
