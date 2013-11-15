package de.iai.ilcd.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.iai.ilcd.delegate.DataSetRestServiceBD;
import de.iai.ilcd.delegate.NodeRestServiceBD;
import de.iai.ilcd.model.dao.DataSetRegistrationDataDao;
import de.iai.ilcd.model.dao.DatasetDeregistrationReasonDao;
import de.iai.ilcd.model.dao.RegistryDao;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import de.iai.ilcd.model.registry.DataSetRegistrationDataStatus;
import de.iai.ilcd.model.registry.DatasetDeregistrationReason;
import de.iai.ilcd.model.registry.Registry;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDeregistrationResult;
import eu.europa.ec.jrc.lca.commons.security.SecurityContext;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

@Service( "dataSetDeregistrationService" )
public class DataSetDeregistrationServiceImpl implements DataSetDeregistrationService {

	private static final Logger LOGGER = LoggerFactory.getLogger( DataSetDeregistrationServiceImpl.class );

	@Autowired
	private RegistryDao registryDao;

	@Autowired
	private SecurityContext securityContext;

	@Autowired
	private DataSetRegistrationDataDao dataSetRegistrationDataDao;

	@Autowired
	private DatasetDeregistrationReasonDao datasetDeregistrationReasonDao;

	@Override
	public void applyDeregistration( String registryId, DataSet changedDS ) throws RestWSUnknownException {
		Registry registry = registryDao.findByIdentity( registryId );
		DataSet synchronizedDS = DataSetRestServiceBD.getInstance( registry ).getDataSetData( changedDS );
		if ( synchronizedDS == null ) {
			try {
				DataSetRegistrationData data = dataSetRegistrationDataDao.findByUUIDAndVersionAndRegistry( changedDS.getUuid(), changedDS.getVersion(),
						registry.getId() );
				dataSetRegistrationDataDao.remove( data.getId() );
			}
			catch ( NoResultException e ) {
				// do nothing - dataset has been removed, and changes have not been propagated to registry yet
			}
		}
	}

	@Override
	public void deregisterDatasets( List<DataSetRegistrationData> registrationData, String reason, Registry registry ) throws AuthenticationException,
			RestWSUnknownException {
		try {
			sendDeregistrationRequest( registrationData, reason, registry );
		}
		catch ( RestWSUnknownException e ) {
			DatasetDeregistrationReason dsReason = createReason( reason );
			moveToPendingStateWithReason( registrationData, dsReason );
			throw e;
		}
	}

	@Override
	public void deregisterDatasets( List<Process> processes, String reason, Long registryId ) throws AuthenticationException, RestWSUnknownException {
		Registry registry = registryDao.findById( registryId );
		List<DataSetRegistrationData> registrationData = getRegistrationData( processes, registry );
		try {
			sendDeregistrationRequest( registrationData, reason, registry );
		}
		catch ( RestWSUnknownException e ) {
			DatasetDeregistrationReason dsReason = createReason( reason );
			moveToPendingStateWithReason( registrationData, dsReason );
			throw e;
		}
	}

	@Override
	public void resubmitDeregisterDatasets( List<DataSetRegistrationData> registrationData, String reason, Registry registry ) throws AuthenticationException,
			RestWSUnknownException {
		try {
			sendDeregistrationRequest( registrationData, reason, registry );
		}
		catch ( RestWSUnknownException e ) {
			throw e;
		}
	}

	@Override
	public void deregisterDatasetOnRemoval( Process dataSet ) throws AuthenticationException {
		List<DataSetRegistrationData> registrations = dataSetRegistrationDataDao.getListOfRegistrations( dataSet );
		if ( registrations != null && !registrations.isEmpty() ) {
			for ( DataSetRegistrationData dsdr : registrations ) {
				try {
					deregisterDatasets( Collections.singletonList( dsdr ), null, dsdr.getRegistry() );
				}
				catch ( RestWSUnknownException e ) {
					// do nothing - will be resubmitted when possible
				}
			}
		}
	}

	private void sendDeregistrationRequest( List<DataSetRegistrationData> registrationData, String reason, Registry registry ) throws AuthenticationException,
			RestWSUnknownException {
		NodeCredentials credentials = getNodeCredentials( registry );
		List<DataSetDeregistrationResult> result = DataSetRestServiceBD.getInstance( registry ).deregisterDataSets( getDTOs( registrationData ), reason,
				credentials );
		updateDataSetRegistrationData( result, registrationData );
	}

	private NodeCredentials getNodeCredentials( Registry reg ) throws RestWSUnknownException {
		reg.getNodeCredentials().decrypt( securityContext.getPrivateKey() );
		return reg.getNodeCredentials().getEncryptedCopyWithNonce( NodeRestServiceBD.getInstance( reg ).getPublicKey() );
	}

	private List<DataSetDTO> getDTOs( List<DataSetRegistrationData> registrationData ) {
		List<DataSetDTO> dtos = new ArrayList<DataSetDTO>();
		for ( DataSetRegistrationData dsrd : registrationData ) {
			dtos.add( new DataSetDTO( dsrd.getUuid(), dsrd.getVersion().getVersionString() ) );
		}
		return dtos;
	}

	private void moveToPendingStateWithReason( List<DataSetRegistrationData> registrationData, DatasetDeregistrationReason dsReason ) {
		for ( DataSetRegistrationData dsrd : registrationData ) {
			if ( dsrd.getStatus().equals( DataSetRegistrationDataStatus.ACCEPTED ) ) {
				dsrd.setStatus( DataSetRegistrationDataStatus.PENDING_DEREGISTRATION );
				dsrd.setReason( dsReason );
				dataSetRegistrationDataDao.saveOrUpdate( dsrd );
			}
		}
	}

	private DatasetDeregistrationReason createReason( String reason ) {
		DatasetDeregistrationReason dsReason = new DatasetDeregistrationReason();
		try {
			dsReason.setContent( reason );
		}
		catch ( UnsupportedEncodingException e ) {
			LOGGER.error( "[createReason]", e );
		}
		return datasetDeregistrationReasonDao.saveOrUpdate( dsReason );
	}

	private void updateDataSetRegistrationData( List<DataSetDeregistrationResult> statuses, List<DataSetRegistrationData> registrationData ) {
		for ( Iterator statIter = statuses.iterator(), rDataIter = registrationData.iterator(); statIter.hasNext(); ) {
			DataSetDeregistrationResult status = (DataSetDeregistrationResult) statIter.next();
			DataSetRegistrationData rData = (DataSetRegistrationData) rDataIter.next();
			if ( status == DataSetDeregistrationResult.DEREGISTERED ) {
				dataSetRegistrationDataDao.remove( rData.getId() );
			}
		}
	}

	private List<DataSetRegistrationData> getRegistrationData( List<Process> processes, Registry registry ) {
		List<DataSetRegistrationData> result = new ArrayList<DataSetRegistrationData>();
		for ( Process p : processes ) {
			result.add( dataSetRegistrationDataDao.findByUUIDAndVersionAndRegistry( p.getUuid().getUuid(), p.getVersion().getVersionString(), registry.getId() ) );
		}
		return result;
	}
}
