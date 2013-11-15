package de.iai.ilcd.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fzk.iai.ilcd.service.model.process.IComplianceSystem;
import de.iai.ilcd.delegate.DataSetRestServiceBD;
import de.iai.ilcd.delegate.NodeRestServiceBD;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.dao.DataSetRegistrationDataDao;
import de.iai.ilcd.model.dao.RegistryDao;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.registry.DataSetRegistrationData;
import de.iai.ilcd.model.registry.DataSetRegistrationDataStatus;
import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.security.UserAccessBean;
import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationResult;
import eu.europa.ec.jrc.lca.commons.security.SecurityContext;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

@Service( "dataSetRegistrationService" )
public class DataSetRegistrationServiceImpl implements DataSetRegistrationService {

	@Autowired
	private RegistryDao registryDao;

	@Autowired
	private SecurityContext securityContext;

	@Autowired
	private DataSetRegistrationDataDao dataSetRegistrationDataDao;

	private UserDao userDao = new UserDao();

	@Override
	public List<DataSetRegistrationData> getRegistered( Long registryId ) {
		return dataSetRegistrationDataDao.getRegistered( registryId );
	}

	@Override
	public List<DataSetRegistrationData> getRegisteredNotAccepted( Long registryId ) {
		return dataSetRegistrationDataDao.getRegisteredNotAccepted( registryId );
	}

	@Override
	public List<DataSetRegistrationData> getRegisteredAccepted( Long registryId ) {
		return dataSetRegistrationDataDao.getRegisteredAccepted( registryId );
	}

	@Override
	public List<DataSetRegistrationResult> register( List<Process> processes, Long registryId ) throws RestWSUnknownException, AuthenticationException,
			NodeIllegalStatusException {
		Registry reg = registryDao.findById( registryId );
		NodeCredentials credentials = getNodeCredentials( reg );
		List<DataSetRegistrationResult> result = DataSetRestServiceBD.getInstance( reg ).registerDataSets( getDatasets( processes, getCurrentUser() ),
				credentials );
		logDataSetRegistrationInfrmation( processes, result, reg );
		return result;
	}

	private List<DataSet> getDatasets( List<Process> processes, User user ) {
		List<DataSet> datasets = new ArrayList<DataSet>();
		for ( Process p : processes ) {
			DataSet ds = new DataSet();
			ds.setUuid( p.getUuid().getUuid() );
			ds.setVersion( p.getVersion().getVersionString() );
			ds.setHash( p.getXmlFile().getContentHash() );
			ds.setUser( user.getUserName() );
			ds.setUserEmail( user.getEmail() );
			ds.setOwner( p.getOwnerReference() == null ? "" : p.getOwnerReference().getShortDescription().getDefaultValue() );
			ds.setName( p.getName().getDefaultValue() );
			for ( IComplianceSystem com : p.getComplianceSystemsAsList() ) {
				ds.getComplianceUUIDs().add( ((GlobalReference) com.getReference()).getUuid().getUuid() );
			}
			datasets.add( ds );
		}
		return datasets;
	}

	private User getCurrentUser() {
		String userName = new UserAccessBean().getUserName();
		return userDao.getUser( userName );
	}

	private void logDataSetRegistrationInfrmation( List<Process> processes, List<DataSetRegistrationResult> result, Registry registry ) {
		for ( int i = 0; i < processes.size(); i++ ) {
			if ( result.get( i ) == DataSetRegistrationResult.ACCEPTED_PENDING ) {
				Process p = processes.get( i );
				DataSetRegistrationData dsrd = new DataSetRegistrationData();
				dsrd.setVersion( p.getVersion() );
				dsrd.setUuid( p.getUuidAsString() );
				dsrd.setRegistry( registry );
				dsrd.setStatus( DataSetRegistrationDataStatus.NOT_ACCEPTED );
				dataSetRegistrationDataDao.saveOrUpdate( dsrd );
			}
		}
	}

	@Override
	public List<DataSetRegistrationData> getListOfRegistrations( Process process ) {
		return dataSetRegistrationDataDao.getListOfRegistrations( process );
	}

	private NodeCredentials getNodeCredentials( Registry reg ) throws RestWSUnknownException {
		reg.getNodeCredentials().decrypt( securityContext.getPrivateKey() );
		return reg.getNodeCredentials().getEncryptedCopyWithNonce( NodeRestServiceBD.getInstance( reg ).getPublicKey() );
	}

}
