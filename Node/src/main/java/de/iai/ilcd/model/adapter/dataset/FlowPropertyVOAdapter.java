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

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.FlowPropertyDataSetVO;
import de.fzk.iai.ilcd.service.client.impl.vo.types.flowproperty.UnitGroupType;
import de.fzk.iai.ilcd.service.model.IFlowPropertyListVO;
import de.fzk.iai.ilcd.service.model.IFlowPropertyVO;
import de.fzk.iai.ilcd.service.model.flowproperty.IUnitGroupType;
import de.iai.ilcd.model.adapter.GlobalReferenceAdapter;
import de.iai.ilcd.model.adapter.LStringAdapter;

/**
 * Adapter for Flow Properties
 */
public class FlowPropertyVOAdapter extends AbstractDatasetAdapter<FlowPropertyDataSetVO, IFlowPropertyListVO, IFlowPropertyVO> {

	/**
	 * Create adapter for {@link IFlowPropertyListVO flow list value object}
	 * 
	 * @param adaptee
	 *            list value object to adapt
	 */
	public FlowPropertyVOAdapter( IFlowPropertyListVO adaptee ) {
		super( new FlowPropertyDataSetVO(), adaptee );
	}

	/**
	 * Create adapter for the {@link IFlowPropertyVO flow value object}
	 * 
	 * @param adaptee
	 *            value object to adapt
	 */
	public FlowPropertyVOAdapter( IFlowPropertyVO adaptee ) {
		super( new FlowPropertyDataSetVO(), adaptee );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IFlowPropertyListVO src, FlowPropertyDataSetVO dst ) {
		IUnitGroupType details = src.getUnitGroupDetails();

		UnitGroupType newDetails = new UnitGroupType();
		newDetails.setName( src.getUnitGroupDetails().getName().getValue() );
		newDetails.setDefaultUnit( details.getDefaultUnit() );
		newDetails.setHref( details.getHref() );

		LStringAdapter.copyLStrings( details.getName(), newDetails.getName() );

		newDetails.setReference( new GlobalReferenceAdapter( details.getReference() ) );

		dst.setUnitGroupDetails( newDetails );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IFlowPropertyVO src, FlowPropertyDataSetVO dst ) {
		this.copyValues( (IFlowPropertyListVO) src, dst );
		LStringAdapter.copyLStrings( src.getSynonyms(), dst.getSynonyms() );
	}
}
