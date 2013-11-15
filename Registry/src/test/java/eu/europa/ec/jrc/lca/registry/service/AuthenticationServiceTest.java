package eu.europa.ec.jrc.lca.registry.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.jrc.lca.registry.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-context-test.xml" })
public class AuthenticationServiceTest {
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Test
	public void testAuthenticateShouldAuthenticate(){
		User u = authenticationService.authenticate("user1", "pass1");
		Assert.assertNotNull(u);
	}
	
	@Test
	public void testAuthenticateWrongLogin(){
		User u = authenticationService.authenticate("user10", "pass1");
		Assert.assertNull(u);
	}
	
	@Test
	public void testAuthenticateWrongPassword(){
		User u = authenticationService.authenticate("user1", "pass10");
		Assert.assertNull(u);
	}
}
