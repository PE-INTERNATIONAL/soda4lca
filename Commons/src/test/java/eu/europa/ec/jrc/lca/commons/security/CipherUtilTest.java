package eu.europa.ec.jrc.lca.commons.security;

import java.security.KeyPair;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import junit.framework.Assert;

import org.junit.Test;

import eu.europa.ec.jrc.lca.commons.security.encryption.CipherUtil;
import eu.europa.ec.jrc.lca.commons.security.encryption.KeyLength;
import eu.europa.ec.jrc.lca.commons.security.encryption.KeysGenerator;

public class CipherUtilTest {
	private KeyPair kp = KeysGenerator.getKey(KeyLength._2048);
	private RSAPublicKeySpec publicKeySpec = KeysGenerator.getPublicKey(kp);
	private RSAPrivateKeySpec privateKeySpec = KeysGenerator.getPrivateKey(kp);
	
	@Test
	public void testEncrypt() {
		byte[] data = "Hello world".getBytes();
		byte[] result = CipherUtil.encrypt(publicKeySpec, data);
		Assert.assertNotNull(result);
		boolean same=true;
		for(int i=0;i < data.length; i++){
			if(data[i]!=result[i]){
				same=false;
			}
		}
		Assert.assertFalse(same);
	}
	
	
	@Test
	public void testEncryptAndDecrypt() {
		byte[] data = "Hello world".getBytes();
		byte[] encrypted = CipherUtil.encrypt(publicKeySpec, data);
		byte[] decrypted = CipherUtil.decrypt(privateKeySpec, encrypted);
		Assert.assertEquals("Hello world", new String(decrypted));
	}
}
