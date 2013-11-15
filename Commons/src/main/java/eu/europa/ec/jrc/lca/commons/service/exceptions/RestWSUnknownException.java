package eu.europa.ec.jrc.lca.commons.service.exceptions;

public class RestWSUnknownException extends Exception {
	private static final long serialVersionUID = 6967678600370691977L;

	public RestWSUnknownException() {
	}
	
	public RestWSUnknownException(String cause) {
		super(cause);
	}
}
