package de.iai.ilcd.util.converter;

import javax.faces.convert.FacesConverter;

import de.iai.ilcd.model.dao.OrganizationDao;
import de.iai.ilcd.model.security.Organization;

/**
 * Converter for JSF Facelets with organization objects in form selections
 */
@FacesConverter( value = "orgConverter", forClass = Organization.class )
public class OrganizationConverter extends AbstractEntityConverter<Organization> {

	/**
	 * Create converter
	 */
	public OrganizationConverter() {
		super( new OrganizationDao(), Organization.class );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId( Organization obj ) {
		return obj.getId().toString();
	}
}