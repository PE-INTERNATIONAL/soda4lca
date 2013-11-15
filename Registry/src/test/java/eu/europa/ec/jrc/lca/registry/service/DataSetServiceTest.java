package eu.europa.ec.jrc.lca.registry.service;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationAcceptanceDecision;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context-test.xml" })
public class DataSetServiceTest {
	
	@Autowired
	private DataSetService dataSetService;
	
//	@Test
	public void testVerifyDataSetValid() throws ResourceNotFoundException{
		DataSet ds = createDS("UUID_1", "");
		ds.setHash(new byte[]{0x12,0x34,0x56});
		boolean result = dataSetService.verifyDataSet(ds);
		Assert.assertTrue(result);
	}
	
//	@Test
	public void testVerifyDataSetInvalid() throws ResourceNotFoundException{
		DataSet ds = createDS("UUID_1", "");
		ds.setHash(new byte[]{0x13,0x34,0x56});
		boolean result = dataSetService.verifyDataSet(ds);
		Assert.assertFalse(result);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void testVerifyDataSetDoesntExist() throws ResourceNotFoundException{
		DataSet ds = createDS("UUID_14", "");
		ds.setHash(new byte[]{0x13,0x34,0x56});
		dataSetService.verifyDataSet(ds);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void testVerifyDataSetNotAccepted() throws ResourceNotFoundException{
		DataSet ds = createDS("UUID_14", "");
		ds.setHash(new byte[]{0x13,0x34,0x56});
		dataSetService.verifyDataSet(ds);
	}
	
//	@Test
	public void testGetStatus(){
		List<DataSetDTO> dsDTOs = Arrays.asList(new DataSetDTO[]{
				new DataSetDTO("UUID_1","1.00.00"),
				new DataSetDTO("UUID_2","1.00.00"),
				new DataSetDTO("UUID_3","1.00.00"),
				new DataSetDTO("UUID_14","1.00.00")});
		
		List<DataSetRegistrationAcceptanceDecision> resul = dataSetService.getStatus(dsDTOs, "testNodeId1");
		
		Assert.assertNotNull(resul);
		Assert.assertEquals(4, resul.size());
		
		Assert.assertEquals(DataSetRegistrationAcceptanceDecision.ACCEPTED, resul.get(0));
		Assert.assertEquals(DataSetRegistrationAcceptanceDecision.PENDING, resul.get(1));
		Assert.assertEquals(DataSetRegistrationAcceptanceDecision.ACCEPTED, resul.get(2));
		Assert.assertEquals(DataSetRegistrationAcceptanceDecision.REJECTED, resul.get(3));
	}
	
	private DataSet createDS(String UUID, String...complianceUUID){
		DataSet ds = new DataSet();
		ds.getComplianceUUIDs().addAll(Arrays.asList(complianceUUID));
		ds.setUuid(UUID);
		ds.setHash(new byte[]{1,2,3,4,5,6,7,8,9});
		return ds;
	}
}
