package eu.europa.ec.jrc.lca.commons.service.exceptions;

public class ResourceNotFoundException extends Exception {
	private static final long serialVersionUID = -3690160471343817506L;

	public ResourceNotFoundException(){
	}
	
	public ResourceNotFoundException(String cause) {
		super(cause);
	}
}
