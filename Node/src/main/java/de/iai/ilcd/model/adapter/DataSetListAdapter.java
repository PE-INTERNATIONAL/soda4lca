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

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.DataSetList;
import de.fzk.iai.ilcd.service.client.impl.vo.dataset.DataSetVO;
import de.fzk.iai.ilcd.service.model.IContactListVO;
import de.fzk.iai.ilcd.service.model.IDataSetListVO;
import de.fzk.iai.ilcd.service.model.IFlowListVO;
import de.fzk.iai.ilcd.service.model.IFlowPropertyListVO;
import de.fzk.iai.ilcd.service.model.ILCIAMethodListVO;
import de.fzk.iai.ilcd.service.model.IProcessListVO;
import de.fzk.iai.ilcd.service.model.ISourceListVO;
import de.fzk.iai.ilcd.service.model.IUnitGroupListVO;
import de.iai.ilcd.model.adapter.dataset.ContactVOAdapter;
import de.iai.ilcd.model.adapter.dataset.FlowPropertyVOAdapter;
import de.iai.ilcd.model.adapter.dataset.FlowVOAdapter;
import de.iai.ilcd.model.adapter.dataset.LCIAMethodVOAdapter;
import de.iai.ilcd.model.adapter.dataset.ProcessVOAdapter;
import de.iai.ilcd.model.adapter.dataset.SourceVOAdapter;
import de.iai.ilcd.model.adapter.dataset.UnitGroupVOAdapter;

public class DataSetListAdapter extends DataSetList {

	public DataSetListAdapter( List<? extends IDataSetListVO> list ) {

		List<DataSetVO> lst = this.getDataSet();

		for ( IDataSetListVO dataset : list ) {
			if ( dataset instanceof IProcessListVO ) {
				lst.add( new ProcessVOAdapter( (IProcessListVO) dataset ).getDataSet() );
			}
			else if ( dataset instanceof ILCIAMethodListVO ) {
				lst.add( new LCIAMethodVOAdapter( (ILCIAMethodListVO) dataset ).getDataSet() );
			}
			else if ( dataset instanceof IFlowListVO ) {
				lst.add( new FlowVOAdapter( (IFlowListVO) dataset ).getDataSet() );
			}
			else if ( dataset instanceof IFlowPropertyListVO ) {
				lst.add( new FlowPropertyVOAdapter( (IFlowPropertyListVO) dataset ).getDataSet() );
			}
			else if ( dataset instanceof IUnitGroupListVO ) {
				lst.add( new UnitGroupVOAdapter( (IUnitGroupListVO) dataset ).getDataSet() );
			}
			else if ( dataset instanceof ISourceListVO ) {
				lst.add( new SourceVOAdapter( (ISourceListVO) dataset ).getDataSet() );
			}
			else if ( dataset instanceof IContactListVO ) {
				lst.add( new ContactVOAdapter( (IContactListVO) dataset ).getDataSet() );
			}
		}

	}

}
