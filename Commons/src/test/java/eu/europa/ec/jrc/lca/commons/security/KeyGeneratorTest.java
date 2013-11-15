package eu.europa.ec.jrc.lca.commons.security;

import java.security.KeyPair;
import java.security.spec.RSAPublicKeySpec;

import junit.framework.Assert;

import org.junit.Test;

import eu.europa.ec.jrc.lca.commons.security.encryption.KeyLength;
import eu.europa.ec.jrc.lca.commons.security.encryption.KeysGenerator;



public class KeyGeneratorTest {
	
	private KeyPair kp = KeysGenerator.getKey(KeyLength._2048);
	
	@Test
	public void testGetKey(){
		KeyPair kp = KeysGenerator.getKey(KeyLength._2048);
		Assert.assertNotNull(kp);
	}
	
	@Test
	public void testGetPublicKey(){
		RSAPublicKeySpec publicKey = KeysGenerator.getPublicKey(kp);
		Assert.assertNotNull(publicKey);
	}
	
	@Test
	public void testGetPrivateKey(){
		RSAPublicKeySpec privateKey = KeysGenerator.getPublicKey(kp);
		Assert.assertNotNull(privateKey);
	}
}
