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

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.UnitGroupDataSetVO;
import de.fzk.iai.ilcd.service.model.ISourceListVO;
import de.fzk.iai.ilcd.service.model.ISourceVO;
import de.fzk.iai.ilcd.service.model.IUnitGroupListVO;
import de.fzk.iai.ilcd.service.model.IUnitGroupVO;

/**
 * Adapter for Sources
 */
public class UnitGroupVOAdapter extends AbstractDatasetAdapter<UnitGroupDataSetVO, IUnitGroupListVO, IUnitGroupVO> {

	/**
	 * Create adapter for {@link ISourceListVO unit group list value object}
	 * 
	 * @param adaptee
	 *            list value object to adapt
	 */
	public UnitGroupVOAdapter( IUnitGroupListVO adaptee ) {
		super( new UnitGroupDataSetVO(), adaptee );
	}

	/**
	 * Create adapter for the {@link ISourceVO unit group value object}
	 * 
	 * @param adaptee
	 *            value object to adapt
	 */
	public UnitGroupVOAdapter( IUnitGroupVO adaptee ) {
		super( new UnitGroupDataSetVO(), adaptee );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IUnitGroupListVO src, UnitGroupDataSetVO dst ) {
		dst.setDefaultUnit( src.getDefaultUnit() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IUnitGroupVO src, UnitGroupDataSetVO dst ) {
		this.copyValues( (IUnitGroupListVO) src, dst );
	}

}
