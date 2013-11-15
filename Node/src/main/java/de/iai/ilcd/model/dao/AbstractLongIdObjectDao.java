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

package de.iai.ilcd.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.Id;

import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * Common implementation for DAO objects that load
 * object that have a {@link Long} type {@link Id} annotated field.
 * 
 * @param <T>
 *            Class that is being accessed
 */
public abstract class AbstractLongIdObjectDao<T> extends AbstractDao<T> {

	/**
	 * Create a DAO
	 * 
	 * @param jpaName
	 *            the name in JPA of class
	 * @param accessedClass
	 *            the class accessed by this DAO
	 */
	public AbstractLongIdObjectDao( String jpaName, Class<T> accessedClass ) {
		super( jpaName, accessedClass );
	}

	/**
	 * Get the entity of type T by numerical JPA id
	 * 
	 * @param id
	 *            id of the entity
	 * @return entity of type T by JPA id
	 */
	public T getById( long id ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		return em.find( this.getAccessedClass(), new Long( id ) );
	}

	/**
	 * Get the entity of type T by JPA id
	 * 
	 * @param id
	 *            id of the entity as string
	 * @return entity of type T by JPA id
	 */
	public T getById( String id ) {
		try {
			return this.getById( Long.parseLong( id ) );
		}
		catch ( NumberFormatException e ) {
			return null;
		}
	}

}
