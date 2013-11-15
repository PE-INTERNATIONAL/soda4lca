package eu.europa.ec.jrc.lca.registry.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationResult;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context-test.xml" })
public class DataSetRegistrationServiceTest {
	
	@Autowired
	private DataSetRegistrationService dataSetRegistrationService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Before
	public void setMockFields() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SendMailService sendMailService = Mockito.mock(SendMailService.class);
		notificationService.setSendMailService(sendMailService);	
	}
	
	@Test
	public void testRegisterDataSet() throws NodeIllegalStatusException{
		DataSet ds = createDS("UUID1", "complianceUUID1");
		List<DataSetRegistrationResult> registered = dataSetRegistrationService.registerDataSets(Arrays.asList(new DataSet[]{ds}), "testNodeId2");
		Assert.assertNotNull(registered.get(0));
		Assert.assertEquals(DataSetRegistrationResult.ACCEPTED_PENDING, registered.get(0));
	}
	
	@Test
	public void testRegisterDataSets() throws NodeIllegalStatusException{
		List<DataSet> dss = new ArrayList<DataSet>();
		dss.add(createDS("UUID2", "complianceUUID1"));
		dss.add(createDS("UUID3", "complianceUUID1"));
		List<DataSetRegistrationResult> registered = dataSetRegistrationService.registerDataSets(dss, "testNodeId2");
		Assert.assertNotNull(registered);
		Assert.assertEquals(2, registered.size());
		for(DataSetRegistrationResult ds: registered){
			Assert.assertEquals(DataSetRegistrationResult.ACCEPTED_PENDING, ds);
		}
	}
	
	@Test
	public void testRegisterDataSets2() throws NodeIllegalStatusException{
		List<DataSet> dss = new ArrayList<DataSet>();
		dss.add(createDS( "UUID2", "complianceUUID1"));
		dss.add(createDS( "UUID3", "complianceUUID1"));
		List<DataSetRegistrationResult> registered = dataSetRegistrationService.registerDataSets(dss, "testNodeId2");
		Assert.assertNotNull(registered);
		Assert.assertEquals(2, registered.size());
		for(DataSetRegistrationResult ds: registered){
			Assert.assertEquals(DataSetRegistrationResult.REJECTED_NO_DIFFERENCE, ds);
		}
	}
	
	@Test
	public void testRegisterDataSets3() throws NodeIllegalStatusException{
		List<DataSet> dss = new ArrayList<DataSet>();
		dss.add(createDS("UUID9", "complianceUUID10"));
		dss.add(createDS( "UUID10", "complianceUUID10"));
		List<DataSetRegistrationResult> registered = dataSetRegistrationService.registerDataSets(dss, "testNodeId2");
		Assert.assertNotNull(registered);
		Assert.assertEquals(2, registered.size());
		for(DataSetRegistrationResult ds: registered){
			Assert.assertEquals(DataSetRegistrationResult.REJECTED_COMPLIANCE, ds);
		}
	}

	@Test(expected=NodeIllegalStatusException.class)
	public void testRegisterDataSetInNotApprovedNode() throws NodeIllegalStatusException{
		DataSet ds = createDS("UUID4", "complianceUUID4");
		dataSetRegistrationService.registerDataSets(Arrays.asList(new DataSet[]{ds}), "testNodeId5");
	}

	
	private DataSet createDS(String UUID, String...complianceUUID){
		DataSet ds = new DataSet();
		ds.getComplianceUUIDs().addAll(Arrays.asList(complianceUUID));
		ds.setUuid(UUID);
		ds.setHash(new byte[]{1,2,3,4,5,6,7,8,9});
		return ds;
	}
}
