package eu.europa.ec.jrc.lca.commons.security.authenticators;

import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.persistence.NoResultException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;

import eu.europa.ec.jrc.lca.commons.dao.CredentialsDao;
import eu.europa.ec.jrc.lca.commons.dao.NonceDao;
import eu.europa.ec.jrc.lca.commons.rest.dto.SecuredRequestWrapper;
import eu.europa.ec.jrc.lca.commons.security.Credentials;
import eu.europa.ec.jrc.lca.commons.security.SecurityContext;

@Component("authenticator")
public class AuthenticatorImpl implements Authenticator {
	@Value("${authenticator.nonce.duration}")
	private int nonceDuration;
	
	@Autowired
	private SecurityContext securityContext;
	
	@Autowired
	private CredentialsDao credentialsDao;
	
	@Autowired
	private NonceDao nonceDao;
	
	public Credentials<?> getCredentials(ContainerRequest request) {
		return request.getEntity(SecuredRequestWrapper.class).getCredentials();
	}
	
	public void authenticate(ContainerRequest request) {
		Credentials<?> credentials = getCredentials(request);
		
		if (credentials == null) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}

		credentials.decrypt(securityContext.getPrivateKey());

		verifyNonce(credentials.getAccessPassword(),
				credentials.getNonce());

		if(nonceDao.exists(credentials.getNonce())){
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		nonceDao.save(credentials.getNonce());
		
		String pass = getPassword(credentials.getAccessPassword(),
				credentials.getNonce());

		try{
			Credentials<?> storedCredentials = credentialsDao
					.findUniqueId(credentials.getUniqueId());
			storedCredentials.decrypt(securityContext.getPrivateKey());
			
			if (!storedCredentials.getAccessPassword().equals(pass)
					|| !storedCredentials.getAccessAccount().equals(
							credentials.getAccessAccount())) {
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}
		}catch(NoResultException e){
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
	}
	
	private void verifyNonce(String decrptedPassword, String nonce) {
		if (nonce == null || nonce.equals("")) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		if (!decrptedPassword.endsWith(nonce)) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
	}

	private String getPassword(String decrptedPassword, String nonce) {
		if (nonce == null) {
			return decrptedPassword;
		}
		return decrptedPassword.substring(0,
				decrptedPassword.length() - nonce.length());
	}
	
	@PostConstruct
	@Scheduled(cron = "${authenticator.clearNonce.startTime}")
	public void updateNodesStatus() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1*nonceDuration);
		nonceDao.clearNonces(cal.getTime());
	}
}
