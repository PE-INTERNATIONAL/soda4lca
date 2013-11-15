package de.iai.ilcd.webgui.controller.url;

/**
 * URL Generator for data links
 */
public class DataURLGenerator extends AbstractURLGenerator {

	/**
	 * Initialize data URL generator
	 * 
	 * @param urlBean
	 *            the URL generator bean
	 */
	public DataURLGenerator( URLGeneratorBean urlBean ) {
		super( urlBean );
	}

	/**
	 * Get the URL to import and upload data (sets) (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getImportUpload() {
		return this.getImportUpload( this.getCurrentStockName() );
	}

	/**
	 * Get the URL to import and upload data (sets)
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getImportUpload( String stockName ) {
		return "/admin/importUpload.xhtml" + super.getStockURLParam( stockName, true );
	}

	/**
	 * Get the URL to export data (base) (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getExport() {
		return this.getExport( this.getCurrentStockName() );
	}

	/**
	 * Get the URL to export data (base)
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getExport( String stockName ) {
		return "/admin/dataExport.xhtml" + super.getStockURLParam( stockName, true );
	}

}
