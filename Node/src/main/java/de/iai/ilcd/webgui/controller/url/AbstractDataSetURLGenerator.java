package de.iai.ilcd.webgui.controller.url;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.webgui.controller.ConfigurationBean;

/**
 * Base implementation for URL generators. Please note that all generated
 * URLs are absolute URLs within context path (start with <code>/</code>).
 * If your element requires context path to be part of the URL, use the
 * context path provided by {@link ConfigurationBean}.
 * 
 * @param <T>
 *            Type to generate URLs for
 */
public abstract class AbstractDataSetURLGenerator<T extends DataSet> extends AbstractURLGenerator {

	/**
	 * Type URL part for being first word
	 */
	private final String typeUrlPartFW;

	/**
	 * Type URL part for not being first word
	 */
	private final String typeUrlPartNFW;

	/**
	 * Resource URL virtual directory
	 */
	private final String resourceDir;

	/**
	 * Create the generator
	 * 
	 * @param typeUrlPart
	 *            type part in URL (upper/lower case first char will be done via conversion)
	 */
	public AbstractDataSetURLGenerator( URLGeneratorBean urlBean, String typeUrlPart, String resourceDir ) {
		this( urlBean, WordUtils.uncapitalize( typeUrlPart ), WordUtils.capitalize( typeUrlPart ), resourceDir );
	}

	/**
	 * Create the generator
	 * 
	 * @param typeUrlPartFW
	 *            type URL part for being first word
	 * @param typeUrlPartNFW
	 *            type URL part for not being first word
	 * @param resourceDir
	 *            resource URL virtual directory
	 */
	public AbstractDataSetURLGenerator( URLGeneratorBean urlBean, String typeUrlPartFW, String typeUrlPartNFW, String resourceDir ) {
		super( urlBean );
		this.typeUrlPartFW = typeUrlPartFW;
		this.typeUrlPartNFW = typeUrlPartNFW;
		this.resourceDir = resourceDir;
	}

	/**
	 * Get the detail URL with {@link #getCurrentStockName() current stock}
	 * 
	 * @param object
	 *            object to generate URL for
	 * @return generated URL
	 */
	public String getDetail( T object ) {
		return this.getDetail( object, this.getCurrentStockName() );
	}

	/**
	 * Get the detail URL
	 * 
	 * @param object
	 *            object to generate URL for
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getDetail( T object, String stockName ) {
		if ( object != null ) {
			return "/show" + this.typeUrlPartNFW + ".xhtml?uuid=" + this.encodeURL( object.getUuidAsString() )
					+ (object.isMostRecentVersion() ? "" : "&version=" + this.encodeURL( object.getDataSetVersion() )) + "&"
					+ this.getStockURLParam( stockName );
		}
		return "/show" + this.typeUrlPartNFW + ".xhtml" + this.getStockURLParam( stockName, true );
	}

	/**
	 * Get the listing URL with {@link #getCurrentStockName() current stock}
	 * 
	 * @return generated URL
	 */
	public String getList() {
		return this.getList( this.getCurrentStockName() );

	}

	/**
	 * Get the listing URL
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getList( String stockName ) {
		return "/" + this.typeUrlPartFW + "List.xhtml" + super.getStockURLParam( stockName, true );
	}

	/**
	 * Get search form URL with {@link #getCurrentStockName() current stock}
	 * 
	 * @return generated URL
	 */
	public String getSearchForm() {
		return this.getSearchForm( this.getCurrentStockName() );
	}

	/**
	 * Get search form URL
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getSearchForm( String stockName ) {
		return "/" + this.typeUrlPartFW + "Search.xhtml" + super.getStockURLParam( stockName, true );
	}

	/* ======================= */
	/* ==== RESOURCE URLS ==== */
	/* ======================= */

	/**
	 * Get resource URL for XML view
	 * 
	 * @param object
	 *            object to generate URL for
	 * @return generated URL
	 */
	public String getResourceDetailXml( T object ) {
		return this.getResourceDetail( object, "xml" );
	}

	/**
	 * Get resource URL for HTML view
	 * 
	 * @param object
	 *            object to generate URL for
	 * @return generated URL
	 */
	public String getResourceDetailHtml( T object ) {
		return this.getResourceDetail( object, "html" );
	}

	/**
	 * Get resource URL for type listing
	 * 
	 * @return generated URL
	 */
	public String getResourceList() {
		return "/resource/" + this.resourceDir + "/";
	}

	/**
	 * Get resource URL for type listing
	 * 
	 * @param startIndex
	 *            start index of listing
	 * @param pageSize
	 *            page size of listing
	 * @return generated URL
	 */
	public String getResourceList( int startIndex, int pageSize ) {
		return this.getResourceList() + "?startIndex=" + Integer.toString( startIndex ) + "&amp;pageSize=" + Integer.toString( pageSize );
	}

	/* ==================== */
	/* ==== ADMIN URLS ==== */
	/* ===================== */

	/**
	 * Get the URL to the list to manage a data set type (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getManageList() {
		return this.getManageList( this.getCurrentStockName() );
	}

	/**
	 * Get the URL to the list to manage a data set type
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getManageList( String stockName ) {
		return "/admin/datasets/manage" + this.typeUrlPartNFW + "List.xhtml" + super.getStockURLParam( stockName, true );
	}

	/* ========================== */
	/* ==== MISC AND HELPERS ==== */
	/* ========================== */

	/**
	 * Get resource URL
	 * 
	 * @param object
	 *            object to generate URL for
	 * @param format
	 *            format (xml or html)
	 * @return generated URL
	 */
	private String getResourceDetail( T object, String format ) {
		if ( object != null ) {
			return "/resource/" + this.resourceDir + "/" + object.getUuidAsString() + "?format=" + (format == null ? "html" : format)
					+ (!StringUtils.isBlank( object.getDataSetVersion() ) ? "&amp;version=" + object.getDataSetVersion() : "");
		}
		return null;
	}

}
