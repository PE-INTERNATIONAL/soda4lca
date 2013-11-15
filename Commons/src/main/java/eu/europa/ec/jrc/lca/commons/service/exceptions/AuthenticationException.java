package eu.europa.ec.jrc.lca.commons.service.exceptions;

public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 9032365504629252955L;

	public AuthenticationException() {
	}

	public AuthenticationException(String cause) {
		super(cause);
	}
}
