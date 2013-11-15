package eu.europa.ec.jrc.lca.registry.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeDegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context-test.xml" })
public class NodeDeregistrationServiceTest {

	@Autowired
	private NodeDeregistrationService deregistrationService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private NodeService nodeService;
	
	@Before
	public void setMockFields() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SendMailService sendMailService = Mockito.mock(SendMailService.class);
		notificationService.setSendMailService(sendMailService);	
		
		BroadcastingService broadcastingService = Mockito.mock(BroadcastingService.class);
		deregistrationService.setBroadcastingService(broadcastingService);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void testDeregisterNodeDoesntExist() throws ResourceNotFoundException, RestWSUnknownException, AuthenticationException, NodeDegistrationException{
		deregistrationService.deregisterNode("testNodeId-1000");
	}
	
	@Test
	public void testDeregisterNode() throws ResourceNotFoundException, RestWSUnknownException, AuthenticationException, NodeDegistrationException{
		deregistrationService.deregisterNode("testNodeId10");
		Assert.assertNull(nodeService.findByNodeId("testNodeId10"));
	}
}
