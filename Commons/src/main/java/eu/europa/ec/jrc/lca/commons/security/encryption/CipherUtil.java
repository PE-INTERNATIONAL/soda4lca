package eu.europa.ec.jrc.lca.commons.security.encryption;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CipherUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CipherUtil.class);

	private static final String ALGORITHM = "RSA";
	
	private static KeyFactory fact;

	static {
		try {
			fact = KeyFactory.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("[static initialization]",e);
		}
	}
	
	private CipherUtil(){}

	public static byte[] encrypt(RSAPublicKeySpec keySpec, byte[] data) {
		PublicKey key = getKey(keySpec);
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("[encrypt]",e);
		} catch (NoSuchPaddingException e) {
			LOGGER.error("[encrypt]",e);
		} catch (InvalidKeyException e) {
			LOGGER.error("[encrypt]",e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("[encrypt]",e);
		} catch (BadPaddingException e) {
			LOGGER.error("[encrypt]",e);
		}
		return null;
	}
	
	public static byte[] decrypt(RSAPrivateKeySpec keySpec, byte[] data) {
		PrivateKey key = getKey(keySpec);
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("[encrypt]",e);
		} catch (NoSuchPaddingException e) {
			LOGGER.error("[encrypt]",e);
		} catch (InvalidKeyException e) {
			LOGGER.error("[encrypt]",e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("[encrypt]",e);
		} catch (BadPaddingException e) {
			LOGGER.error("[encrypt]",e);
		}
		return null;
	}

	private static PublicKey getKey(RSAPublicKeySpec keySpec) {
		try {
			return fact.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			LOGGER.error("[getKey]",e);
		}
		return null;
	}
	
	private static PrivateKey getKey(RSAPrivateKeySpec keySpec) {
		try {
			return fact.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			LOGGER.error("[getKey]",e);
		}
		return null;
	}
}
