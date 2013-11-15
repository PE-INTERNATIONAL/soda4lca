package eu.europa.ec.jrc.lca.commons.rest;


public enum CustomStatus {
	ENTITY_NOT_FOUND(420);
	
	private int code;
	
	CustomStatus(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}
