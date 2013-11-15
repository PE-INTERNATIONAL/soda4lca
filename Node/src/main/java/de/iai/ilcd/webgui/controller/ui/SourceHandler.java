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

import de.fzk.iai.ilcd.service.model.ISourceVO;
import de.iai.ilcd.model.dao.SourceDao;
import de.iai.ilcd.model.source.Source;

/**
 * Backing bean for source detail view
 */
@ManagedBean
public class SourceHandler extends AbstractDataSetHandler<ISourceVO, Source, SourceDao> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 8501172387748377828L;

	/**
	 * Initialize handler
	 */
	public SourceHandler() {
		super( new SourceDao(), "sources" );
	}

	/**
	 * Convenience method, delegates to {@link #getDataSet()}
	 * 
	 * @return represented source instance
	 */
	public ISourceVO getSource() {
		return this.getDataSet();
	}

}
