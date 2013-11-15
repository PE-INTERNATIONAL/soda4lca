package de.iai.ilcd.webgui.controller.url;

/**
 * URL Generator for stock links
 */
public class StockURLGenerator extends AbstractURLGenerator {

	/**
	 * Flag to indicate if generator shall generate URLs for root stocks
	 */
	private final boolean isRoot;

	/**
	 * Initialize stock URL generator
	 * 
	 * @param urlBean
	 *            the URL generator bean
	 */
	public StockURLGenerator( URLGeneratorBean urlBean ) {
		this( urlBean, false );
	}

	/**
	 * Initialize (root) stock URL generator
	 * 
	 * @param urlBean
	 *            the URL generator bean
	 * @param isRoot
	 *            <code>true</code> if stock is root stock, else <code>false</code>
	 */
	public StockURLGenerator( URLGeneratorBean urlBean, boolean isRoot ) {
		super( urlBean );
		this.isRoot = isRoot;
	}

	/**
	 * Get the URL to create a new stock (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getNew() {
		return this.getNew( this.getCurrentStockName() );
	}

	/**
	 * Get the URL to create a new stock
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getNew( String stockName ) {
		return "/admin/stocks/newStock.xhtml?stockType=" + (this.isRoot ? "rds" : "ds") + "&" + super.getStockURLParam( stockName );
	}

	/**
	 * Get the URL to edit stock
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( String stockName ) {
		return this.getEdit( null, stockName );
	}

	/**
	 * Get the URL to edit stock (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getEdit() {
		return this.getEdit( null, this.getCurrentStockName() );
	}

	/**
	 * Get the URL to edit stock
	 * 
	 * @param uid
	 *            ID of the stock to edit
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( Long uid, String stockName ) {
		return "/admin/stocks/editStock.xhtml?" + (uid != null ? "stockId=" + uid.toString() + "&" : "") + super.getStockURLParam( stockName );
	}

	/**
	 * Get the URL to edit stock (on current stock name)
	 * 
	 * @param uid
	 *            ID of the stock to edit
	 * @return generated URL
	 */
	public String getEdit( Long uid ) {
		return this.getEdit( uid, this.getCurrentStockName() );
	}

	/**
	 * Get the URL to the list to manage a stock (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getManageList() {
		return this.getManageList( this.getCurrentStockName() );
	}

	/**
	 * Get the URL to the list to manage a stock
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getManageList( String stockName ) {
		return "/admin/stocks/manageStockList.xhtml" + super.getStockURLParam( stockName, true );
	}

	// /**
	// * Convenience method to check if this stock is a root stock (then add &quot;Root&quot;) or not (add
	// &quot;&quot;).
	// *
	// * @return &quot;Root&quot; if this stock is a root stock, else blank String
	// */
	// private String addRootIfIsRoot() {
	// if( this.isRoot ) {
	// return "Root";
	// }
	// else {
	// return "";
	// }
	// }
}
