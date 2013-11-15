package de.iai.ilcd.util.lstring;

import org.apache.commons.collections.MapUtils;

import de.fzk.iai.ilcd.service.model.common.ILString;

/**
 * Adapter for LStrings with a map of strings as multi language string
 */
class LStringAdapter implements ILString {

	/**
	 * Map provider
	 */
	private final IStringMapProvider provider;

	/**
	 * Language entry
	 */
	private String lang;

	/**
	 * Create adapter
	 * @param provider provider for map
	 * @param lang language of entry
	 */
	LStringAdapter( IStringMapProvider provider, String lang ) {
		super();
		this.provider = provider;
		this.lang = lang;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLang() {
		return this.lang;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		if ( MapUtils.isNotEmpty( this.provider.getMap() ) ){
			return this.provider.getMap().get( this.lang );
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLang( String newLang ) {
		String oldVal = MapUtils.getString( this.provider.getMap(), this.lang );
		if ( oldVal != null ){
			this.provider.getMap().remove( this.lang );
			this.provider.getMap().put( newLang, oldVal );
		}
		this.lang = newLang;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue( String value ) {
		if ( this.provider.getMap() != null ){
			this.provider.getMap().put( this.lang, value );
		}
	}

}
