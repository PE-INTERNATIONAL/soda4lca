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

import de.fzk.iai.ilcd.service.client.impl.vo.types.common.GlobalReferenceType;
import de.fzk.iai.ilcd.service.model.common.IGlobalReference;

/**
 * Adapter for global reference
 */
public class GlobalReferenceAdapter extends GlobalReferenceType {

	/**
	 * Create the adapter.
	 * 
	 * @param adaptee
	 *            instance to adapt
	 */
	public GlobalReferenceAdapter( IGlobalReference reference ) {
		if ( reference != null ) {
			this.setHref( reference.getHref() );
			this.setType( reference.getType() );
			this.setUri( reference.getUri() );
			this.setVersion( reference.getVersionAsString() );

			this.setShortDescription( new LStringAdapter( reference.getShortDescription() ).getLStrings() );
		}
	}

	/**
	 * Copy global references via adapter
	 * 
	 * @param src
	 *            source list
	 * @param dst
	 *            destination list
	 */
	public static void copyGlobalReferences( List<IGlobalReference> src, List<IGlobalReference> dst ) {
		if ( src != null && dst != null ) {
			for ( IGlobalReference srcItem : src ) {
				dst.add( new GlobalReferenceAdapter( srcItem ) );
			}
		}
	}
}
