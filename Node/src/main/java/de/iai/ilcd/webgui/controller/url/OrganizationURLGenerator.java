package de.iai.ilcd.webgui.controller.url;

/**
 * URL Generator for organization links
 */
public class OrganizationURLGenerator extends AbstractURLGenerator {

	/**
	 * Initialize organization URL generator
	 * 
	 * @param urlBean
	 *            the URL generator bean
	 */
	public OrganizationURLGenerator( URLGeneratorBean urlBean ) {
		super( urlBean );
	}

	/**
	 * Get the URL to create new organization (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getNew() {
		return this.getNew( this.getCurrentStockName() );

	}

	/**
	 * Get the URL to create new organization
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getNew( String stockName ) {
		return "/admin/newOrganization.xhtml" + super.getStockURLParam( stockName, true );
	}

	/**
	 * Get the URL to edit organization
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( String stockName ) {
		return this.getEdit( null, stockName );
	}

	/**
	 * Get the URL to edit organization (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getEdit() {
		return this.getEdit( null, this.getCurrentStockName() );
	}

	/**
	 * Get the URL to edit organization
	 * 
	 * @param id
	 *            ID of the organization to edit
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( Long id, String stockName ) {
		return "/admin/editOrganization.xhtml?" + (id != null ? "orgId=" + id.toString() + "&" : "") + super.getStockURLParam( stockName );
	}

	/**
	 * Get the URL to edit organization (on current stock name)
	 * 
	 * @param id
	 *            ID of the organization to edit
	 * @return generated URL
	 */
	public String getEdit( Long id ) {
		return this.getEdit( id, this.getCurrentStockName() );
	}

	/**
	 * Get the URL to the list to manage an organization (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getManageList() {
		return this.getManageList( this.getCurrentStockName() );
	}

	/**
	 * Get the URL to the list to manage an organization
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getManageList( String stockName ) {
		return "/admin/manageOrganizationList.xhtml" + super.getStockURLParam( stockName, true );
	}

}
