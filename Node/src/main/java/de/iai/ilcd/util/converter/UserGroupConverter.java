package de.iai.ilcd.util.converter;

import javax.faces.convert.FacesConverter;

import de.iai.ilcd.model.dao.UserGroupDao;
import de.iai.ilcd.model.security.UserGroup;

/**
 * Converter for JSF Facelets with user group objects in form selections
 */
@FacesConverter( value = "userGroupConverter", forClass = UserGroup.class )
public class UserGroupConverter extends AbstractEntityConverter<UserGroup> {

	/**
	 * Create converter
	 */
	public UserGroupConverter() {
		super( new UserGroupDao(), UserGroup.class );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId( UserGroup obj ) {
		return obj.getId().toString();
	}

}