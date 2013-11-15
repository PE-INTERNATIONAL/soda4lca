package eu.europa.ec.jrc.lca.commons.rest.dto;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum DataSetDeregistrationResult {
	DEREGISTERED,
	REJECTED_NOT_FOUND,
	REJECTED_WRONG_STATUS
}
