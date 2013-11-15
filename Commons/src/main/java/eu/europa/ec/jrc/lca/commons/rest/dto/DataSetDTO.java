package eu.europa.ec.jrc.lca.commons.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataSetDTO {
	private String uuid;
	private String version;
	
	public DataSetDTO(String uuid, String version) {
		super();
		this.uuid = uuid;
		this.version = version;
	}

	public DataSetDTO() {
		super();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
