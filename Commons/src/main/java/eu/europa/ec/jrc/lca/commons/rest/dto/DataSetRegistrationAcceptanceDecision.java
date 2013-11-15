package eu.europa.ec.jrc.lca.commons.rest.dto;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum DataSetRegistrationAcceptanceDecision {
	REJECTED,
	PENDING,
	ACCEPTED
}
