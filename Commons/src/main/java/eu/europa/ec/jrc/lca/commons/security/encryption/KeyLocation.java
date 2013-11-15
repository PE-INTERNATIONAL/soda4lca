package eu.europa.ec.jrc.lca.commons.security.encryption;

import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public interface KeyLocation {
	boolean keyExists();
	RSAPublicKeySpec getPublicKey();
	RSAPrivateKeySpec getPrivateKey();
	void store(RSAPublicKeySpec publicKey, RSAPrivateKeySpec privateKey);
}
