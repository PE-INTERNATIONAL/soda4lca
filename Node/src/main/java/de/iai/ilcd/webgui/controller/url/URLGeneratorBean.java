package de.iai.ilcd.webgui.controller.url;

import java.io.Serializable;
import java.net.URLEncoder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.iai.ilcd.webgui.controller.ui.StockSelectionHandler;

/**
 * Bean for the generation of URLs for the Facelets
 */
@ViewScoped
@ManagedBean( name = "url" )
public class URLGeneratorBean implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 3903685753048662243L;

	/**
	 * {@value #FACES_REDIRECT_TRUE}
	 */
	public final static String FACES_REDIRECT_TRUE = "faces-redirect=true";

	/**
	 * Stock selection bean
	 */
	@ManagedProperty( value = "#{stockSelection}" )
	private StockSelectionHandler stockSelectionHandler;

	/**
	 * Generator for Process URLs
	 */
	private final ProcessURLGenerator process = new ProcessURLGenerator( this );

	/**
	 * Generator for Flow URLs
	 */
	private final FlowURLGenerator flow = new FlowURLGenerator( this );

	/**
	 * Generator for LCIAMethod URLs
	 */
	private final LCIAMethodURLGenerator lciaMethod = new LCIAMethodURLGenerator( this );

	/**
	 * Generator for Flow Property URLs
	 */
	private final FlowPropertyURLGenerator flowProperty = new FlowPropertyURLGenerator( this );

	/**
	 * Generator for Unit Group URLs
	 */
	private final UnitGroupURLGenerator unitGroup = new UnitGroupURLGenerator( this );

	/**
	 * Generator for Source URLs
	 */
	private final SourceURLGenerator source = new SourceURLGenerator( this );

	/**
	 * Generator for Contact URLs
	 */
	private final ContactURLGenerator contact = new ContactURLGenerator( this );

	/**
	 * Generator for User URLs
	 */
	private final UserURLGenerator user = new UserURLGenerator( this );

	/**
	 * Generator for Group URLs
	 */
	private final GroupURLGenerator group = new GroupURLGenerator( this );

	/**
	 * Generator for Node URLs
	 */
	private final NodeURLGenerator node = new NodeURLGenerator( this );

	/**
	 * Generator for Organization URLs
	 */
	private final OrganizationURLGenerator org = new OrganizationURLGenerator( this );

	/**
	 * Generator for Root Stock URLs
	 */
	private final StockURLGenerator rootStock = new StockURLGenerator( this, true );

	/**
	 * Generator for Stock URLs
	 */
	private final StockURLGenerator stock = new StockURLGenerator( this, false );

	/**
	 * Generator for Data URLs
	 */
	private final DataURLGenerator data = new DataURLGenerator( this );

	/**
	 * Current stock name
	 */
	private String currentStockName;

	/**
	 * Get the stock selection bean
	 * 
	 * @return stock selection bean
	 */
	public StockSelectionHandler getStockSelectionHandler() {
		return this.stockSelectionHandler;
	}

	/**
	 * Set the stock selection bean
	 * 
	 * @param stockSelectionHandler
	 *            stock selection bean to set
	 */
	public void setStockSelectionHandler( StockSelectionHandler stockSelectionHandler ) {
		this.stockSelectionHandler = stockSelectionHandler;
		this.currentStockName = stockSelectionHandler != null ? (stockSelectionHandler.getCurrentStock() != null ? stockSelectionHandler.getCurrentStock()
				.getName() : null) : null;
	}

	/**
	 * Get the process URL generator
	 * 
	 * @return process URL generator
	 */
	public ProcessURLGenerator getProcess() {
		return this.process;
	}

	/**
	 * Get the flow property URL generator
	 * 
	 * @return flow property URL generator
	 */
	public FlowURLGenerator getFlow() {
		return this.flow;
	}

	/**
	 * Get the LCIA method URL generator
	 * 
	 * @return LCIA method URL generator
	 */
	public LCIAMethodURLGenerator getLciaMethod() {
		return this.lciaMethod;
	}

	/**
	 * Get the flow property URL generator
	 * 
	 * @return flow property URL generator
	 */
	public FlowPropertyURLGenerator getFlowProperty() {
		return this.flowProperty;
	}

	/**
	 * Get the unit group URL generator
	 * 
	 * @return unit group URL generator
	 */
	public UnitGroupURLGenerator getUnitGroup() {
		return this.unitGroup;
	}

	/**
	 * Get the source URL generator
	 * 
	 * @return source URL generator
	 */
	public SourceURLGenerator getSource() {
		return this.source;
	}

	/**
	 * Get the contact URL generator
	 * 
	 * @return contact URL generator
	 */
	public ContactURLGenerator getContact() {
		return this.contact;
	}

	/**
	 * Get the user URL generator
	 * 
	 * @return user URL generator
	 */
	public UserURLGenerator getUser() {
		return this.user;
	}

	/**
	 * Get the group URL generator
	 * 
	 * @return group URL generator
	 */
	public GroupURLGenerator getGroup() {
		return this.group;
	}

	/**
	 * Get the node URL generator
	 * 
	 * @return node URL generator
	 */
	public NodeURLGenerator getNode() {
		return this.node;
	}

	/**
	 * Get the config URL (on current stock name)
	 * 
	 * @return config URL
	 */
	public String getConfig() {
		return this.getConfig( this.getCurrentStockName() );
	}

	/**
	 * Get the config URL
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return config URL
	 */
	public String getConfig( String stockName ) {
		return "/admin/configuration.xhtml" + this.getStockURLParam( stockName, true );
	}

	/**
	 * Get the root stock URL generator
	 * 
	 * @return root stock URL generator
	 */
	public StockURLGenerator getRootStock() {
		return this.rootStock;
	}

	/**
	 * Get the stock URL generator
	 * 
	 * @return stock URL generator
	 */
	public StockURLGenerator getStock() {
		return this.stock;
	}

	/**
	 * Get the URL parameter for data
	 * 
	 * @return the data
	 */
	public DataURLGenerator getData() {
		return this.data;
	}

	/**
	 * Get the URL parameter for organization
	 * 
	 * @return the organization
	 */
	public OrganizationURLGenerator getOrg() {
		return this.org;
	}

	/**
	 * Get the index URL (on current stock name)
	 * 
	 * @return index URL
	 */
	public String getIndex() {
		return this.getIndex( this.getCurrentStockName() );
	}

	/**
	 * Get the index URL
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return index URL
	 */
	public String getIndex( String stockName ) {
		return "/index.xhtml" + this.getStockURLParam( stockName, true );
	}

	/**
	 * Get the admin index URL (on current stock name)
	 * 
	 * @return admin index URL
	 */
	public String getAdminIndex() {
		return this.getAdminIndex( this.getCurrentStockName() );
	}

	/**
	 * Get the admin index URL
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return admin index URL
	 */
	public String getAdminIndex( String stockName ) {
		return "/admin/index.xhtml" + this.getStockURLParam( stockName, true );
	}

	/**
	 * Get current stock name
	 * 
	 * @return current stock name
	 */
	public String getCurrentStockName() {
		return this.currentStockName;
	}

	/**
	 * Encode an URL parameter
	 * 
	 * @param s
	 *            string to encode
	 * @return encoded string
	 */
	final String encodeURL( String s ) {
		try {
			return URLEncoder.encode( s, "UTF-8" );
		}
		catch ( Exception e ) {
			return s;
		}
	}

	/**
	 * Get the URL parameter for stock
	 * 
	 * @param stockName
	 *            name of stock
	 * @return URL parameter for stock as String
	 */
	final String getStockURLParam( String stockName, boolean includeQuestionMark ) {
		if ( stockName != null ) {
			return (includeQuestionMark ? "?" : "") + "stock=" + this.encodeURL( stockName );
		}
		return "";
	}

}
