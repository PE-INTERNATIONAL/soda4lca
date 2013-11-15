package eu.europa.ec.jrc.lca.commons.rest.dto;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
	public ObjectFactory() {
	}

	public SecuredRequestWrapper createSecuredRequestWrappert() {
		return new SecuredRequestWrapper();
	}
	
	public KeyWrapper createKeyWrapper() {
		return new KeyWrapper();
	}
	
	public JaxbBaseList createJaxbBaseList(){
		return new JaxbBaseList();
	}
	
	public DataSetDTO createDataSetDTO(){
		return new DataSetDTO();
	}
	
	public DataSetDeregistrationRequest createDataSetDeregistrationRequest(){
		return new DataSetDeregistrationRequest();
	}
	
	public DataSetRegistrationResult createDataSetRegistrationResult(Object value) {
		return DataSetRegistrationResult.valueOf((String)value);
	}
	
	public DataSetRegistrationAcceptanceDecision createDataSetRegistrationAcceptanceDecision(Object value){
		return DataSetRegistrationAcceptanceDecision.valueOf((String) value);
	}
	
	public DataSetDeregistrationResult createDataSetDeregistrationResult(Object value){
		return DataSetDeregistrationResult.valueOf((String) value);
	}
	
}
