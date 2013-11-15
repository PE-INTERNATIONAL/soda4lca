package de.iai.ilcd.util;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.contact.Contact;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.flowproperty.FlowProperty;
import de.iai.ilcd.model.lciamethod.LCIAMethod;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.source.Source;
import de.iai.ilcd.model.unitgroup.UnitGroup;

/**
 * Data Model with lazy support and selection feature
 * 
 * @param <T>
 *            type of data set
 */
public abstract class DataSetSelectableDataModel<T extends DataSet> extends LazyDataModel<T> {

	/**
	 * Loader for lazy data for data sets
	 * 
	 * @param <T>
	 *            type of data set
	 */
	public interface IDataSetLoader<T extends DataSet> {

		/**
		 * Load the lazy data
		 * 
		 * @param first
		 *            first index
		 * @param pageSize
		 *            max count of elements to load
		 * @param sortField
		 *            field to sort by
		 * @param sortOrder
		 *            sort order
		 * @param filters
		 *            filters for search etc.
		 * @return list with lazy loaded data
		 */
		public List<T> load( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters );

		/**
		 * Load count of elements
		 * 
		 * @return count of elements
		 */
		public long loadCount();
	}

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -4940798106696383971L;

	/**
	 * Loader for content
	 */
	private final IDataSetLoader<T> loader;

	/**
	 * 
	 */
	private boolean disableSelection = false;

	/**
	 * Create the model
	 * 
	 * @param loader
	 *            loader for data
	 */
	public DataSetSelectableDataModel( IDataSetLoader<T> loader ) {
		this.loader = loader;
		this.loadRowCount();
	}

	/**
	 * Disable the selection (meaning: {@link #isNothingSelected()} will always return <code>true</code>)
	 * 
	 * @param disableSelection
	 *            flag to set the disable
	 */
	public void setDisableSelection( boolean disableSelection ) {
		this.disableSelection = disableSelection;
	}

	/**
	 * Selected items
	 */
	private T[] selected;

	/**
	 * Set selected, tunnel with typed delegate model because generic type will be reduced to {@link Object}
	 * 
	 * @param selected
	 *            selected items
	 */
	public void setSelected( T[] selected ) {
		this.selected = selected;
	}

