package de.iai.ilcd.util.converter;

import javax.faces.convert.FacesConverter;

import de.iai.ilcd.model.common.GeographicalArea;
import de.iai.ilcd.model.dao.GeographicalAreaDao;

/**
 * Converter for JSF Facelets with sector objects in form selections
 */
@FacesConverter( value = "geoAreaConverter", forClass = GeographicalArea.class )
public class GeogaphicalAreaConverter extends AbstractEntityConverter<GeographicalArea> {

	/**
	 * Create converter
	 */
	public GeogaphicalAreaConverter() {
		super( new GeographicalAreaDao(), GeographicalArea.class );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId( GeographicalArea obj ) {
		return obj.getId().toString();
	}

}