/**
 * 
 */
package de.iai.ilcd.webgui.controller.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.AuthorizationException;

import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.webgui.controller.ConfigurationBean;

/**
 * Managed bean for data stock
 */
@ViewScoped
@ManagedBean( name = "stockSelection" )
public class StockSelectionHandler implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -2678136415735063421L;

	/**
	 * Query string to manipulate for other stock
	 */
	private String queryString;

	/**
	 * Stock selection bean
	 */
	@ManagedProperty( value = "#{availableStocks}" )
	private AvailableStockHandler availableStocksHandler;

	/**
	 * Configuration bean
	 */
	@ManagedProperty( value = "#{conf}" )
	private ConfigurationBean conf;

	/**
	 * Current stock meta data
	 */
	private IDataStockMetaData currentStock;

	/**
	 * Initialize data stock bean
	 */
	public StockSelectionHandler() {
		// do nothing here that requires the injected beans, use init (because of the managed property)!!

		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
		this.queryString = servletRequest.getQueryString();

	}

	@PostConstruct
	public void init() {
		try {
			this.setCurrentStock( FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( "stock" ) );
		}
		catch ( Exception e ) {
			if ( e instanceof AuthorizationException ) {
				throw (AuthorizationException) e;
			}
			this.setDefaultStock();
		}
	}

	/**
	 * Get the current stock meta data
	 * 
	 * @return current stock meta data
	 */
	public IDataStockMetaData getCurrentStock() {
		return this.currentStock;
	}

	/**
	 * Get the current stock meta data as array with one entry
	 * 
	 * @return current stock meta data as array with one entry
	 */
	public IDataStockMetaData[] getCurrentStockAsArray() {
		return new IDataStockMetaData[] { this.currentStock };
	}

	/**
	 * Set the current stock via DB id
	 * 
	 * @param id
	 *            database id
	 */
	public void setCurrentStock( long id ) {
		for ( IDataStockMetaData m : this.availableStocksHandler.getAllStocksMeta() ) {
			if ( m.getId() == id ) {
				this.currentStock = m;
				return;
			}
		}
		this.setDefaultStock();
	}

	/**
	 * Set the current stock via DB id
	 * 
	 * @param name
	 *            stock name
	 */
	public void setCurrentStock( String name ) {
		if ( StringUtils.isBlank( name ) ) {
			this.setDefaultStock();
			return;
		}
		for ( IDataStockMetaData m : this.availableStocksHandler.getAllStocksMeta() ) {
			if ( name.equals( m.getName() ) ) {
				this.currentStock = m;
				return;
			}
		}
		this.setDefaultStock();
	}

	/**
	 * Set the name of the current stock
	 * 
	 * @param name
	 *            name to set
	 */
	public void setCurrentStockName( String name ) {
		this.setCurrentStock( name );
	}

	/**
	 * Get the name of the current stock
	 * 
	 * @return name of the current stock
	 */
	public String getCurrentStockName() {
		return this.currentStock != null ? this.currentStock.getName() : "";
	}

	/**
	 * Set the handler for the available stocks
	 * 
	 * @param availableStocksHandler
	 *            the handler for the available stocks to set
	 */
	public void setAvailableStocksHandler( AvailableStockHandler availableStocksHandler ) {
		this.availableStocksHandler = availableStocksHandler;
	}

	/**
	 * Get the handler for the available stocks
	 * 
	 * @return handler for the available stocks
	 */
	public AvailableStockHandler getAvailableStocksHandler() {
		return this.availableStocksHandler;
	}

	/**
	 * Called by selectOneMenu after setting new stock value.
	 * Redirect is done by {@link NavigationHandler}.
	 */
	public void navigate() {
		FacesContext context = FacesContext.getCurrentInstance();
		StringBuffer sb = new StringBuffer( context.getViewRoot().getViewId() );
		sb.append( "?" );
		sb.append( this.queryString );
		sb.append( "&faces-redirect=true" );

		NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
		navigationHandler.handleNavigation( context, null, sb.toString() );
	}

	/**
	 * Method to wait for new values from stock selection selectOneMenu
	 * 
	 * @param event
	 *            event with old and new values (note: stock name is passed)
	 */
	public void stockChangeEventHandler( ValueChangeEvent event ) {
		final String stockPattern = "stock=" + event.getOldValue();
		final String stockReplacement = "stock=" + event.getNewValue();
		// no query string (e.g. index.xhtml) ==> create query string
		if ( StringUtils.isBlank( this.queryString ) ) {
			this.queryString = stockReplacement;
		}
		// old query string contains a stock ==> replace with new stock
		else if ( this.queryString.contains( stockPattern ) ) {
			this.queryString = this.queryString.replace( stockPattern, stockReplacement );
		}
		// query string is there, but contains no stock ==> append new stock
		else {
			this.queryString += "&" + stockReplacement;
		}
	}

	/**
	 * Set the default stock.
	 */
	private void setDefaultStock() {
		final long defId = this.conf.getDefaultDataStockId();
		for ( IDataStockMetaData m : this.availableStocksHandler.getAllStocksMeta() ) {
			if ( defId == m.getId() ) {
				this.currentStock = m;
				return;
			}
		}
		if ( !this.availableStocksHandler.getAllStocksMeta().isEmpty() ) {
			this.currentStock = this.availableStocksHandler.getAllStocksMeta().get( 0 );
			return;
		}
	}

	/**
	 * Get the configuration bean
	 * 
	 * @return the configuration bean
	 */
	public ConfigurationBean getConf() {
		return this.conf;
	}

	/**
	 * Set the configuration bean
	 * 
	 * @param conf
	 *            the configuration bean to set
	 */
	public void setConf( ConfigurationBean conf ) {
		this.conf = conf;
	}

}
