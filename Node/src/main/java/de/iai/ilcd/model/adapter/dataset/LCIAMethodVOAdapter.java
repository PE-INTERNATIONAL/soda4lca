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

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.LCIAMethodDataSetVO;
import de.fzk.iai.ilcd.service.client.impl.vo.types.lciamethod.TimeInformationType;
import de.fzk.iai.ilcd.service.model.IFlowPropertyVO;
import de.fzk.iai.ilcd.service.model.ILCIAMethodListVO;
import de.fzk.iai.ilcd.service.model.ILCIAMethodVO;
import de.iai.ilcd.model.adapter.LStringAdapter;

/**
 * Adapter for LCIA methods
 */
public class LCIAMethodVOAdapter extends AbstractDatasetAdapter<LCIAMethodDataSetVO, ILCIAMethodListVO, ILCIAMethodVO> {

	/**
	 * Create adapter for {@link ILCIAMethodListVO LCIA method list value object}
	 * 
	 * @param adaptee
	 *            list value object to adapt
	 */
	public LCIAMethodVOAdapter( ILCIAMethodListVO adaptee ) {
		super( new LCIAMethodDataSetVO(), adaptee );
	}

	/**
	 * Create adapter for the {@link IFlowPropertyVO LCIA method value object}
	 * 
	 * @param adaptee
	 *            value object to adapt
	 */
	public LCIAMethodVOAdapter( ILCIAMethodVO adaptee ) {
		super( new LCIAMethodDataSetVO(), adaptee );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( ILCIAMethodListVO src, LCIAMethodDataSetVO dst ) {
		TimeInformationType time = new TimeInformationType();

		LStringAdapter.copyLStrings( src.getTimeInformation().getDuration(), time.getDuration() );
		dst.setTimeInformation( time );

		dst.setType( src.getType() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( ILCIAMethodVO src, LCIAMethodDataSetVO dst ) {
		this.copyValues( (ILCIAMethodListVO) src, dst );

		dst.setImpactIndicator( src.getImpactIndicator() );
		dst.getAreaOfProtection().addAll( src.getAreaOfProtection() );
		dst.getImpactCategory().addAll( src.getImpactCategory() );
		dst.getMethodology().addAll( src.getMethodology() );
	}

}
