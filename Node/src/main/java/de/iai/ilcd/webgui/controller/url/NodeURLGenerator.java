package de.iai.ilcd.webgui.controller.url;

/**
 * URL Generator for node links
 */
public class NodeURLGenerator extends AbstractURLGenerator {

	/**
	 * Initialize node URL generator
	 * 
	 * @param urlBean
	 *            the URL generator bean
	 */
	public NodeURLGenerator( URLGeneratorBean urlBean ) {
		super( urlBean );
	}

	/**
	 * Get the URL to create new node (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getNew() {
		return this.getNew( this.getCurrentStockName() );

	}

	/**
	 * Get the URL to create new node
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getNew( String stockName ) {
		return "/admin/newNode.xhtml" + super.getStockURLParam( stockName, true );
	}

	/**
	 * Get the URL to show node list (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getShowList() {
		return this.getShowList( this.getCurrentStockName() );

	}

	/**
	 * Get the URL to edit node
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( String stockName ) {
		return "/admin/showNode.xhtml" + super.getStockURLParam( stockName, true );
	}

	/**
	 * Get the URL to edit node (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getEdit() {
		return this.getEdit( this.getCurrentStockName() );

	}

	/**
	 * Get the URL to show node list
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getShowList( String stockName ) {
		return "/admin/manageNodeList.xhtml" + super.getStockURLParam( stockName, true );
	}

	/**
	 * Get the URL to show node info (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getNodeInfo() {
		return this.getNodeInfo( this.getCurrentStockName() );

	}

	/**
	 * Get the URL to show node info
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getNodeInfo( String stockName ) {
		return "/admin/showNodeInfo.xhtml" + super.getStockURLParam( stockName, true );
	}

}
