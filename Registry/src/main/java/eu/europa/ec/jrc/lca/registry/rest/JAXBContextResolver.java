package eu.europa.ec.jrc.lca.registry.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.registry.domain.ObjectFactory;

@Provider
@Component
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
	private JAXBContext context;

	private Set<Package> packages = new HashSet<Package>();
	
	public JAXBContextResolver() throws JAXBException {
		packages.add(ObjectFactory.class.getPackage());
		packages.add(eu.europa.ec.jrc.lca.commons.rest.dto.ObjectFactory.class.getPackage());
		
		context = JAXBContext.newInstance(getContextPath());
	}

	public JAXBContext getContext(Class<?> type) {
		if(packages.contains(type.getPackage())){
			return context;
		}
		return null;
	}

	private String getContextPath(){
		StringBuilder sb = new StringBuilder();
		for(Package p: packages){
			sb.append(p.getName()).append(":");
		}
		return sb.toString();
	}
}