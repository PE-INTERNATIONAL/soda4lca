package eu.europa.ec.jrc.lca.commons.service.exceptions;

public class DatasetIllegalStatusException extends Exception {
	private static final long serialVersionUID = -1471951290784362064L;

	public DatasetIllegalStatusException() {
	}

	public DatasetIllegalStatusException(String cause) {
		super(cause);
	}
}
