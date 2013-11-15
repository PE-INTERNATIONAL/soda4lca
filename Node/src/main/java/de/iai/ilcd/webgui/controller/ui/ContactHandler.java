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

import de.fzk.iai.ilcd.service.model.IContactVO;
import de.iai.ilcd.model.contact.Contact;
import de.iai.ilcd.model.dao.ContactDao;

/**
 * Backing bean for contact detail view
 */
@ManagedBean
public class ContactHandler extends AbstractDataSetHandler<IContactVO, Contact, ContactDao> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 1618629753241533506L;

	/**
	 * Initialize backing bean
	 */
	public ContactHandler() {
		super( new ContactDao(), "contacts" );
	}

	/**
	 * Convenience method, delegates to {@link #getDataSet()}
	 * 
	 * @return represented contact
	 */
	public IContactVO getContact() {
		return this.getDataSet();
	}

}
