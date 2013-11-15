package eu.europa.ec.jrc.lca.registry.service;

import java.util.List;

import javax.mail.internet.AddressException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.service.exceptions.AuthenticationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeRegistrationException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.RestWSUnknownException;
import eu.europa.ec.jrc.lca.registry.domain.Node;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context-test.xml" })
public class NodeRegistrationServiceTest {

	@Autowired
	private NodeRegistrationService registrationService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private NodeService nodeService;
	
	@Before
	public void setMockFields() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SendMailService sendMailService = Mockito.mock(SendMailService.class);
		notificationService.setSendMailService(sendMailService);	
		
		BroadcastingService broadcastingService = Mockito.mock(BroadcastingService.class);
		registrationService.setBroadcastingService(broadcastingService);
	}
	
	@Test(expected=NodeRegistrationException.class)
	public void testRegisterNewNodeWithKnownNodeId() throws NodeRegistrationException, RestWSUnknownException, AddressException {
		Node node = createNode("testNodeId","http://localhost:8082");
		registrationService.registerNode(node);
		
	}
	
	@Test(expected=NodeRegistrationException.class)
	public void testRegisterNewNodeWithKnownUrl() throws NodeRegistrationException, RestWSUnknownException, AddressException {
		Node node = createNode("testNodeId343","http://localhost:8081");
		registrationService.registerNode(node);	
	}
	
	
	@Test
	public void testAcceptNodeRegistration() throws NodeIllegalStatusException, RestWSUnknownException, AuthenticationException{
		List<Node> notApproved = nodeService.getListOfNotApprovedNodes();
		Node node = notApproved.get(0);
		Node approved = registrationService.acceptNodeRegistration(node);
		Assert.assertEquals(node.getNodeId(), approved.getNodeId());
		Assert.assertEquals(notApproved.size()-1, nodeService.getListOfNotApprovedNodes().size());
	}
	
	@Test(expected= NodeIllegalStatusException.class)
	public void testAcceptNodeRegistrationWhenAccepted() throws NodeIllegalStatusException, RestWSUnknownException, AuthenticationException{
		List<Node> notApproved = nodeService.getListOfNotApprovedNodes();
		Node node = notApproved.get(0);
		registrationService.acceptNodeRegistration(node);
		registrationService.acceptNodeRegistration(node);
	}
	
	@Test
	public void testRejectNodeRegistration() throws NodeIllegalStatusException, RestWSUnknownException, AuthenticationException{
		List<Node> notApproved = nodeService.getListOfNotApprovedNodes();
		Node node = notApproved.get(0);
		registrationService.rejectNodeRegistration(node, null);
		Assert.assertEquals(notApproved.size()-1, nodeService.getListOfNotApprovedNodes().size());
		Assert.assertNull(nodeService.findByNodeId(node.getNodeId()));
	}
	
	private Node createNode(String nodeId,String url) {
		Node node = new Node();
		node.setName("Unit test node");
		node.setNodeId(nodeId);
		node.setBaseUrl(url);
		node.setNodeCredentials(new NodeCredentials());
		node.getNodeCredentials().setAccessPassword("testPassword");
		node.getNodeCredentials().setAccessAccount("testAccount");
		node.setAdminName("testAdmin");
		node.setAdminEmailAddress("admin@admin.com");
		return node;
	}
}
