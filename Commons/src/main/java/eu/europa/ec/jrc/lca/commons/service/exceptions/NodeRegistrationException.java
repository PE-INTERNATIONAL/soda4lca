package eu.europa.ec.jrc.lca.commons.service.exceptions;

public class NodeRegistrationException extends Exception {
	private static final long serialVersionUID = 1111356083666049333L;

	public NodeRegistrationException(){
	}
	
	public NodeRegistrationException(String cause) {
		super(cause);
	}
}
