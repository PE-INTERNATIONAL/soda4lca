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

import java.util.ArrayList;
import java.util.List;

import de.fzk.iai.ilcd.service.client.impl.vo.types.common.LString;
import de.fzk.iai.ilcd.service.model.common.ILString;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;

/**
 * Adapter for localized strings
 */
public class LStringAdapter {

	/**
	 * The localized strings
	 */
	protected List<LString> lStrings = new ArrayList<LString>();

	/**
	 * Create the adapter
	 * 
	 * @param mls
	 *            instance to adapt
	 */
	public LStringAdapter( IMultiLangString mls ) {
		if ( mls != null ) {
			for ( ILString s : mls.getLStrings() ) {
				lStrings.add( new LString( s.getLang(), s.getValue() ) );
			}
		}
	}

	/**
	 * Get the localized strings
	 * 
	 * @return localized strings
	 */
	public List<LString> getLStrings() {
		return lStrings;
	}

	/**
	 * Copy all localized strings
	 * 
	 * @param src
	 *            source {@link IMultiLangString} instance
	 * @param dst
	 *            destination {@link IMultiLangString} instance
	 */
	public static void copyLStrings( IMultiLangString src, IMultiLangString dst ) {
		if ( src != null && dst != null ) {
			for ( ILString s : src.getLStrings() ) {
				dst.setValue( s.getLang(), s.getValue() );
			}
		}
	}
}
