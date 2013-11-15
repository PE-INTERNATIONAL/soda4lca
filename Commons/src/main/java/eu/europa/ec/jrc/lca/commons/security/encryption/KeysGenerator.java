package eu.europa.ec.jrc.lca.commons.security.encryption;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class KeysGenerator {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(KeysGenerator.class);

	private static final String ALGORITHM = "RSA";
	
	private KeysGenerator(){}
	
	public static KeyPair getKey(KeyLength length) {
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance(ALGORITHM);
			kpg.initialize(length.getSize());
			return kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("[getKey]",e);
		}
		return null;
	}

	public static RSAPublicKeySpec getPublicKey(KeyPair kp) {
		try {
			return KeyFactory.getInstance(ALGORITHM).getKeySpec(kp.getPublic(),
					RSAPublicKeySpec.class);
		} catch (InvalidKeySpecException e) {
			LOGGER.error("[getPublicKey]",e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("[getPublicKey]",e);
		}
		return null;
	}

	public static RSAPrivateKeySpec getPrivateKey(KeyPair kp) {
		try {
			return KeyFactory.getInstance(ALGORITHM).getKeySpec(kp.getPrivate(),
					RSAPrivateKeySpec.class);
		} catch (InvalidKeySpecException e) {
			LOGGER.error("[getPrivateKey]",e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("[getPrivateKey]",e);
		}
		return null;
	}
}
