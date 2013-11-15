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

package de.iai.ilcd.webgui.controller.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import de.iai.ilcd.model.dao.ProcessDao;
import de.iai.ilcd.model.process.Process;

/**
 * Backing bean for process list view
 */
@ManagedBean
@ViewScoped
public class ProcessesHandler extends AbstractDataSetsHandler<Process, ProcessDao> implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -3347083803227668593L;

	/**
	 * Key for name filter
	 */
	private final static String NAME_FILTER_KEY = "name";

	/**
	 * Filter key for classes.
	 */
	private final static String CLASSES_FILTER_KEY = "classes";

	/**
	 * 
	 */
	private final List<SelectItem> all2ndLevelClasses = new ArrayList<SelectItem>();

	/**
	 * Initialize handler
	 */
	public ProcessesHandler() {
		// TODO remove 3rd arg in constructor call once primefaces fixes datatable issue
		super( Process.class, new ProcessDao(), "tableForm:processTable" );

		// Load all classes for pick list
		for ( String topClassStr : super.getDaoInstance().getTopClasses() ) {
			for ( String subClassStr : super.getDaoInstance().getSubClasses( topClassStr, "1" ) ) {
				this.all2ndLevelClasses.add( new SelectItem( subClassStr, topClassStr + " / " + subClassStr ) );
			}
		}

	}

	/**
	 * Get the selected classes
	 * 
	 * @return selected classes
	 */
	public List<String> getSelectedClasses() {
		String[] val = super.getFilterStringArr( ProcessesHandler.CLASSES_FILTER_KEY );
		if ( val != null ) {
			return Arrays.asList( val );
		}
		else {
			return null;
		}
	}

	/**
	 * Set the selected classes
	 * 
	 * @param selected
	 *            selected classes
	 */
	public void setSelectedClasses( List<String> selected ) {
		String[] val;
		if ( selected != null && selected.size() > 0 ) {
			val = selected.toArray( new String[0] );
		}
		else {
			val = null;
		}
		super.setFilter( ProcessesHandler.CLASSES_FILTER_KEY, val );
	}

	/**
	 * Get the current value of name filter
	 * 
	 * @return current value of name filter
	 */
	public String getNameFilter() {
		return super.getFilter( ProcessesHandler.NAME_FILTER_KEY );
	}

	/**
	 * Set value for name filter
	 * 
	 * @param nameFilter
	 *            name filter value to set
	 */
	public void setNameFilter( String nameFilter ) {
		super.setFilter( ProcessesHandler.NAME_FILTER_KEY, nameFilter );
	}

	/**
	 * Get all 2nd level classification classes
	 * 
	 * @return 2nd level classification classes
	 */
	public List<SelectItem> getAll2ndLevelClasses() {
		return this.all2ndLevelClasses;
	}
}
