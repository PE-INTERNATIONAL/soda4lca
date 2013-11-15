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

package de.iai.ilcd.model.adapter.dataset;

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.ContactDataSetVO;
import de.fzk.iai.ilcd.service.model.IContactListVO;
import de.fzk.iai.ilcd.service.model.IContactVO;
import de.iai.ilcd.model.adapter.LStringAdapter;

/**
 * Adapter for Contacts
 */
public class ContactVOAdapter extends AbstractDatasetAdapter<ContactDataSetVO, IContactListVO, IContactVO> {

	/**
	 * Create adapter for {@link IContactListVO contact list value object}
	 * 
	 * @param adaptee
	 *            list value object to adapt
	 */
	public ContactVOAdapter( IContactListVO adaptee ) {
		super( new ContactDataSetVO(), adaptee );
	}

	/**
	 * Create adapter for the {@link IContactVO contact value object}
	 * 
	 * @param adaptee
	 *            value object to adapt
	 */
	public ContactVOAdapter( IContactVO adaptee ) {
		super( new ContactDataSetVO(), adaptee );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IContactListVO src, ContactDataSetVO dst ) {
		dst.setEmail( src.getEmail() );

		dst.setPhone( src.getPhone() );

		dst.setWww( src.getWww() );

		LStringAdapter.copyLStrings( src.getShortName(), dst.getShortName() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IContactVO src, ContactDataSetVO dst ) {
		this.copyValues( (IContactListVO) src, dst );

		dst.setCentralContactPoint( src.getCentralContactPoint() );
		dst.setFax( src.getFax() );
	}

}
