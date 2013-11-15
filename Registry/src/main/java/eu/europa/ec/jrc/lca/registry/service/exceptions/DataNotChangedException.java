package eu.europa.ec.jrc.lca.registry.service.exceptions;

public class DataNotChangedException extends Exception {

	private static final long serialVersionUID = 7526612051696383930L;

	public DataNotChangedException() {
	}

	public DataNotChangedException(String cause) {
		super(cause);
	}

}
