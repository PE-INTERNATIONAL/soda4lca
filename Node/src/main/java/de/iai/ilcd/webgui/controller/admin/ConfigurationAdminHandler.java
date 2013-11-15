package de.iai.ilcd.webgui.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.security.SecurityUtil;
import de.iai.ilcd.security.UserAccessBean;
import de.iai.ilcd.util.DBConfigurationUtil;
import de.iai.ilcd.webgui.controller.AbstractHandler;
import de.iai.ilcd.webgui.controller.ConfigurationBean;
import de.iai.ilcd.webgui.controller.ui.AvailableStockHandler;

/**
 * The admin area handler for the application wide configuration
 */
@ViewScoped
@ManagedBean( name = "confAdminHandler" )
public class ConfigurationAdminHandler extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -8363489557091673889L;

	/**
	 * Available stocks bean
	 */
	@ManagedProperty( value = "#{availableStocks}" )
	private AvailableStockHandler availableStocksHandler;

	/**
	 * Stock selection bean
	 */
	@ManagedProperty( value = "#{user}" )
	private UserAccessBean userBean;

	/**
	 * Configuration bean
	 */
	@ManagedProperty( value = "#{conf}" )
	private ConfigurationBean conf;

	/**
	 * Selected stock meta data name
	 */
	private String selectedStockMetaName;

	/**
	 * Stock meta data names
	 */
	private List<String> stockMetaNames;

	/**
	 * Called after dependency injection
	 */
	@PostConstruct
	public void init() {
		SecurityUtil.assertSuperAdminPermission();

		final long defaultId = this.conf.getDefaultDataStockId();
		this.stockMetaNames = new ArrayList<String>();
		for ( IDataStockMetaData dsmd : this.availableStocksHandler.getAllStocksMeta() ) {
			this.stockMetaNames.add( dsmd.getName() );
			if ( defaultId == dsmd.getId() ) {
				this.selectedStockMetaName = dsmd.getName();
			}
		}
	}

	/**
	 * Save configuration to DB
	 */
	public void saveConfiguration() {
		IDataStockMetaData selectedStockMeta = this.availableStocksHandler.getStock( this.selectedStockMetaName );
		if ( selectedStockMeta != null ) {
			if ( !DBConfigurationUtil.setDefaultDataStock( selectedStockMeta.getId(), selectedStockMeta.isRoot() ) ) {
				this.addI18NFacesMessage( "facesMsg.config.saveError", FacesMessage.SEVERITY_ERROR );
				return;
			}
			this.conf.setDefaultDataStockId( selectedStockMeta.getId() );
			this.conf.setDefaultDataStockRoot( selectedStockMeta.isRoot() );
			this.addI18NFacesMessage( "facesMsg.config.saveSuccess", FacesMessage.SEVERITY_INFO );
		}
		else {
			this.addI18NFacesMessage( "facesMsg.stock.noSelected", FacesMessage.SEVERITY_ERROR );
		}
	}

	/**
	 * Get all stock names
	 * 
	 * @return stock names list
	 */
	public List<String> getAllStocksMetaNames() {
		return this.stockMetaNames;
	}

	/**
	 * Get the selected stock meta data name
	 * 
	 * @return selected stock meta data name
	 */
	public String getSelectedStockMetaName() {
		return this.selectedStockMetaName;
	}

	/**
	 * Set the stock meta data name
	 * 
	 * @param selectedStockMetaName
	 *            stock meta name data to set
	 */
	public void setSelectedStockMetaName( String selectedStockMetaName ) {
		this.selectedStockMetaName = selectedStockMetaName;
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
	 * Get configuration bean
	 * 
	 * @return configuration bean
	 */
	public ConfigurationBean getConf() {
		return this.conf;
	}

	/**
	 * Set configuration bean
	 * 
	 * @param conf
	 *            configuration bean to set
	 */
	public void setConf( ConfigurationBean conf ) {
		this.conf = conf;
	}

	/**
	 * Set user bean
	 * 
	 * @param userBean
	 *            user bean to set
	 */
	public void setUserBean( UserAccessBean userBean ) {
		this.userBean = userBean;
	}

	/**
	 * Get user bean
	 * 
	 * @return user bean
	 */
	public UserAccessBean getUserBean() {
		return this.userBean;
	}

}
