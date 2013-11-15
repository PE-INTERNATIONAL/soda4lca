package de.iai.ilcd.webgui.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.services.RegistryService;
import eu.europa.ec.jrc.lca.commons.view.util.SelectItemsProducer;

@Component
@Scope( "request" )
public class RegistriesSelectItems extends SelectItemsProducer<Registry> {

	@Autowired
	private RegistryService registryService;

	@PostConstruct
	public void init() {
		setEntities( registryService.getNonVirtualRegistriesInWhichRegistered() );
		addNotSelectedItem();
	}

	@Override
	public Object getValue( Registry entity ) {
		return entity.getUuid();
	}

	@Override
	public String getLabel( Registry entity ) {
		return entity.getName();
	}

}
