package de.iai.ilcd.webgui.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import de.iai.ilcd.model.registry.RegistryStatus;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;

@FacesConverter( forClass = RegistryStatus.class )
public class RegistryStatusConverter implements Converter {

	@Override
	public Object getAsObject( FacesContext context, UIComponent component, String value ) {
		return RegistryStatus.valueOf( value );
	}

	@Override
	public String getAsString( FacesContext context, UIComponent component, Object value ) {
		return Messages.getString( null, ((RegistryStatus) value).name(), null );
	}

}
