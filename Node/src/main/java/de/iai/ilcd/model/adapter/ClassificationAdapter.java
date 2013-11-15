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

import de.fzk.iai.ilcd.service.client.impl.vo.types.common.ClassType;
import de.fzk.iai.ilcd.service.client.impl.vo.types.common.ClassificationType;
import de.fzk.iai.ilcd.service.model.common.IClass;
import de.fzk.iai.ilcd.service.model.common.IClassification;

public class ClassificationAdapter extends ClassificationType {

	public ClassificationAdapter( IClassification classification ) throws Exception {
		if ( classification == null || classification.getIClassList().size() == 0 )
			throw new Exception( "no classification present" );

		this.setName( classification.getName() );

		for ( IClass clazz : classification.getIClassList() ) {
			this.getClasses().add( new ClassType( clazz.getLevel(), clazz.getName() ) );
		}
	}
}
