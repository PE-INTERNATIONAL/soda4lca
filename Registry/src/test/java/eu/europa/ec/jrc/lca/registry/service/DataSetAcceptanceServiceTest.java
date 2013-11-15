package eu.europa.ec.jrc.lca.registry.service;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.jrc.lca.commons.service.exceptions.DatasetIllegalStatusException;
import eu.europa.ec.jrc.lca.registry.dao.DataSetDao;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context-test.xml" })
public class DataSetAcceptanceServiceTest {
	
	@Autowired
	private DataSetAcceptanceService dataSetAcceptanceService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private DataSetDao dataSetDao;
	
	
	@Before
	public void setMockFields() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SendMailService sendMailService = Mockito.mock(SendMailService.class);
		notificationService.setSendMailService(sendMailService);	
	}
	
	@Test
	public void testAcceptDataSets(){
		DataSet ds1 = dataSetDao.findByUUID("UUID_9").get(0);
		DataSet ds2 = dataSetDao.findByUUID("UUID_10").get(0);
		dataSetAcceptanceService.acceptDataSets(Arrays.asList(new DataSet[]{ds1, ds2}));
		Assert.assertEquals(DataSetStatus.ACCEPTED, dataSetDao.findByUUID("UUID_9").get(0).getStatus());
		Assert.assertEquals(DataSetStatus.ACCEPTED, dataSetDao.findByUUID("UUID_10").get(0).getStatus());
	}
	
	@Test
	public void acceptUpdateDataSet() throws DatasetIllegalStatusException{
		DataSet ds1 = dataSetDao.findByUUIDAndVersionAndNode("UUID_11", "1.10.00","testNodeId4");
		dataSetAcceptanceService.acceptUpdateDataSet(ds1);
		Assert.assertEquals(1, dataSetDao.findByUUID("UUID_11").size());
		Assert.assertEquals(DataSetStatus.ACCEPTED, dataSetDao.findByUUID("UUID_11").get(0).getStatus());
		Assert.assertEquals("1.10.00", dataSetDao.findByUUID("UUID_11").get(0).getVersion());
	}
	
	@Test
	public void rejectDataSets(){
		DataSet ds1 = dataSetDao.findByUUIDAndVersionAndNode("UUID_12", "1.10.00","testNodeId5");
		DataSet ds2 = dataSetDao.findByUUIDAndVersionAndNode("UUID_12", "1.11.00","testNodeId5");
		dataSetAcceptanceService.rejectDataSets(Arrays.asList(new DataSet[]{ds1, ds2}), "reason");
		Assert.assertEquals(0, dataSetDao.findByUUID("UUID_12").size());
	}
	
}
