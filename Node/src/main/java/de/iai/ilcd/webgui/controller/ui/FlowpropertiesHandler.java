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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.iai.ilcd.model.dao.FlowPropertyDao;
import de.iai.ilcd.model.flowproperty.FlowProperty;

/**
 * Backing bean for flow property list view
 */
@ManagedBean( )
@ViewScoped
public class FlowpropertiesHandler extends AbstractDataSetsHandler<FlowProperty, FlowPropertyDao> implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -4221400320067993534L;

	/**
	 * Initialize handler
	 */
	public FlowpropertiesHandler() {
		super( FlowProperty.class, new FlowPropertyDao() );
	}

}
