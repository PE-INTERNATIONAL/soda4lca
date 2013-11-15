package eu.europa.ec.jrc.lca.commons.rest.dto;

import java.math.BigInteger;
import java.security.spec.RSAPublicKeySpec;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class KeyWrapper {
	private BigInteger modulus;
	private BigInteger publicExponent;

	public KeyWrapper() {

	}

	public KeyWrapper(RSAPublicKeySpec spec) {
		this.modulus = spec.getModulus();
		this.publicExponent = spec.getPublicExponent();
	}

	public RSAPublicKeySpec getRSAPublicKeySpec() {
		return new RSAPublicKeySpec(modulus, publicExponent);
	}

	public BigInteger getModulus() {
		return modulus;
	}

	public void setModulus(BigInteger modulus) {
		this.modulus = modulus;
	}

	public BigInteger getPublicExponent() {
		return publicExponent;
	}

	public void setPublicExponent(BigInteger publicExponent) {
		this.publicExponent = publicExponent;
	}
	
}
