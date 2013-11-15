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

package de.iai.ilcd.model.flowproperty;

import de.fzk.iai.ilcd.service.model.common.IGlobalReference;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.flowproperty.IUnitGroupType;
import de.iai.ilcd.model.unitgroup.UnitGroup;

/**
 * 
 * @author clemens.duepmeier
 */
public class UnitGroupDetails implements IUnitGroupType {

	private UnitGroup unitGroup;

	private IGlobalReference unitGroupReference;

	public UnitGroupDetails( UnitGroup unitGroup, IGlobalReference unitGroupReference ) {
		this.unitGroup = unitGroup;
		this.unitGroupReference = unitGroupReference;
	}

	@Override
	public String getHref() {
		return null;
	}

	@Override
	public IMultiLangString getName() {
		return unitGroup.getName();
	}

	@Override
	public String getDefaultUnit() {
		if ( this.unitGroup != null ) {
			return unitGroup.getDefaultUnit();
		}
		else {
			return "";
		}

	}

	@Override
	public IGlobalReference getReference() {
		return unitGroupReference;
	}

}
