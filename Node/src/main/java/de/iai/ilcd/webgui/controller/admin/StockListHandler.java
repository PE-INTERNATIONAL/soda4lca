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

package de.iai.ilcd.webgui.controller.admin;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.ObjectUtils;
import org.primefaces.model.SortOrder;

import de.iai.ilcd.model.dao.CommonDataStockDao;
import de.iai.ilcd.model.datastock.AbstractDataStock;
import de.iai.ilcd.security.StockAccessRight;
import de.iai.ilcd.security.StockAccessRightDao;
import de.iai.ilcd.util.SodaUtil;
import de.iai.ilcd.webgui.controller.DirtyFlagBean;

/**
 * Admin handler for stock list
 */
@ManagedBean
@ViewScoped
public class StockListHandler extends AbstractAdminListHandler<AbstractDataStock> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -8296677018448858250L;

	/**
	 * Dirty flag bean
	 */
	@ManagedProperty( value = "#{dirty}" )
	private DirtyFlagBean dirty;

	/**
	 * DAO for model access
	 */
	private final CommonDataStockDao dao = new CommonDataStockDao();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long loadElementCount() {
		return this.dao.getAllCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void postConstruct() {
	}

	/**
	 * Set the selected stocks.
	 * <p>
	 * <b>Do not replace</b> by {@link AbstractAdminListHandler#setSelectedItems(Object[]) setSelectedItems} in Facelets
	 * (see it's documentation for the reason)
	 * </p>
	 * 
	 * @param selected
	 *            selected root data stocks
	 */
	public void setSelectedStocks( AbstractDataStock[] selected ) {
		super.setSelectedItems( selected );
	}

	/**
	 * Get the selected stocks.
	 * <p>
	 * <b>Do not replace</b> by {@link AbstractAdminListHandler#getSelectedItems() getSelectedItems} in Facelets (see
	 * it's documentation for the reason)
	 * </p>
	 * 
	 * @return selected root data stocks
	 */
	public AbstractDataStock[] getSelectedStocks() {
		return super.getSelectedItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSelected() {
		final AbstractDataStock[] selected = this.getSelectedItems();
		if ( selected == null ) {
			return;
		}

		StockAccessRightDao sarDao = new StockAccessRightDao();
		for ( AbstractDataStock rds : selected ) {
			// Default root stock not deletable
			if ( ObjectUtils.equals( rds.getId(), SodaUtil.DEFAULT_ROOTSTOCK_ID ) ) {
				continue;	// not selectable in facelet and not deletable (double check)
			}
			List<StockAccessRight> sars = sarDao.get( rds );
			try {
				sarDao.remove( sars );
				this.dao.remove( rds );
				this.addI18NFacesMessage( "facesMsg.removeSuccess", FacesMessage.SEVERITY_INFO, rds.getLongTitle().getDefaultValue() + " (" + rds.getName()
						+ ")" );
			}
			catch ( Exception e ) {
				this.addI18NFacesMessage( "facesMsg.removeError", FacesMessage.SEVERITY_ERROR, rds.getLongTitle().getDefaultValue() + " (" + rds.getName()
						+ ")" );
			}
		}
		this.dirty.stockModified();
		this.clearSelection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractDataStock> lazyLoad( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
		return this.dao.get( first, pageSize );
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

}
