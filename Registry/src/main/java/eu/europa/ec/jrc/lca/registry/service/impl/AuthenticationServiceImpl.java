package eu.europa.ec.jrc.lca.registry.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.registry.dao.UserDao;
import eu.europa.ec.jrc.lca.registry.domain.User;
import eu.europa.ec.jrc.lca.registry.service.AuthenticationService;

@Transactional
@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthenticationServiceImpl.class);
	
	@Autowired
	private UserDao userDao;

	@Override
	public User authenticate(String login, String password) {
		try {
			User user = userDao.findByLogin(login);
			if (user != null) {
				if (user.getPassword().equals(getHashedPassword(password))) {
					return user;
				} else {
					return null;
				}
			}
			return null;
		} catch (NoResultException ex) {
			return null;
		}
	}

	private String getHashedPassword(String pass) {
		byte[] bytesOfMessage;
		try {
			bytesOfMessage = pass.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] dig = md.digest(bytesOfMessage);
			StringBuilder sb = new StringBuilder();
			for(byte b: dig){
				sb.append((b >>> 4 == 0 ? "0" : "") + Integer.toHexString(0XFF & b));
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("[getHashedPassword]",e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("[getHashedPassword]",e);
		}
		return null;
	}
}
