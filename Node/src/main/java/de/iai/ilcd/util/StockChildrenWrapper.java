package de.iai.ilcd.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.primefaces.model.SortOrder;

import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.contact.Contact;
import de.iai.ilcd.model.dao.ContactDao;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.dao.FlowDao;
import de.iai.ilcd.model.dao.FlowPropertyDao;
import de.iai.ilcd.model.dao.LCIAMethodDao;
import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.dao.SourceDao;
import de.iai.ilcd.model.dao.UnitGroupDao;
import de.iai.ilcd.model.datastock.AbstractDataStock;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.flowproperty.FlowProperty;
import de.iai.ilcd.model.lciamethod.LCIAMethod;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.source.Source;
import de.iai.ilcd.model.unitgroup.UnitGroup;
import de.iai.ilcd.persistence.PersistenceUtil;
import de.iai.ilcd.util.DataSetSelectableDataModel.IDataSetLoader;
import de.iai.ilcd.webgui.controller.admin.StockHandler;

/**
 * <p>
 * Lazy data model for children (assigned data sets) of a stock as well as detachment management for the stock.
 * </p>
 * <p>
 * <b>Purpose:</b> In order to required as little code as possible, this lazy model also is responsible for the
 * detachment of it's selected elements from the stock. <br />
 * This way, there is no need for separate detachment methods for each data type and no additional ui:params must be
 * injected into the common stockEntry.xhtml facelet - only the lazy model is required to do all actions.
 * </p>
 * 
 * @param <E>
 *            data set type
 */
