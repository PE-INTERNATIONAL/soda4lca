package eu.europa.ec.jrc.lca.commons.security.encryption;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class FileKeyLocation implements KeyLocation {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileKeyLocation.class);

	@Value("${key.path}")
	private String keyPath;

	private static final String PUBLIC_KEY_FILE_NAME = "public.key";

	private static final String PRIVATE_KEY_FILE_NAME = "private.key";

	@Override
	public boolean keyExists() {
		return new File(getLocation() + PUBLIC_KEY_FILE_NAME).exists()
				&& new File(getLocation() + PRIVATE_KEY_FILE_NAME).exists();
	}

	@Override
	public RSAPublicKeySpec getPublicKey() {
		ObjectInputStream oin = null;
		try {
			oin = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(getLocation() + PUBLIC_KEY_FILE_NAME)));

			BigInteger mod = (BigInteger) oin.readObject();
			BigInteger exp = (BigInteger) oin.readObject();
			return new RSAPublicKeySpec(mod, exp);
		} catch (FileNotFoundException e) {
			LOGGER.error("[getPublicKey]", e);
		} catch (IOException e) {
			LOGGER.error("[getPublicKey]", e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("[getPublicKey]", e);
		} finally {
			try {
				oin.close();
			} catch (IOException e) {
				LOGGER.error("[getPublicKey]", e);
			}
		}
		return null;
	}

	@Override
	public RSAPrivateKeySpec getPrivateKey() {
		ObjectInputStream oin = null;
		try {
			oin = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(getLocation() + PRIVATE_KEY_FILE_NAME)));

			BigInteger mod = (BigInteger) oin.readObject();
			BigInteger exp = (BigInteger) oin.readObject();
			return new RSAPrivateKeySpec(mod, exp);
		} catch (FileNotFoundException e) {
			LOGGER.error("[getPrivateKey]", e);
		} catch (IOException e) {
			LOGGER.error("[getPrivateKey]", e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("[getPrivateKey]", e);
		} finally {
			try {
				oin.close();
			} catch (IOException e) {
				LOGGER.error("[getPrivateKey]", e);
			}
		}
		return null;
	}

	@Override
	public void store(RSAPublicKeySpec publicKey, RSAPrivateKeySpec privateKey) {
		storePublic(publicKey);
		storePrivate(privateKey);
	}

	private void storePublic(RSAPublicKeySpec publicKey) {
		ObjectOutputStream oout = null;
		try {
			oout = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(getLocation() + PUBLIC_KEY_FILE_NAME)));
			BigInteger mod = publicKey.getModulus();
			BigInteger exp = publicKey.getPublicExponent();
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (FileNotFoundException e) {
			LOGGER.error("[storePublic]", e);
		} catch (IOException e) {
			LOGGER.error("[storePublic]", e);
		} catch (Exception e) {
			LOGGER.error("[storePublic]", e);
		} finally {
			try {
				if (oout != null) {
					oout.close();
				}
			} catch (IOException e) {
				LOGGER.error("[storePublic]", e);
			}
		}
	}

	private void storePrivate(RSAPrivateKeySpec privateKey) {
		ObjectOutputStream oout = null;
		try {
			oout = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(getLocation() + PRIVATE_KEY_FILE_NAME)));
			BigInteger mod = privateKey.getModulus();
			BigInteger exp = privateKey.getPrivateExponent();
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (FileNotFoundException e) {
			LOGGER.error("[storePrivate]", e);
		} catch (IOException e) {
			LOGGER.error("[storePrivate]", e);
		} catch (Exception e) {
			LOGGER.error("[storePrivate]", e);
		} finally {
			try {
				if (oout != null) {
					oout.close();
				}
			} catch (IOException e) {
				LOGGER.error("[storePrivate]", e);
			}
		}
	}
	
	private String getLocation(){
		String catalinaBase = System.getProperty("catalina.base");
		return (catalinaBase == null ? "" : catalinaBase) + keyPath;
	}
}
