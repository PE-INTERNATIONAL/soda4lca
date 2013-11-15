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

package de.iai.ilcd.webgui.controller.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author clemens.duepmeier
 */
public class GuiListManagerUtils<T> {

	public List<GuiListManagerItem<T>> createGuiItemList( List<T> items ) {
		List<GuiListManagerItem<T>> guiItemList = new ArrayList<GuiListManagerItem<T>>();

		for ( T item : items ) {
			GuiListManagerItem<T> guiItem = new GuiListManagerItem<T>( item );
			guiItemList.add( guiItem );
		}
		return guiItemList;
	}
}
