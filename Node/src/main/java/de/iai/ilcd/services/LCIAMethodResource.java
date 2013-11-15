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

package de.iai.ilcd.services;

import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

import de.fzk.iai.ilcd.api.dataset.ILCDTypes;
import de.iai.ilcd.model.common.DataSetType;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.model.dao.LCIAMethodDao;
import de.iai.ilcd.model.lciamethod.LCIAMethod;

/**
 * REST web service for LCIAMethods
 */
@Component
@Path( "lciamethods" )
public class LCIAMethodResource extends AbstractDataSetResource<LCIAMethod> {

	public LCIAMethodResource() {
		super( DataSetType.LCIAMETHOD, ILCDTypes.LCIAMETHOD );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetDao<LCIAMethod, ?, ?> getFreshDaoInstance() {
		return new LCIAMethodDao();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getXMLListTemplatePath() {
		return "/xml/lciamethods.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getXMLTemplatePath() {
		return "/xml/lciamethod.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHTMLOverviewTemplatePath() {
		return "/html/lciamethod_overview.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDataSetTypeName() {
		return "LCIA method";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHTMLFullViewTemplatePath() {
		return "/html/lciamethod.vm";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean userRequiresFullViewRights() {
		return true;
	}

}
