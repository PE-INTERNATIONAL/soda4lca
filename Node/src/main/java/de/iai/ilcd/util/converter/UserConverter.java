package de.iai.ilcd.util.converter;

import javax.faces.convert.FacesConverter;

import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.security.User;

/**
 * Converter for JSF Facelets with user objects in form selections
 */
@FacesConverter( value = "userConverter", forClass = User.class )
public class UserConverter extends AbstractEntityConverter<User> {

	/**
	 * Create converter
	 */
	public UserConverter() {
		super( new UserDao(), User.class );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId( User obj ) {
		return obj.getId().toString();
	}

}