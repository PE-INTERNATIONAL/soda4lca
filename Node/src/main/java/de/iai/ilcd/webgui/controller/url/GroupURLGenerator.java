package de.iai.ilcd.webgui.controller.url;

/**
 * URL Generator for group links
 */
public class GroupURLGenerator extends AbstractURLGenerator {

	/**
	 * Initialize group URL generator
	 * 
	 * @param urlBean
	 *            the URL generator bean
	 */
	public GroupURLGenerator( URLGeneratorBean urlBean ) {
		super( urlBean );
	}

	/**
	 * Get the URL to create new group (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getNew() {
		return this.getNew( this.getCurrentStockName() );

	}

	/**
	 * Get the URL to edit group
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( String stockName ) {
		return this.getEdit( null, stockName );
	}

	/**
	 * Get the URL to edit group (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getEdit() {
		return this.getEdit( null, this.getCurrentStockName() );
	}

	/**
	 * Get the URL to edit group
	 * 
	 * @param uid
	 *            ID of the group to edit
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( Long uid, String stockName ) {
		return "/admin/showGroup.xhtml?" + (uid != null ? "groupId=" + uid.toString() + "&" : "") + super.getStockURLParam( stockName );
	}

	/**
	 * Get the URL to edit group (on current stock name)
	 * 
	 * @param uid
	 *            ID of the group to edit
	 * @return generated URL
	 */
	public String getEdit( Long uid ) {
		return this.getEdit( uid, this.getCurrentStockName() );
	}

	/**
	 * Get the URL to create new group
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getNew( String stockName ) {
		return "/admin/newGroup.xhtml" + super.getStockURLParam( stockName, true );
	}

	/**
	 * Get the URL to show group list (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getShowList() {
		return this.getShowList( this.getCurrentStockName() );
	}

	/**
	 * Get the URL to show group list
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getShowList( String stockName ) {
		return "/admin/manageGroupList.xhtml" + super.getStockURLParam( stockName, true );
	}

}
