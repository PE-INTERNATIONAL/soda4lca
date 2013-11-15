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

package de.iai.ilcd.model.adapter;

import java.util.List;

import de.fzk.iai.ilcd.service.client.impl.vo.types.process.ReviewType;
import de.fzk.iai.ilcd.service.model.process.IReview;

/**
 * Adapter for {@link IReview}
 */
public class ReviewAdapter extends ReviewType {

	/**
	 * Create the adapter
	 * 
	 * @param adaptee
	 *            object to adapt
	 */
	public ReviewAdapter( IReview adaptee ) {
		if ( adaptee != null ) {
			LStringAdapter.copyLStrings( adaptee.getOtherReviewDetails(), this.getOtherReviewDetails() );
			LStringAdapter.copyLStrings( adaptee.getReviewDetails(), this.getReviewDetails() );
			GlobalReferenceAdapter.copyGlobalReferences( adaptee.getReferencesToReviewers(), this.getReferencesToReviewers() );
			ScopeAdapter.copyScopes( adaptee.getScopes(), this.getScopes() );
			DataQualityIndicatorAdapter.copyDataQualityIndicators( adaptee.getDataQualityIndicators(), this.getDataQualityIndicators() );
			this.setType( adaptee.getType() );
		}
	}

	/**
	 * Copy reviewes via adapter
	 * 
	 * @param src
	 *            source set
	 * @param dst
	 *            destination set
	 */
	public static void copyReviews( List<IReview> src, List<IReview> dst ) {
		if ( src != null && dst != null ) {
			for ( IReview srcItem : src ) {
				dst.add( new ReviewAdapter( srcItem ) );
			}
		}
	}

}
