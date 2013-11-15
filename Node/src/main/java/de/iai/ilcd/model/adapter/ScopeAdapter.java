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
import java.util.Set;

import de.fzk.iai.ilcd.service.client.impl.vo.types.process.ReviewType;
import de.fzk.iai.ilcd.service.model.enums.MethodOfReviewValue;
import de.fzk.iai.ilcd.service.model.process.IScope;

/**
 * Adapter for {@link ReviewType.Scope}
 */
public class ScopeAdapter extends ReviewType.Scope {

	/**
	 * Create the adapter
	 * 
	 * @param adaptee
	 *            object to adapt
	 */
	public ScopeAdapter( IScope adaptee ) {
		if ( adaptee != null ) {
			this.setName( adaptee.getName() );

			final Set<MethodOfReviewValue> adapteeMethods = adaptee.getMethods();
			if ( adapteeMethods != null ) {
				final List<Method> adapterMethods = this.getMethod();
				for ( MethodOfReviewValue enumVal : adapteeMethods ) {
					Method m = new Method();
					m.setName( enumVal );
					adapterMethods.add( m );
				}
			}
		}
	}

	/**
	 * Copy scope via adapter
	 * 
	 * @param src
	 *            source set
	 * @param dst
	 *            destination set
	 */
	public static void copyScopes( Set<IScope> src, Set<IScope> dst ) {
		if ( src != null && dst != null ) {
			for ( IScope srcItem : src ) {
				dst.add( new ScopeAdapter( srcItem ) );
			}
		}
	}

}
