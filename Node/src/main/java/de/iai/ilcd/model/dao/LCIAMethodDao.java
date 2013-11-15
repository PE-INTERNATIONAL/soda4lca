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

import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.apache.velocity.tools.generic.ValueParser;

import de.fzk.iai.ilcd.service.model.ILCIAMethodListVO;
import de.fzk.iai.ilcd.service.model.ILCIAMethodVO;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.common.GlobalReference;
import de.iai.ilcd.model.common.Uuid;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.lciamethod.LCIAMethod;
import de.iai.ilcd.model.lciamethod.LCIAMethodCharacterisationFactor;

/**
 * Data access object for {@link LCIAMethod LCIA methods}
 */
public class LCIAMethodDao extends DataSetDao<LCIAMethod, ILCIAMethodListVO, ILCIAMethodVO> {

	/**
	 * Create the data access object for {@link LCIAMethod LCIA methods}
	 */
	public LCIAMethodDao() {
		super( "LCIAMethod", LCIAMethod.class, ILCIAMethodListVO.class, ILCIAMethodVO.class );
	}

	protected String getDataStockField() {
		return "lciamethods";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addWhereClausesAndNamedParamesForQueryStringJpql( String typeAlias, ValueParser params, List<String> whereClauses,
			Map<String, Object> whereParamValues ) {
		// nothing to do beside the defaults
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryStringOrderJpql( String typeAlias, String sortString ) {
		if ( "type.value".equals( sortString ) ) {
			return typeAlias + ".type";
		}
		else if ( "timeInformation.referenceYear.value".equals( sortString ) ) {
			return typeAlias + ".timeInformation.referenceYear.defaultValue";
		}
		else if ( "timeInformation.duration.value".equals( sortString ) ) {
			return typeAlias + ".timeInformation.duration.defaultValue";
		}
		else {
			return typeAlias + ".nameCache";
		}
	}

	/**
	 * Link the flow instances in the database with the flow global references of this LCIAMethod
	 * 
	 * @param method
	 *            {@link LCIAMethod}
	 */
	@Override
	protected void preCheckAndPersist( LCIAMethod method ) {
		for ( LCIAMethodCharacterisationFactor cf : method.getCharactarisationFactors() ) {

			if ( cf.getReferencedFlowInstance() != null ) {
				continue; // flow already associated, nothing to do
			}

			GlobalReference flowGlobalReference = cf.getFlowGlobalReference();
			if ( flowGlobalReference == null ) {
				continue; // no global reference available, so no mapping with UUID in this characterisation factor can
						  // be tried --> nothing to do
			}

			String flowUuidStr = null;
			Uuid flowUuid = flowGlobalReference.getUuid();
			if ( flowGlobalReference.getUuid() == null ) {
				// sadly no UUID attribute in the global reference
				// but we can try our luck with the URI
				String uriStr = flowGlobalReference.getUri();
				if ( uriStr == null ) {
					continue; // bad luck: no URL :-(
				}
				String[] splittedUri = uriStr.split( "_" );
				if ( splittedUri.length <= 2 ) {
					continue; // URI has not enough parts in order to contain an UUID: bad luck again :-(
				}
				flowUuidStr = splittedUri[splittedUri.length - 2];
			}
			else {
				flowUuidStr = flowUuid.getUuid();
			}
			// all right ... we found ourselves an UUID string, let's try to find the flow for it in the DB
			Flow flowInstance = null;
			try {
				FlowDao flowDao = new FlowDao();
				flowInstance = flowDao.getByUuid( flowUuidStr );

				// if no exception was thrown: congratulations, we found the flow!
				cf.setReferencedFlowInstance( flowInstance );
			}
			catch ( NoResultException ex ) {
				// close, but not close enough. UUID string was found, but no flow
				// with this UUID in the database: bad luck after all :-(
			}
		}
	}

	/*
	 * ================================================== == DEPRECATED METHODS / BACKWARDS COMPATIBILITY ==
	 * ==================================================
	 */

	/**
	 * Get {@link LCIAMethod} by UUID string and version
	 * 
	 * @param uuid
	 *            the UUID string
	 * @param version
	 *            the version of the method
	 * @return {@link LCIAMethod} for the specified UUID string/version, or <code>null</code> if none found
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases. Use
	 *             {@link #getByUuidAndVersion(String, DataSetVersion)}.
	 */
	@Deprecated
	public LCIAMethod getLCIAMethod( String uuid, DataSetVersion version ) {
		return this.getByUuidAndVersion( uuid, version );
	}

	/**
	 * Get {@link LCIAMethod} by UUID string
	 * 
	 * @param uuid
	 *            the UUID string
	 * @return {@link LCIAMethod} for the specified UUID string, or <code>null</code> if none found
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases
	 */
	@Deprecated
	public LCIAMethod getLCIAMethod( String uuid ) {
		return this.getByUuid( uuid );
	}

	/**
	 * Get all LCIAMethod instances, delegates to {@link #getAll()}.
	 * 
	 * @return all LCIAMethod instances
	 * @deprecated still exists for internal backwards compatibility, may be removed in future releases
	 */
	@Deprecated
	public List<LCIAMethod> getLCIAMethods() {
		return this.getAll();
	}

}
