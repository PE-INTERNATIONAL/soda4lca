package de.iai.ilcd.webgui.controller.admin;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;

import org.apache.velocity.tools.generic.ValueParser;
import org.primefaces.model.SortOrder;

import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.webgui.controller.ui.StockSelectionHandler;

/**
 * Base class for all data set lists
 * 
 * @param <T>
 *            type of data set
 */
public abstract class AbstractDataSetListHandler<T extends DataSet> extends AbstractAdminListHandler<T> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 6411162747167696159L;

	/**
	 * Parameters
	 */
	protected ValueParser params;

	/**
	 * Data access object to use
	 */
	private final DataSetDao<T, ?, ?> dao;

	/**
	 * Type class
	 */
	private final Class<T> type;

	/**
	 * Stock selection handler
	 */
	@ManagedProperty( value = "#{stockSelection}" )
	private StockSelectionHandler stockSelection;

	/**
	 * Create the handler
	 * 
	 * @param type
	 *            Type class
	 * @param dao
	 *            Data access object to use
	 */
	public AbstractDataSetListHandler( Class<T> type, DataSetDao<T, ?, ?> dao ) {
		this.dao = dao;
		this.type = type;
		this.params = new ValueParser();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void postConstruct() {
	}

	/**
	 * Get the display string for faces message. May be overridden by sub classes.
	 * 
	 * @param obj
	 *            object to use
	 * @return display string
	 */
	protected String getDisplayString( T obj ) {
		try {
			return obj.getName().getDefaultValue();
		}
		catch ( Exception e ) {
			return "";
		}
	}

	/**
	 * Get the data access object
	 * 
	 * @return data access object
	 */
	protected final DataSetDao<T, ?, ?> getDao() {
		return this.dao;
	}

	/**
	 * Delete selected items
	 */
	@Override
	public final void deleteSelected() {
		final T[] selectedItems = this.getSelectedItems();
		if ( this.getSelectedItems() == null ) {
			return;
		}

		for ( T item : selectedItems ) {
			try {
				this.dao.remove( item );
				this.addI18NFacesMessage( "facesMsg.removeSuccess", FacesMessage.SEVERITY_INFO, this.getDisplayString( item ) );
			}
			catch ( Exception ex ) {
				this.addI18NFacesMessage( "facesMsg.removeError", FacesMessage.SEVERITY_ERROR, this.getDisplayString( item ) );
			}
		}
		this.clearSelection();
		this.reloadCount();
	}

	/**
	 * Set the stock selection handler
	 * 
	 * @param stockSelection
	 *            stock selection handler to set
	 */
	public final void setStockSelection( StockSelectionHandler stockSelection ) {
		this.stockSelection = stockSelection;
	}

	/**
	 * Get the stock selection handler
	 * 
	 * @return stock selection handler
	 */
	public final StockSelectionHandler getStockSelection() {
		return this.stockSelection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long loadElementCount() {
		return this.dao.searchResultCount( this.params, false, this.stockSelection.getCurrentStockAsArray() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> lazyLoad( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
		return this.dao.search( this.type, this.params, first, pageSize, sortField, false, this.stockSelection.getCurrentStockAsArray() );
	}
}
