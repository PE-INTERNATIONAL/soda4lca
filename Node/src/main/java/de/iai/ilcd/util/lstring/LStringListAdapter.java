package de.iai.ilcd.util.lstring;

import java.util.AbstractList;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import de.fzk.iai.ilcd.service.model.common.ILString;

/**
 * Adapter to simulate a list of ILStrings based on a
 * {@link IStringMapProvider map provider}.
 */
class LStringListAdapter extends AbstractList<ILString> {

	/**
	 * Provider for map
	 */
	private final IStringMapProvider provider;

	/**
	 * Create the adapter
	 * @param provider provider for data
	 */
	LStringListAdapter( IStringMapProvider provider ) {
		if ( provider == null ){
			throw new NullPointerException( "Provider must not be null!" );
		}
		this.provider = provider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public ILString get( int index ) {
		if ( MapUtils.isNotEmpty( this.provider.getMap() ) ){
			Entry<String,String> entry = (Entry<String, String>) CollectionUtils.get( this.provider.getMap(), index );
			if ( entry != null ){
				return new LStringAdapter( this.provider, entry.getKey() );
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		if ( MapUtils.isNotEmpty( this.provider.getMap() ) ){
			return this.provider.getMap().size();
		}
		return 0;
	}

}
