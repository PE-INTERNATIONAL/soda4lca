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

import javax.faces.bean.ManagedBean;

import de.fzk.iai.ilcd.service.model.ILCIAMethodVO;
import de.iai.ilcd.model.dao.LCIAMethodDao;
import de.iai.ilcd.model.lciamethod.LCIAMethod;

/**
 * Backing bean for source LCIA method view
 */
@ManagedBean( name = "lciamethodHandler" )
public class LCIAMethodHandler extends AbstractDataSetHandler<ILCIAMethodVO, LCIAMethod, LCIAMethodDao> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 8501172387748377828L;

	/**
	 * Initialize handler
	 */
	public LCIAMethodHandler() {
		super( new LCIAMethodDao(), "lciamethods" );
	}

	/**
	 * Convenience method, delegates to {@link #getDataSet()}
	 * 
	 * @return represented LCIA method instance
	 */
	public ILCIAMethodVO getLciamethod() {
		return this.getDataSet();
	}

}
