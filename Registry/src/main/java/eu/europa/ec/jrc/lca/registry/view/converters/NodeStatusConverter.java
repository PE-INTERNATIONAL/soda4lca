package eu.europa.ec.jrc.lca.registry.view.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import eu.europa.ec.jrc.lca.commons.view.util.Messages;
import eu.europa.ec.jrc.lca.registry.domain.NodeStatus;

@FacesConverter(forClass=NodeStatus.class)
public class NodeStatusConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		return NodeStatus.valueOf(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return Messages.getString(null, ((NodeStatus)value).name(), null);
	}

}
