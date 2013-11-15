/**
 * 
 */
package de.iai.ilcd.webgui.controller.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import de.iai.ilcd.model.dao.CommonDataStockDao;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.datastock.AbstractDataStock;
import de.iai.ilcd.model.datastock.DataStockMetaData;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.webgui.controller.DirtyFlagBean;

/**
 * Managed bean for data stock
 */
@SessionScoped
@ManagedBean( name = "availableStocks" )
public class AvailableStockHandler implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -2678136344534345321L;

	/**
	 * The last known time stamp for a stock modification
	 */
	private long lastKnownStockModifiation;

	/**
	 * Dirty flag bean
	 */
	@ManagedProperty( value = "#{dirty}" )
	private DirtyFlagBean dirty;

	/**
	 * List of all stock meta data
	 */
	private List<IDataStockMetaData> allStocksMeta = new ArrayList<IDataStockMetaData>();

	/**
	 * Initialize data stock bean
	 */
	public AvailableStockHandler() {
		this.reloadAllDataStocks();
	}

	/**
	 * Get meta data of all data stocks
	 * 
	 * @return meta data of all data stocks
	 */
	public List<IDataStockMetaData> getAllStocksMeta() {
		if ( this.dirty.isStockModificationDirty( this.lastKnownStockModifiation ) ) {
			this.reloadAllDataStocks();
		}
		return this.allStocksMeta;
	}

	/**
	 * Load all data stocks.
	 */
	public void reloadAllDataStocks() {
		// new list created and assigned at the end in order to
		// prevent ConcurrentModificationExceptions from being
		// thrown if list is cleared / entries added while
		// view is iterating through the list
		List<IDataStockMetaData> meta = new ArrayList<IDataStockMetaData>();

		CommonDataStockDao dao = new CommonDataStockDao();
		List<AbstractDataStock> lstDs;
		Subject currentUser = SecurityUtils.getSubject();
		if ( currentUser == null ) {
			lstDs = dao.getAllReadable( 0, null );
		}
		else {
			String userName = (String) currentUser.getPrincipal();
			if ( userName != null ) {
				UserDao uDao = new UserDao();
				User u = uDao.getUser( userName );
				if ( u != null ) {
					// super admin ==> all
					if ( u.isSuperAdminPermission() ) {
						lstDs = dao.getAll();
					}
					// no super admin ==> all readable for user
					else {
						lstDs = dao.getAllReadable( u );
					}
				}
				// no user (aka guest), all readable for guests
				else {
					lstDs = dao.getAllReadable( 0, null );
				}
			}
			// no user name set => assume guest
			else {
				lstDs = dao.getAllReadable( 0, null );
			}
		}

		for ( AbstractDataStock stock : lstDs ) {
			meta.add( new DataStockMetaData( stock ) );
		}

		this.allStocksMeta = meta;
		this.lastKnownStockModifiation = DirtyFlagBean.getNow();
	}

	/**
	 * Get the dirty flag bean
	 * 
	 * @return dirty flag bean
	 */
	public DirtyFlagBean getDirty() {
		return this.dirty;
	}

	/**
	 * Set the dirty flag bean
	 * 
	 * @param dirty
	 *            the dirty flag bean
	 */
	public void setDirty( DirtyFlagBean dirty ) {
		this.dirty = dirty;
	}

	/**
	 * Get meta data of stock by its name
	 * 
	 * @param name
	 *            name of the stock
	 * @return meta data of stock
	 */
	public IDataStockMetaData getStock( String name ) {
		if ( name == null ) {
			return null;
		}
		for ( IDataStockMetaData meta : this.allStocksMeta ) {
			if ( name.equals( meta.getName() ) ) {
				return meta;
			}
		}
		return null;
	}

}
