package de.iai.ilcd.model.datastock;

import java.text.Collator;

/**
 * Utility class to store {@link DataStock} meta data in order to prevent having a list of all data stocks where
 * accidentally lazy loading of one or more lists
 * of data sets (40k flows on ELCD max) was triggered.
 */
public final class DataStockMetaData implements IDataStockMetaData {

	/**
	 * ID of the data stock
	 */
	private final Long id;

	/**
	 * Name of the data stock
	 */
	private final String name;

	/**
	 * Long title of the data stock
	 */
	private final String longTitle;

	/**
	 * Is root data stock flag
	 */
	private final boolean root;

	/**
	 * Create a meta data storage object for data stocks
	 * 
	 * @param ads
	 *            data stock to store meta data from
	 * @param root
	 *            root data stock flag
	 */
	public DataStockMetaData( AbstractDataStock ads ) {
		this.id = ads.getId();
		this.name = ads.getName();
		this.longTitle = ads.getLongTitle().getDefaultValue();
		this.root = ads.isRoot();
	}

	/**
	 * Get the ID of the represented stock
	 * 
	 * @return the id of the represented stock
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Get the name of the represented stock
	 * 
	 * @return the name of the represented stock
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Get the long title of the represented stock
	 * 
	 * @return the long title of the represented stock
	 */
	@Override
	public String getLongTitleValue() {
		return this.longTitle;
	}

	/**
	 * Determine if stock is a root data stock
	 * 
	 * @return <code>true</code> if stock is a root data stock, <code>false</code> otherwise
	 */
	@Override
	public boolean isRoot() {
		return this.root;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo( IDataStockMetaData o ) {
		return Collator.getInstance().compare( this.getName(), o.getName() );
	}
}