public class StockChildrenWrapper<E extends DataSet> implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -6538508118524915048L;

	/**
	 * Parent stock handler
	 */
	private final StockHandler handler;

	/**
	 * Model with current children of data stock
	 */
	private final DataSetSelectableDataModel<E> contentModel;

	/**
	 * Model with potential candidate accessible by current user
	 */
	private final DataSetSelectableDataModel<E> candidateModel;

	/**
	 * Create the lazy data model
	 * 
	 * @param handler
	 *            Parent stock handler
	 * @param contentModel
	 *            model with content
	 * @param candidateModel
	 *            model with candidates
	 */
	public StockChildrenWrapper( StockHandler handler, DataSetSelectableDataModel<E> contentModel, DataSetSelectableDataModel<E> candidateModel ) {
		super();
		this.handler = handler;
		this.contentModel = contentModel;
		this.candidateModel = candidateModel;
	}

	/**
	 * Detach the selected entries from the data stock
	 */
	public void detachSelectedFromStock() {
		this.handler.ensureEntryIsAttachedToEM();
		final AbstractDataStock entry = this.handler.getEntry();
		if ( entry instanceof DataStock ) {
			if ( this.contentModel.getSelected() != null && this.contentModel.getSelected().length > 0 ) {
				EntityManager em = PersistenceUtil.getEntityManager();
				EntityTransaction t = em.getTransaction();
				String dsName = null;
				try {
					t.begin();
					for ( DataSet ds : this.contentModel.getSelected() ) {
						dsName = ds.getName().getDefaultValue();
						ds = this.handler.ensureObjectIsAttachedToEM( ds );
						ds.removeFromDataStock( (DataStock) entry );
						em.merge( ds ); // save new state
					}
					t.commit();
				}
				catch ( Exception e ) {
					this.handler.addI18NFacesMessage( "facesMsg.stock.removeError", FacesMessage.SEVERITY_ERROR, dsName );
					t.rollback();
					return;
				}
				this.handler.mergeStock( entry );
			}
		}
	}

	/**
	 * Attach the selected entries to the data stock
	 */
	public void attachSelectedToStock() {
		this.handler.ensureEntryIsAttachedToEM();
		final AbstractDataStock entry = this.handler.getEntry();
		if ( entry instanceof DataStock ) {
			if ( this.candidateModel.getSelected() != null && this.candidateModel.getSelected().length > 0 ) {
				EntityManager em = PersistenceUtil.getEntityManager();
				EntityTransaction t = em.getTransaction();
				String dsName = null;
				try {
					t.begin();
					for ( DataSet ds : this.candidateModel.getSelected() ) {
						dsName = ds.getName().getDefaultValue();
						ds = this.handler.ensureObjectIsAttachedToEM( ds );
						ds.addToDataStock( (DataStock) entry );
						PersistenceUtil.getEntityManager().merge( ds ); // save new state
					}
					t.commit();
				}
				catch ( Exception e ) {
					this.handler.addI18NFacesMessage( "facesMsg.stock.assignError", FacesMessage.SEVERITY_ERROR, dsName );
					t.rollback();
					return;
				}
				this.handler.mergeStock( entry );
			}
		}
	}

	/**
	 * Get the candidate model (model with all data sets that can be added to the stock by current user)
	 * 
	 * @return candidate model
	 */
	public DataSetSelectableDataModel<E> getCandidateModel() {
		return this.candidateModel;
	}

	/**
	 * Get the content model (model with all data sets already in the stock)
	 * 
	 * @return content model
	 */
	public DataSetSelectableDataModel<E> getContentModel() {
		return this.contentModel;
	}

	/**
	 * Create the stock children wrapper for process data sets
	 * 
	 * @param handler
	 *            the stock handler to use
	 * @return created wrapper
	 */
	public static StockChildrenWrapper<Process> getProcessWrapper( StockHandler handler ) {
		final ProcessDao dao = new ProcessDao();
		final DataSetSelectableDataModel.ProcessSelectableDataModel contentModel = new DataSetSelectableDataModel.ProcessSelectableDataModel( getContentLoader(
				dao, handler ) );
		final DataSetSelectableDataModel.ProcessSelectableDataModel candidateModel = new DataSetSelectableDataModel.ProcessSelectableDataModel(
				getCandidateLoader( dao, handler ) );
		contentModel.setDisableSelection( handler.getEntry().isRoot() );
		return new StockChildrenWrapper<Process>( handler, contentModel, candidateModel );
	}

	/**
	 * Create the stock children wrapper for LCIA method data sets
	 * 
	 * @param handler
	 *            the stock handler to use
	 * @return created wrapper
	 */
	public static StockChildrenWrapper<LCIAMethod> getLCIAMethodWrapper( StockHandler handler ) {
		final LCIAMethodDao dao = new LCIAMethodDao();
		final DataSetSelectableDataModel.LCIAMethodSelectableDataModel contentModel = new DataSetSelectableDataModel.LCIAMethodSelectableDataModel(
				getContentLoader( dao, handler ) );
		final DataSetSelectableDataModel.LCIAMethodSelectableDataModel candidateModel = new DataSetSelectableDataModel.LCIAMethodSelectableDataModel(
				getCandidateLoader( dao, handler ) );
		contentModel.setDisableSelection( handler.getEntry().isRoot() );
		return new StockChildrenWrapper<LCIAMethod>( handler, contentModel, candidateModel );
	}

	/**
	 * Create the stock children wrapper for flow data sets
	 * 
	 * @param handler
	 *            the stock handler to use
	 * @return created wrapper
	 */
	public static StockChildrenWrapper<Flow> getFlowWrapper( StockHandler handler ) {
		final FlowDao dao = new FlowDao();
		final DataSetSelectableDataModel.FlowSelectableDataModel contentModel = new DataSetSelectableDataModel.FlowSelectableDataModel( getContentLoader( dao,
				handler ) );
		final DataSetSelectableDataModel.FlowSelectableDataModel candidateModel = new DataSetSelectableDataModel.FlowSelectableDataModel( getCandidateLoader(
				dao, handler ) );
		contentModel.setDisableSelection( handler.getEntry().isRoot() );
		return new StockChildrenWrapper<Flow>( handler, contentModel, candidateModel );
	}

	/**
	 * Create the stock children wrapper for flow property data sets
	 * 
	 * @param handler
	 *            the stock handler to use
	 * @return created wrapper
	 */
	public static StockChildrenWrapper<FlowProperty> getFlowPropertyWrapper( StockHandler handler ) {
		final FlowPropertyDao dao = new FlowPropertyDao();
		final DataSetSelectableDataModel.FlowPropertySelectableDataModel contentModel = new DataSetSelectableDataModel.FlowPropertySelectableDataModel(
				getContentLoader( dao, handler ) );
		final DataSetSelectableDataModel.FlowPropertySelectableDataModel candidateModel = new DataSetSelectableDataModel.FlowPropertySelectableDataModel(
				getCandidateLoader( dao, handler ) );
		contentModel.setDisableSelection( handler.getEntry().isRoot() );
		return new StockChildrenWrapper<FlowProperty>( handler, contentModel, candidateModel );
	}

	/**
	 * Create the stock children wrapper for unit group data sets
	 * 
	 * @param handler
	 *            the stock handler to use
	 * @return created wrapper
	 */
	public static StockChildrenWrapper<UnitGroup> getUnitGroupWrapper( StockHandler handler ) {
		final UnitGroupDao dao = new UnitGroupDao();
		final DataSetSelectableDataModel.UnitGroupSelectableDataModel contentModel = new DataSetSelectableDataModel.UnitGroupSelectableDataModel(
				getContentLoader( dao, handler ) );
		final DataSetSelectableDataModel.UnitGroupSelectableDataModel candidateModel = new DataSetSelectableDataModel.UnitGroupSelectableDataModel(
				getCandidateLoader( dao, handler ) );
		contentModel.setDisableSelection( handler.getEntry().isRoot() );
		return new StockChildrenWrapper<UnitGroup>( handler, contentModel, candidateModel );
	}

	/**
	 * Create the stock children wrapper for source data sets
	 * 
	 * @param handler
	 *            the stock handler to use
	 * @return created wrapper
	 */
	public static StockChildrenWrapper<Source> getSourceWrapper( StockHandler handler ) {
		final SourceDao dao = new SourceDao();
		final DataSetSelectableDataModel.SourceSelectableDataModel contentModel = new DataSetSelectableDataModel.SourceSelectableDataModel( getContentLoader(
				dao, handler ) );
		final DataSetSelectableDataModel.SourceSelectableDataModel candidateModel = new DataSetSelectableDataModel.SourceSelectableDataModel(
				getCandidateLoader( dao, handler ) );
		contentModel.setDisableSelection( handler.getEntry().isRoot() );
		return new StockChildrenWrapper<Source>( handler, contentModel, candidateModel );
	}

	/**
	 * Create the stock children wrapper for contact data sets
	 * 
	 * @param handler
	 *            the stock handler to use
	 * @return created wrapper
	 */
	public static StockChildrenWrapper<Contact> getContactWrapper( StockHandler handler ) {
		final ContactDao dao = new ContactDao();
		final DataSetSelectableDataModel.ContactSelectableDataModel contentModel = new DataSetSelectableDataModel.ContactSelectableDataModel( getContentLoader(
				dao, handler ) );
		final DataSetSelectableDataModel.ContactSelectableDataModel candidateModel = new DataSetSelectableDataModel.ContactSelectableDataModel(
				getCandidateLoader( dao, handler ) );
		contentModel.setDisableSelection( handler.getEntry().isRoot() );
		return new StockChildrenWrapper<Contact>( handler, contentModel, candidateModel );
	}

	/**
	 * Get a loader for the content (already assigned children) for stock and DAO
	 * 
	 * @param <T>
	 *            type of data set
	 * @param dao
	 *            DAO to load data from
	 * @param handler
	 *            handler to get stock information from
	 * @return created loader
	 */
	private static <T extends DataSet> IDataSetLoader<T> getContentLoader( final DataSetDao<T, ?, ?> dao, StockHandler handler ) {
		final AbstractDataStock entry = handler.getEntry();
		final IDataStockMetaData[] metaArr = entry != null ? new IDataStockMetaData[] { entry } : new IDataStockMetaData[0];

		return new IDataSetLoader<T>() {

			@Override
			public List<T> load( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
				return dao.lsearch( null, first, pageSize, null, false, metaArr );
			}

			@Override
			public long loadCount() {
				return metaArr.length > 0 ? dao.searchResultCount( null, false, metaArr ) : 0;
			}
		};
	}

	/**
	 * Get a loader for the candidates (data sets that can be added to stock by current user) for stock and DAO
	 * 
	 * @param <T>
	 *            type of data set
	 * @param dao
	 *            DAO to load data from
	 * @param handler
	 *            handler to get stock information from
	 * @return created loader
	 */
	private static <T extends DataSet> IDataSetLoader<T> getCandidateLoader( final DataSetDao<T, ?, ?> dao, StockHandler handler ) {
		final AbstractDataStock entry = handler.getEntry();
		final IDataStockMetaData[] candidateStocks = handler.getAvailableStocks().getAllStocksMeta().toArray( new IDataStockMetaData[0] );

		return new IDataSetLoader<T>() {

			@Override
			public List<T> load( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
				return dao.lsearch( null, first, pageSize, null, false, candidateStocks, entry );
			}

			@Override
			public long loadCount() {
				return candidateStocks.length > 0 ? dao.searchResultCount( null, false, candidateStocks, entry ) : 0;
			}
		};
	}

}