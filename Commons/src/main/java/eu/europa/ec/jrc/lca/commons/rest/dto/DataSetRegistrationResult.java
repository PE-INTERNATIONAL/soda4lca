package eu.europa.ec.jrc.lca.commons.rest.dto;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum DataSetRegistrationResult {
	REJECTED_COMPLIANCE,
	REJECTED_NO_DIFFERENCE,
	ACCEPTED_PENDING
}
