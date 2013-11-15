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

package de.iai.ilcd.model.adapter.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.DataSetVO;
import de.fzk.iai.ilcd.service.model.IDataSetListVO;
import de.fzk.iai.ilcd.service.model.IDataSetVO;
import de.fzk.iai.ilcd.service.model.common.ILString;
import de.iai.ilcd.model.adapter.ClassificationAdapter;
import de.iai.ilcd.model.adapter.LStringAdapter;

public abstract class AbstractDatasetAdapter<T extends DataSetVO, LVO extends IDataSetListVO, VO extends IDataSetVO> {

	private static final Logger logger = LoggerFactory.getLogger( AbstractDatasetAdapter.class );

	private final T dataset;

	private AbstractDatasetAdapter( T adapterObject ) {
		this.dataset = adapterObject;
	}

	public AbstractDatasetAdapter( T adapterObject, LVO adaptee ) {
		this( adapterObject );
		this.copyValues( adaptee );
		this.copyValues( adaptee, this.dataset );
	}

	public AbstractDatasetAdapter( T adapterObject, VO adaptee ) {
		this( adapterObject );
		this.copyValues( adaptee );
		this.copyValues( adaptee, this.dataset );
	}

	/**
	 * Copy the values from the value <b>AND</b> the list value object
	 * to the adapter data set.
	 * 
	 * @param src
	 *            source object
	 * @param dst
	 *            destination data set
	 */
	protected abstract void copyValues( VO src, T dst );

	/**
	 * Copy the values from the list value object
	 * to the adapter data set
	 * 
	 * @param src
	 *            source object
	 * @param dst
	 *            destination data set
	 */
	protected abstract void copyValues( LVO src, T dst );

	private final void copyValues( IDataSetVO src ) {
		this.copyValues( (IDataSetListVO) src );
		LStringAdapter.copyLStrings( src.getDescription(), this.dataset.getDescription() );
	}

	private final void copyValues( IDataSetListVO src ) {

		this.dataset.setUuid( src.getUuidAsString() );
		this.dataset.setDataSetVersion( src.getDataSetVersion() );
		this.dataset.setPermanentUri( src.getPermanentUri() );

		// TODO copy all names, this doesn't work yet since adaptee.getName().getLStrings() is empty
		this.dataset.setName( src.getName().getValue() );
		// for (ILString l : adaptee.getName().getLStrings()) {
		// this.dataset.setName(l.getLang(), l.getValue());
		// }

		try {
			this.dataset.setClassification( new ClassificationAdapter( src.getClassification() ) );
		}
		catch ( Exception e ) {
		}

		this.dataset.setSourceId( src.getSourceId() );
		this.dataset.setHref( src.getHref() );
	}

	public T getDataSet() {
		return dataset;
	}
}
