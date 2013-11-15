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

import java.util.List;
import java.util.Map;

import org.apache.velocity.tools.generic.ValueParser;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.ProcessDataSetVO;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.datastock.IDataStockMetaData;
import de.iai.ilcd.model.utils.DistributedSearchLog;

/**
 * Lazy data model for JSF for all data set types
 * 
 * @param <T>
 *            type of data set
 */
public class DataSetLazyDataModel<T extends DataSet> extends LazyDataModel<T> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 651369617999373671L;

	/**
	 * Type of data set
	 */
	private final Class<T> type;

	/**
	 * Load most recent version only
	 */
	private final boolean mostRecentOnly;

	/**
	 * DAO for data set type
	 */
	private final DataSetDao<T, ?, ?> daoObject;

	/**
	 * the parameter value parser
	 */
	private ValueParser params;

	/**
	 * the stocks
	 */
	private IDataStockMetaData[] stocks;

	private DistributedSearchLog log;

	/**
	 * Initialize lazy data model, package wide visibility for the handler classes
	 * 
	 * @param type
	 *            type of data set
	 * @param daoObject
	 *            DAO for data set
	 * @param mostRecentOnly
	 *            Load most recent version only
	 * @param stocks
	 *            stocks
	 */
	DataSetLazyDataModel( Class<T> type, DataSetDao<T, ?, ?> daoObject, boolean mostRecentOnly, IDataStockMetaData[] stocks ) {
		if ( type == null ) {
			throw new IllegalArgumentException( "Type for lazy data model must not be null" );
		}
		if ( daoObject == null ) {
			throw new IllegalArgumentException( "Dao object for lazy data model must not be null" );
		}
		this.stocks = stocks;
		this.mostRecentOnly = mostRecentOnly;
		this.params = new ValueParser();
		this.type = type;
		this.daoObject = daoObject;
		this.setRowCount( (int) this.daoObject.searchResultCount( this.params, this.mostRecentOnly, stocks ) );
	}

	/**
	 * Get the parameter
	 * 
	 * @return
	 */
	public ValueParser getParams() {
		return this.params;
	}

	/**
	 * Set the parameter
	 * 
	 * @param params
	 */
	public void setParams( ValueParser params ) {
		this.params = params;
	}

	@Override
	public List<T> load( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
		this.setRowCount( (int) this.daoObject.searchResultCount( this.params, this.mostRecentOnly, this.stocks ) );

		// SortCriteria sortCriteria = SortCriteria.fromValue(sortField);

		// boolean sortOrder: true = asc / false = desc (old, Primefaces 2.x)
		// enum SortOrder: ASCENDING, DESCENDING, UNSORTED (new, Primefaces 3.x)
		// return this.daoObject.search(this.type, this.params, first, pageSize, sortField, sortOrder,
		// this.mostRecentOnly, this.stocks, this.log);
		List result = this.daoObject.searchDist( this.type, this.params, first, pageSize, sortField, sortOrder, this.mostRecentOnly, this.stocks, null,
				this.log );

		// TODO: this solution is lacking some elegance
		if ( result != null ) {
			int counter = 0;
			for ( Object entity : result ) {
				if ( entity instanceof ProcessDataSetVO ) {
					if ( !((ProcessDataSetVO) entity).getSourceId().equals( ConfigurationService.INSTANCE.getNodeId() ) ) {
						counter++;
					}
				}
			}
			this.setRowCount( this.getRowCount() + counter );
		}
		return result;
	}

	public DistributedSearchLog getLog() {
		return this.log;
	}

	public void setLog( DistributedSearchLog log ) {
		this.log = log;
	}
}
