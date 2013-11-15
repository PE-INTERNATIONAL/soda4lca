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

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.FlowDataSetVO;
import de.fzk.iai.ilcd.service.client.impl.vo.types.flow.ReferenceFlowPropertyType;
import de.fzk.iai.ilcd.service.model.IFlowListVO;
import de.fzk.iai.ilcd.service.model.IFlowVO;
import de.iai.ilcd.model.adapter.FlowCategorizationAdapter;
import de.iai.ilcd.model.adapter.GlobalReferenceAdapter;
import de.iai.ilcd.model.adapter.LStringAdapter;

/**
 * Adapter for Flows
 */
public class FlowVOAdapter extends AbstractDatasetAdapter<FlowDataSetVO, IFlowListVO, IFlowVO> {

	/**
	 * Create adapter for {@link IFlowListVO flow list value object}
	 * 
	 * @param adaptee
	 *            list value object to adapt
	 */
	public FlowVOAdapter( IFlowListVO adaptee ) {
		super( new FlowDataSetVO(), adaptee );
	}

	/**
	 * Create adapter for the {@link IFlowVO flow value object}
	 * 
	 * @param adaptee
	 *            value object to adapt
	 */
	public FlowVOAdapter( IFlowVO adaptee ) {
		super( new FlowDataSetVO(), adaptee );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IFlowListVO src, FlowDataSetVO dst ) {
		try {
			dst.setFlowCategorization( new FlowCategorizationAdapter( src.getFlowCategorization() ) );
		}
		catch ( Exception e ) {
			// Ignore
		}

		ReferenceFlowPropertyType flowProperty = new ReferenceFlowPropertyType();

		if ( src.getReferenceFlowProperty() != null ) {
			flowProperty.setDefaultUnit( src.getReferenceFlowProperty().getDefaultUnit() );
			flowProperty.setReference( new GlobalReferenceAdapter( src.getReferenceFlowProperty().getReference() ) );
			flowProperty.setHref( src.getReferenceFlowProperty().getHref() );
			LStringAdapter.copyLStrings( src.getReferenceFlowProperty().getName(), flowProperty.getName() );
		}

		dst.setReferenceFlowProperty( flowProperty );

		dst.setType( src.getType() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IFlowVO src, FlowDataSetVO dst ) {
		this.copyValues( (IFlowListVO) src, dst );
		LStringAdapter.copyLStrings( src.getSynonyms(), dst.getSynonyms() );
		dst.setCasNumber( src.getCasNumber() );
		dst.setSumFormula( src.getSumFormula() );
	}
}