	/**
	 * Get selected, tunnel with typed delegate model because generic type will be reduced to {@link Object}
	 * 
	 * @return selected items
	 */
	public T[] getSelected() {
		return this.selected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> load( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
		this.loadRowCount();
		return this.loader.load( first, pageSize, sortField, sortOrder, filters );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRowIndex( int rowIndex ) {
		// workaround, see http://code.google.com/p/primefaces/issues/detail?id=1544
		if ( this.getPageSize() != 0 ) {
			super.setRowIndex( rowIndex );
		}
	}

	/**
	 * Load row count
	 */
	private void loadRowCount() {
		this.setRowCount( (int) this.loader.loadCount() );
	}

	/**
	 * Determine if nothing is selected
	 * 
	 * @return <code>true</code> if noting is selected, <code>false</code> otherwise
	 */
	public boolean isNothingSelected() {
		return this.disableSelection || (this.selected == null || this.selected.length == 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageSize( int pageSize ) {
		super.setPageSize( pageSize );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPageSize() {
		return super.getPageSize();
	}

	/**
	 * Data model with selection capabilities for {@link Process}
	 */
	public static class ProcessSelectableDataModel extends DataSetSelectableDataModel<Process> {

		/**
		 * Serialization ID
		 */
		private static final long serialVersionUID = -5338129763233588799L;

		/**
		 * Create the model
		 * 
		 * @param loader
		 *            data loader
		 */
		public ProcessSelectableDataModel( IDataSetLoader<Process> loader ) {
			super( loader );
		}

		/**
		 * Get the selected items
		 * 
		 * @return selected items
		 */
		public Process[] getSelectedItems() {
			return this.getSelected();
		}

		/**
		 * Set the selected items
		 * 
		 * @param items
		 *            selected items
		 */
		public void setSelectedItems( Process[] items ) {
			super.setSelected( items );
		}

	}

	/**
	 * Data model with selection capabilities for {@link LCIAMethod}
	 */
	public static class LCIAMethodSelectableDataModel extends DataSetSelectableDataModel<LCIAMethod> {

		/**
		 * Serialization ID
		 */
		private static final long serialVersionUID = -25866843798800048L;

		/**
		 * Create the model
		 * 
		 * @param loader
		 *            data loader
		 */
		public LCIAMethodSelectableDataModel( IDataSetLoader<LCIAMethod> loader ) {
			super( loader );
		}

		/**
		 * Get the selected items
		 * 
		 * @return selected items
		 */
		public LCIAMethod[] getSelectedItems() {
			return this.getSelected();
		}

		/**
		 * Set the selected items
		 * 
		 * @param items
		 *            selected items
		 */
		public void setSelectedItems( LCIAMethod[] items ) {
			super.setSelected( items );
		}

	}

	/**
	 * Data model with selection capabilities for {@link Flow}
	 */
	public static class FlowSelectableDataModel extends DataSetSelectableDataModel<Flow> {

		/**
		 * Serialization ID
		 */
		private static final long serialVersionUID = 3383790123810846611L;

		/**
		 * Create the model
		 * 
		 * @param loader
		 *            data loader
		 */
		public FlowSelectableDataModel( IDataSetLoader<Flow> loader ) {
			super( loader );
		}

		/**
		 * Get the selected items
		 * 
		 * @return selected items
		 */
		public Flow[] getSelectedItems() {
			return this.getSelected();
		}

		/**
		 * Set the selected items
		 * 
		 * @param items
		 *            selected items
		 */
		public void setSelectedItems( Flow[] items ) {
			super.setSelected( items );
		}

	}

	/**
	 * Data model with selection capabilities for {@link FlowProperty}
	 */
	public static class FlowPropertySelectableDataModel extends DataSetSelectableDataModel<FlowProperty> {

		/**
		 * Serialization ID
		 */
		private static final long serialVersionUID = -3455733938391343362L;

		/**
		 * Create the model
		 * 
		 * @param loader
		 *            data loader
		 */
		public FlowPropertySelectableDataModel( IDataSetLoader<FlowProperty> loader ) {
			super( loader );
		}

		/**
		 * Get the selected items
		 * 
		 * @return selected items
		 */
		public FlowProperty[] getSelectedItems() {
			return this.getSelected();
		}

		/**
		 * Set the selected items
		 * 
		 * @param items
		 *            selected items
		 */
		public void setSelectedItems( FlowProperty[] items ) {
			super.setSelected( items );
		}

	}

	/**
	 * Data model with selection capabilities for {@link UnitGroup}
	 */
	public static class UnitGroupSelectableDataModel extends DataSetSelectableDataModel<UnitGroup> {

		/**
		 * Serialization ID
		 */
		private static final long serialVersionUID = 8009685951105561608L;

		/**
		 * Create the model
		 * 
		 * @param loader
		 *            data loader
		 */
		public UnitGroupSelectableDataModel( IDataSetLoader<UnitGroup> loader ) {
			super( loader );
		}

		/**
		 * Get the selected items
		 * 
		 * @return selected items
		 */
		public UnitGroup[] getSelectedItems() {
			return this.getSelected();
		}

		/**
		 * Set the selected items
		 * 
		 * @param items
		 *            selected items
		 */
		public void setSelectedItems( UnitGroup[] items ) {
			super.setSelected( items );
		}

	}

	/**
	 * Data model with selection capabilities for {@link Contact}
	 */
	public static class ContactSelectableDataModel extends DataSetSelectableDataModel<Contact> {

		/**
		 * Serialization ID
		 */
		private static final long serialVersionUID = -2551320755608301233L;

		/**
		 * Create the model
		 * 
		 * @param loader
		 *            data loader
		 */
		public ContactSelectableDataModel( IDataSetLoader<Contact> loader ) {
			super( loader );
		}

		/**
		 * Get the selected items
		 * 
		 * @return selected items
		 */
		public Contact[] getSelectedItems() {
			return this.getSelected();
		}

		/**
		 * Set the selected items
		 * 
		 * @param items
		 *            selected items
		 */
		public void setSelectedItems( Contact[] items ) {
			super.setSelected( items );
		}

	}

	/**
	 * Data model with selection capabilities for {@link Source}
	 */
	public static class SourceSelectableDataModel extends DataSetSelectableDataModel<Source> {

		/**
		 * Serialization ID
		 */
		private static final long serialVersionUID = -3986950833614527784L;

		/**
		 * Create the model
		 * 
		 * @param loader
		 *            data loader
		 */
		public SourceSelectableDataModel( IDataSetLoader<Source> loader ) {
			super( loader );
		}

		/**
		 * Get the selected items
		 * 
		 * @return selected items
		 */
		public Source[] getSelectedItems() {
			return this.getSelected();
		}

		/**
		 * Set the selected items
		 * 
		 * @param items
		 *            selected items
		 */
		public void setSelectedItems( Source[] items ) {
			super.setSelected( items );
		}

	}
}
