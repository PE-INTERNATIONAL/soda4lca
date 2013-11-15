package eu.europa.ec.jrc.lca.commons.security;

import java.security.KeyPair;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.commons.security.encryption.KeyLength;
import eu.europa.ec.jrc.lca.commons.security.encryption.KeyLocation;
import eu.europa.ec.jrc.lca.commons.security.encryption.KeysGenerator;

@Component
public class SecurityContext {

	@Autowired
	private KeyLocation keyLocation;
	
	@Value("${key.length}")
	private int length;
	
	private RSAPublicKeySpec publicKey;
	
	private RSAPrivateKeySpec privateKey;
	
	@PostConstruct
	private void loadOrCreateKey(){
		if(keyLocation.keyExists()){
			publicKey = keyLocation.getPublicKey();
			privateKey = keyLocation.getPrivateKey();
		}else{
			KeyPair kp = KeysGenerator.getKey(KeyLength.get(length));
			publicKey = KeysGenerator.getPublicKey(kp);
			privateKey = KeysGenerator.getPrivateKey(kp);
			keyLocation.store(publicKey, privateKey);
		}
	}

	public RSAPublicKeySpec getPublicKey() {
		return publicKey;
	}

	public RSAPrivateKeySpec getPrivateKey() {
		return privateKey;
	}
	
}
