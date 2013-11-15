package de.iai.ilcd.util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import de.iai.ilcd.model.dao.AbstractLongIdObjectDao;

/**
 * Converter for JSF Facelets with entity objects in form selections
 */
public abstract class AbstractEntityConverter<T> implements Converter {

	/**
	 * DAO to use
	 */
	private final AbstractLongIdObjectDao<T> dao;

	/**
	 * Class for runtime instance check
	 */
	private final Class<T> clazz;

	/**
	 * Create converter
	 * 
	 * @param dao
	 *            DAO to use
	 * @param clazz
	 *            Class for runtime instance check
	 */
	public AbstractEntityConverter( AbstractLongIdObjectDao<T> dao, Class<T> clazz ) {
		super();
		this.dao = dao;
		this.clazz = clazz;
	}

	/**
	 * Get the ID of the object as string
	 * 
	 * @param obj
	 *            object
	 * @return ID of object as string
	 */
	protected abstract String getId( T obj );

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAsObject( FacesContext contect, UIComponent comp, String objStr ) {
		try {
			return this.dao.getById( objStr );
		}
		catch ( Exception e ) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public String getAsString( FacesContext contect, UIComponent comp, Object obj ) {
		if ( this.clazz.isInstance( obj ) ) {
			return this.getId( (T) obj );
		}
		return "";
	}

}