/*******************************************************************************
 * Copyright (c) 2011 Karlsruhe Institute of Technology (KIT) - Institute for
 * Applied Computer Science (IAI).
 * 
 * This file is part of soda4LCA - the Service-Oriented Life Cycle Data Store.
 * 
 * soda4LCA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * soda4LCA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with soda4LCA. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package de.iai.ilcd.webgui.controller.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;

import org.apache.velocity.tools.generic.ValueParser;

import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.security.SecurityUtil;

/**
 * <p>
 * Handler for DataSet lists
 * </p>
 * <p>
 * <strong>Please note:</strong> There will be <u>no filtering</u> until {@link #doFilter()} was called!
 * </p>
 * 
 * @param <T>
 *            type of model objects that this handler provides
 * @param <D>
 *            type of data set DAO
 */
public abstract class AbstractDataSetsHandler<T extends DataSet, D extends DataSetDao<T, ?, ?>> implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 4275620873048386353L;

	/**
	 * The lazy model for data sets
	 */
	private DataSetLazyDataModel<T> lazyDataModel;

	/**
	 * The map / value parser with the filter
	 */
	private final Map<String, Object> parameterMap = new HashMap<String, Object>();

	/**
	 * DAO instance for data retrieval
	 */
	private final D daoInstance;

	/**
	 * View ID of data table for fix
	 */
	private final String dataTableViewId;

	/**
	 * Stock selection handler
	 */
	@ManagedProperty( value = "#{stockSelection}" )
	private StockSelectionHandler stockSelection;

	/**
	 * Class type of data
	 */
	private final Class<T> type;

	/**
	 * Initialize the handler
	 * 
	 * @param type
	 *            type of model objects to access
	 * @param daoObject
	 *            matching DAO for data access
	 */
	public AbstractDataSetsHandler( Class<T> type, D daoInstance ) {
		this( type, daoInstance, null );
	}

	/**
	 * Initialize the handler. Please note that this implementation <strong>only loads the most recent version of a data
	 * set</strong>
	 * 
	 * TODO this is required for the primefaces datatable issue workaround in {@link #doFilter()}
	 * 
	 * @param type
	 *            type of model objects to access
	 * @param daoObject
	 *            matching DAO for data access
	 * @param dataTableViewId
	 *            id of the data table in view
	 */
	public AbstractDataSetsHandler( Class<T> type, D daoInstance, String dataTableViewId ) {
		this.type = type;
		this.daoInstance = daoInstance;
		this.dataTableViewId = dataTableViewId;
	}

	/**
	 * Will be called after dependency injection
	 */
	@PostConstruct
	public final void init() {
		SecurityUtil.assertCanRead( this.stockSelection.getCurrentStock() );

		this.lazyDataModel = new DataSetLazyDataModel<T>( this.type, this.daoInstance, true, this.stockSelection.getCurrentStockAsArray() );
		this.postConstruct();
	}

	/**
	 * Method called after dependency injection by {@link #init()}. Override to perform actions.
	 */
	protected void postConstruct() {

	}

	/**
	 * Set filter, empty strings will be considered as indicator to remove filter
	 * 
	 * @param key
	 *            key of filter
	 * @param value
	 *            value of filter
	 */
	protected void setFilter( String key, String value ) {
		if ( value != null && !value.trim().isEmpty() ) {
			this.parameterMap.put( key, value );
		}
		else {
			this.parameterMap.remove( key );
		}
	}

	/**
	 * Set filter, null means: remove filter
	 * 
	 * @param key
	 *            key of filter
	 * @param value
	 *            value of filter
	 */
	protected void setFilter( String key, Object value ) {
		if ( value != null ) {
			this.parameterMap.put( key, value );
		}
		else {
			this.parameterMap.remove( key );
		}
	}

	/**
	 * Get the value of a filter as string
	 * 
	 * @param key
	 *            key in map to get filter from
	 * @return the value of the filter (<code>null</code> if not available, no exception is being thrown)
	 */
	protected String getFilter( String key ) {
		Object o = this.parameterMap.get( key );
		return o != null ? o.toString() : null;
	}

	/**
	 * Get the value of a filter as object
	 * 
	 * @param key
	 *            key in map to get filter from
	 * @return the value of the filter (<code>null</code> if not available, no exception is being thrown)
	 */
	protected Object getFilterObject( String key ) {
		return this.parameterMap.get( key );
	}

	/**
	 * Get the string array value of a filter
	 * 
	 * @param key
	 *            key in map to get filter from
	 * @return the string array value of the filter (<code>null</code> if not available or no string array, no exception
	 *         is being thrown)
	 */
	protected String[] getFilterStringArr( String key ) {
		Object o = this.parameterMap.get( key );
		return o instanceof String[] ? (String[]) o : null;
	}

	/**
	 * Get the string array value of a filter
	 * 
	 * @param key
	 *            key in map to get filter from
	 * @return the string array value of the filter (<code>null</code> if not available or no string array, no exception
	 *         is being thrown)
	 */
	protected Boolean getFilterBoolean( String key ) {
		Object o = this.parameterMap.get( key );
		return o instanceof Boolean ? (Boolean) o : null;
	}

	/**
	 * Get the lazy data model for this handler
	 * 
	 * @return lazy data model for this handler
	 */
	public DataSetLazyDataModel<T> getLazyDataModel() {
		return this.lazyDataModel;
	}

	/**
	 * Get the DAO instance
	 * 
	 * @return DAO instance
	 */
	protected D getDaoInstance() {
		return this.daoInstance;
	}

	/**
	 * Do the filtering
	 * 
	 * TODO remove workaround for primefaces datatable issue
	 */
	public final void doFilter() {
		// Apply data table fix for previously empty results (but only of view id given)
		final boolean doFix = this.lazyDataModel.getRowCount() == 0 && this.dataTableViewId != null;

		// load the lazy data
		this.lazyDataModel.setParams( new ValueParser( this.parameterMap ) );

		// apply fix if required
		if ( doFix ) {
			ListUtils.fixDataTable( this.dataTableViewId );
		}
		// logger.info("hier in doFilter mit params = " + params);
		// ProcessDao dao = new ProcessDao();
		// int rowCount = (int) dao.searchResultCount(params);
		// logger.info("count=" + rowCount);
		// processLazyDataModel.setRowCount((int) rowCount);

	}

	/**
	 * Get the current parameter map as value parser
	 * 
	 * @return current parameter map as value parser
	 */
	public ValueParser getParameterMapAsValueParser() {
		return new ValueParser( this.parameterMap );
	}

	/**
	 * Set the stock selection handler
	 * 
	 * @param stockSelection
	 *            stock selection handler to set
	 */
	public void setStockSelection( StockSelectionHandler stockSelection ) {
		this.stockSelection = stockSelection;
	}

	/**
	 * Get the stock selection handler
	 * 
	 * @return stock selection handler
	 */
	public StockSelectionHandler getStockSelection() {
		return this.stockSelection;
	}
}
