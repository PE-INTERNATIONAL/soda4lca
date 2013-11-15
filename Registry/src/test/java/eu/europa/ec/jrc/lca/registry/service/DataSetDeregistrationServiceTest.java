package eu.europa.ec.jrc.lca.registry.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDeregistrationResult;
import eu.europa.ec.jrc.lca.commons.service.exceptions.DatasetIllegalStatusException;
import eu.europa.ec.jrc.lca.registry.dao.DataSetDao;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context-test.xml" })
public class DataSetDeregistrationServiceTest {
	
	@Autowired
	private DataSetDeregistrationService dataSetDeregistrationService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private DataSetDao dataSetDao;
	
	
	@Before
	public void setMockFields() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SendMailService sendMailService = Mockito.mock(SendMailService.class);
		notificationService.setSendMailService(sendMailService);	
		
		BroadcastingService broadcastingService = Mockito.mock(BroadcastingService.class);
		dataSetDeregistrationService.setBroadcastingService(broadcastingService);
	}
	
	@Test
	public void testDeregisterDataSetsInternal() throws DatasetIllegalStatusException{
		DataSet ds1 = dataSetDao.findByUUID("UUID_1").get(0);
		DataSet ds2 = dataSetDao.findByUUID("UUID_3").get(0);
		dataSetDeregistrationService.deregisterDataSets(Arrays.asList(new DataSet[]{ds1, ds2}));
		Assert.assertEquals(0, dataSetDao.findByUUID("UUID_1").size());
		Assert.assertEquals(0, dataSetDao.findByUUID("UUID_3").size());
	}
	
	@Test(expected=DatasetIllegalStatusException.class)
	public void testDeregisterDataSetsInternalNotAccepted() throws DatasetIllegalStatusException{
		DataSet ds1 = dataSetDao.findByUUID("UUID_2").get(0);
		dataSetDeregistrationService.deregisterDataSets(Arrays.asList(new DataSet[]{ds1}));
	}
	
	@Test
	public void testDeregisterDataSetsExternalWhenVersionDoesntExist(){
		List<DataSetDTO> datasets = new ArrayList<DataSetDTO>();
		datasets.add(new DataSetDTO("UUID_6", "2.00.00"));
		List<DataSetDeregistrationResult> result = dataSetDeregistrationService.deregisterDataSets(datasets, "reason", "testNodeId1");
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(DataSetDeregistrationResult.REJECTED_NOT_FOUND, result.get(0));
		Assert.assertNotNull(dataSetDao.findByUUIDAndVersionAndNode("UUID_6", "1.00.00", "testNodeId1"));
	}
	
	@Test
	public void testDeregisterDataSetsExternalWhenDSDoesntExist(){
		List<DataSetDTO> datasets = new ArrayList<DataSetDTO>();
		datasets.add(new DataSetDTO("UUID_7", "1.00.00"));
		List<DataSetDeregistrationResult> result = dataSetDeregistrationService.deregisterDataSets(datasets, "reason", "testNodeId1");
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(DataSetDeregistrationResult.REJECTED_NOT_FOUND, result.get(0));
		Assert.assertNotNull(dataSetDao.findByUUIDAndVersionAndNode("UUID_7", "1.00.00", "testNodeId2"));
	}
	
	@Test
	public void testDeregisterDataSetsExternalWhenNotAccepted(){
		List<DataSetDTO> datasets = new ArrayList<DataSetDTO>();
		datasets.add(new DataSetDTO("UUID_5", "1.00.00"));
		List<DataSetDeregistrationResult> result = dataSetDeregistrationService.deregisterDataSets(datasets, "reason", "testNodeId1");
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(DataSetDeregistrationResult.REJECTED_WRONG_STATUS, result.get(0));
		Assert.assertNotNull(dataSetDao.findByUUIDAndVersionAndNode("UUID_5", "1.00.00", "testNodeId1"));
	}
	
	@Test(expected=NoResultException.class)
	public void testDeregisterDataSetsExternal(){
		List<DataSetDTO> datasets = new ArrayList<DataSetDTO>();
		datasets.add(new DataSetDTO("UUID_4", "1.00.00"));
		List<DataSetDeregistrationResult> result = dataSetDeregistrationService.deregisterDataSets(datasets, "reason", "testNodeId1");
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(DataSetDeregistrationResult.DEREGISTERED, result.get(0));
		dataSetDao.findByUUIDAndVersionAndNode("UUID_4", "1.00.00", "testNodeId1");
	}
}
