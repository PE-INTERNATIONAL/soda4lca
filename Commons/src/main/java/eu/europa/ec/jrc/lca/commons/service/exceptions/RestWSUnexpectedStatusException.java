package eu.europa.ec.jrc.lca.commons.service.exceptions;

public class RestWSUnexpectedStatusException extends Exception {
	private static final long serialVersionUID = 7981088265054873818L;

	private int responseStatus;

	public RestWSUnexpectedStatusException(int status) {
		this.responseStatus = status;
	}

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}
}
