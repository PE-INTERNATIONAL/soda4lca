package de.iai.ilcd.webgui.controller.admin;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.webgui.controller.AbstractHandler;

/**
 * Base class for all lists manages beans
 * 
 * @param <T>
 *            list item type
 */
public abstract class AbstractAdminListHandler<T> extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 6415662747167696159L;

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger( AbstractAdminListHandler.class );

	/**
	 * Lazy model
	 */
	private LazyDataModel<T> lazyModel = new AdminLazyDataModel();

	/**
	 * Selected items
	 */
	private T[] selectedItems;

	/**
	 * Create the handler
	 */
	public AbstractAdminListHandler() {
	}

	/**
	 * Load count of elements
	 * 
	 * @return the count of elements
	 */
	protected abstract long loadElementCount();

	/**
	 * Do everything here that depends on injected beans
	 */
	protected abstract void postConstruct();

	/**
	 * Delete selected items
	 */
	public abstract void deleteSelected();

	/**
	 * Do the lazy loading
	 * 
	 * @param first
	 *            first index
	 * @param pageSize
	 *            page size
	 * @param sortField
	 *            sort field
	 * @param sortOrder
	 *            sort oder
	 * @param filters
	 *            filters
	 * @return loaded data
	 */
	public abstract List<T> lazyLoad( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters );

	/**
	 * Reload count for model
	 */
	protected final void reloadCount() {
		long rowCount = this.loadElementCount();
		this.lazyModel.setRowCount( (int) rowCount );
	}

	/**
	 * Called after dependency injection
	 */
	@PostConstruct
	public final void init() {
		this.postConstruct();
		this.reloadCount();
	}

	/**
	 * Get the lazy model
	 * 
	 * @return lazy model
	 */
	public final LazyDataModel<T> getLazyModel() {
		return this.lazyModel;
	}

	/**
	 * Set the lazy model
	 * 
	 * @param lazyModel
	 *            model to set
	 */
	public final void setLazyModel( LazyDataModel<T> lazyModel ) {
		this.lazyModel = lazyModel;
	}

	/**
	 * <p>
	 * Get the selected items
	 * <p>
	 * <p>
	 * <b>Please see note on {@link #setSelectedItems(Object[])}</b>
	 * </p>
	 * 
	 * @return the selected items
	 * @see #setSelectedItems(Object[])
	 */
	public final T[] getSelectedItems() {
		return this.selectedItems;
	}

	/**
	 * Clear the selection (via setting <code>null</code>)
	 */
	protected final void clearSelection() {
		this.setSelectedItems( null );
	}

	/**
	 * <p>
	 * Set the selected items
	 * <p>
	 * <p>
	 * <b>Important:</b> Do not use directly in Facelets! The generic type <code>T</code> is being converted to object
	 * by the compiler! Therefore the Facelet is trying to set an Object array instead of typed array. This results in a
	 * {@link ClassCastException} once {@link #getSelectedItems()} is being used.<br />
	 * &rArr; Write a delegation method with your type
	 * </p>
	 * 
	 * @param selectedItems
	 *            items to set
	 */
	protected final void setSelectedItems( T[] selectedItems ) {
		this.selectedItems = selectedItems;
	}

	/**
	 * Convenience method for state of the action button
	 * 
	 * @return <code>true</code> if action button shall be disabled, <code>false</code> otherwise
	 */
	public boolean isNothingSelected() {
		return this.getSelectedItems() == null || this.getSelectedItems().length == 0;
	}

	/**
	 * Lazy model
	 */
	public class AdminLazyDataModel extends LazyDataModel<T> {

		/**
		 * Serialization ID
		 */
		private static final long serialVersionUID = -8147455033133411665L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<T> load( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
			AbstractAdminListHandler.logger.info( "Loading the lazy data between {0} and {1}", new Object[] { first, (first + pageSize) } );

			List<T> lazyData = AbstractAdminListHandler.this.lazyLoad( first, pageSize, sortField, sortOrder, filters );

			long rowCount = AbstractAdminListHandler.this.loadElementCount();
			this.setRowCount( (int) rowCount );
			return lazyData;
		}

		/**
		 * Convenience method for state of the action button
		 * 
		 * @return <code>true</code> if action button shall be disabled, <code>false</code> otherwise
		 * @see AbstractAdminListHandler#isNothingSelected()
		 */
		public boolean isNothingSelected() {
			return AbstractAdminListHandler.this.isNothingSelected();
		}
	}
}
