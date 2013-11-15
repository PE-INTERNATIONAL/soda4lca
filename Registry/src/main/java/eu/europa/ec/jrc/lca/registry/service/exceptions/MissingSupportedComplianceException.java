package eu.europa.ec.jrc.lca.registry.service.exceptions;

public class MissingSupportedComplianceException extends Exception {

	private static final long serialVersionUID = 436920683841216917L;

	public MissingSupportedComplianceException() {
	}

	public MissingSupportedComplianceException(String cause) {
		super(cause);
	}

}
