package de.iai.ilcd.webgui.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import eu.europa.ec.jrc.lca.commons.view.util.Messages;

@FacesValidator( "de.iai.ilcd.webgui.validators.NodeBaseUrlValidator" )
public class NodeBaseUrlValidator implements Validator {

	@Override
	public void validate( FacesContext context, UIComponent component, Object value ) throws ValidatorException {

		if ( !isUrlValid( (String) value ) ) {
			FacesMessage message = Messages.getMessage( "resources.lang", "admin.registerNode.invalidBaseUrl", null );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			throw new ValidatorException( message );
		}

	}

	private boolean isUrlValid( String baseURL ) {
		if ( baseURL != null && !baseURL.endsWith( "/" ) ) {
			return true;
		}
		return false;
	}
}
