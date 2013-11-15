package de.iai.ilcd.model.datastock;

/**
 * Utility class to store {@link DataStock} meta data in order to prevent having a list of all data stocks where
 * accidentally lazy loading of one or more lists
 * of data sets (40k flows on ELCD max) was triggered.
 */
public interface IDataStockMetaData extends Comparable<IDataStockMetaData> {

	/**
	 * ID of default data stock
	 */
	public final static long DEFAULT_DATASTOCK_ID = 1L;

	/**
	 * Get the ID of the represented stock
	 * 
	 * @return the id of the represented stock
	 */
	public Long getId();

	/**
	 * Get the name of the represented stock
	 * 
	 * @return the name of the represented stock
	 */
	public String getName();

	/**
	 * Get the long title of the represented stock
	 * 
	 * @return the long title of the represented stock
	 */
	public String getLongTitleValue();

	/**
	 * Determine if stock is a root data stock
	 * 
	 * @return <code>true</code> if stock is a root data stock, <code>false</code> otherwise
	 */
	public boolean isRoot();

}