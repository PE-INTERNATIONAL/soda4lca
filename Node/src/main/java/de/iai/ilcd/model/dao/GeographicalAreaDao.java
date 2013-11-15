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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.model.common.GeographicalArea;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * 
 * @author clemens.duepmeier
 */
public class GeographicalAreaDao extends AbstractLongIdObjectDao<GeographicalArea> {

	public GeographicalAreaDao() {
		super( "GeographicalArea", GeographicalArea.class );
	}

	private static final Logger logger = LoggerFactory.getLogger( GeographicalAreaDao.class );

	private EntityManager em = PersistenceUtil.getEntityManager();

	public List<GeographicalArea> getAreas() {
		return this.getAll();
	}

	@SuppressWarnings( "unchecked" )
	public List<GeographicalArea> getCountries() {

		List<GeographicalArea> countries = new ArrayList<GeographicalArea>();

		countries = this.em.createQuery( "select area from GeographicalArea area where LENGTH(area.areaCode)=2 order by area.name" ).getResultList();

		return countries;
	}

	public GeographicalArea getArea( Long id ) {
		return this.getById( id );
	}

	public GeographicalArea getArea( String areaCode ) {
		try {
			return this.em.createQuery( "select area from GeographicalArea area where area.areaCode=:areaCode", GeographicalArea.class ).setParameter(
					"areaCode", areaCode ).getSingleResult();
		}
		catch ( NoResultException e ) {
			return null;
		}
	}
}
