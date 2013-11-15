package eu.europa.ec.jrc.lca.commons.service.exceptions;

public class NodeIllegalStatusException extends Exception {
	private static final long serialVersionUID = 2468253137865661482L;

	public NodeIllegalStatusException() {
	}

	public NodeIllegalStatusException(String cause) {
		super(cause);
	}
}
