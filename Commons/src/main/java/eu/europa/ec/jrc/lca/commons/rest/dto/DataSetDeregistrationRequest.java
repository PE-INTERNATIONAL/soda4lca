package eu.europa.ec.jrc.lca.commons.rest.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataSetDeregistrationRequest {
	private String reason;
	
	private List<DataSetDTO> datasets;

	public DataSetDeregistrationRequest() {
	}

	public DataSetDeregistrationRequest(String reason, List<DataSetDTO> datasets) {
		super();
		this.reason = reason;
		this.datasets = datasets;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public List<DataSetDTO> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DataSetDTO> datasets) {
		this.datasets = datasets;
	}
}
