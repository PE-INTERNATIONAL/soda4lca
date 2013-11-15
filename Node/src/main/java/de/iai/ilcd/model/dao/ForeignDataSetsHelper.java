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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.velocity.tools.generic.ValueParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.fzk.iai.ilcd.service.client.DatasetNotFoundException;
import de.fzk.iai.ilcd.service.client.FailedAuthenticationException;
import de.fzk.iai.ilcd.service.client.FailedConnectionException;
import de.fzk.iai.ilcd.service.client.impl.ILCDNetworkClient;
import de.fzk.iai.ilcd.service.client.impl.vo.Result;
import de.fzk.iai.ilcd.service.model.IDataSetListVO;
import de.fzk.iai.ilcd.service.model.IDataSetVO;
import de.iai.ilcd.model.nodes.NetworkNode;
import de.iai.ilcd.model.utils.DistributedSearchLog;

/**
 * 
 * @author clemens.duepmeier
 */
public class ForeignDataSetsHelper {

	private static final String REST_SERVLET_PREFIX = "resource/";

	private static final Logger logger = LoggerFactory.getLogger( ForeignDataSetsHelper.class );

	public <T extends IDataSetListVO> List<T> foreignSearch( Class<T> searchResultClassType, ValueParser params, DistributedSearchLog log ) {
		logger.debug( "searching foreign nodes" );

		List<T> results = new ArrayList<T>();

		MultivaluedMap<String, String> paramMap = new MultivaluedMapImpl();
		for ( Entry<String, Object> entry : params.entrySet() ) {
			String key = entry.getKey();
			if ( key.equals( "distributed" ) ) {
				continue;
			}
			String[] values = params.getStrings( key );
			if ( values != null && values.length > 0 ) {
				List<String> valueList = new ArrayList<String>();
				for ( String value : values ) {
					valueList.add( value );
				}
				paramMap.put( key, valueList );
			}
			else {
				String singleValue = params.getString( key );
				if ( singleValue != null && !singleValue.isEmpty() ) {
					paramMap.putSingle( key, singleValue );
				}
			}

		}

		NetworkNodeDao nodeDao = new NetworkNodeDao();
		List<NetworkNode> nodes = null;
		if ( params.getBoolean( "virtual" ) == null || Boolean.FALSE.equals( params.getBoolean( "virtual" ) ) ) {
			nodes = nodeDao.getRemoteNetworkNodesFromRegistry( params.getString( "registeredIn" ) );
		}
		else {
			nodes = nodeDao.getRemoteNetworkNodes();
		}

		logger.info( "querying {} foreign nodes", nodes.size() );

		logger.debug( "param  keys:  {}", paramMap.keySet() );
		logger.debug( "param values: {}", paramMap.values() );

		for ( NetworkNode node : nodes ) {

			logger.debug( "querying node {}", node.getBaseUrl() );

			String baseUrl = node.getBaseUrl();
			if ( !node.getBaseUrl().endsWith( "/" ) ) {
				baseUrl = baseUrl + "/";
			}
			baseUrl += REST_SERVLET_PREFIX;
			try {
				ILCDNetworkClient ilcdClient = new ILCDNetworkClient( baseUrl );
				Result<T> resultList = ilcdClient.query( searchResultClassType, paramMap );

				logger.debug( "found {} results, {} on page 1", resultList.getTotalSize(), resultList.getDataSets().size() );

				for ( T result : resultList.getDataSets() ) {
					// if (result.getSourceId() == null)
					result.setSourceId( node.getNodeId() );
					result.setHref( baseUrl );
					results.add( result );
				}
			}
			catch ( FailedConnectionException ex ) {
				logger.error( "connection to {} failed", baseUrl, ex );
			}
			catch ( FailedAuthenticationException ex ) {
				logger.error( "authentication failed", ex );
			}
			catch ( ClientHandlerException ex ) {
				log.logException( node, ex );
			}
			catch ( Exception ex ) {
				logger.error( "Error querying foreign node", ex );
			}

		}

		return results;
	}

	public <T extends IDataSetVO> T getForeignDataSet( Class<T> dataSetClassType, String nodeShortName, String uuid, Long registryId ) {

		// we need node name and uuid
		if ( nodeShortName == null || uuid == null ) {
			return null;
		}
		logger.info( "get foreign process from node {} with uuid {}", nodeShortName, uuid );
		NetworkNodeDao nodeDao = new NetworkNodeDao();
		NetworkNode node = nodeDao.getNetworkNode( nodeShortName, registryId );
		if ( node == null ) {
			return null;
		}

		String baseUrl = node.getBaseUrl();
		if ( !node.getBaseUrl().endsWith( "/" ) ) {
			baseUrl = baseUrl + "/";
		}
		baseUrl += REST_SERVLET_PREFIX;

		T dataSet = null;

		try {
			ILCDNetworkClient targetConnection = new ILCDNetworkClient( baseUrl );
			dataSet = (T) targetConnection.getDataSetVO( dataSetClassType, uuid );
			// attach node information
			// if (dataSet.getSourceId() == null)
			dataSet.setSourceId( node.getNodeId() );
			dataSet.setHref( baseUrl );
			// process = new Process(poProcess);
		}
		catch ( FailedConnectionException ex ) {
			logger.error( "Connection to {} failed", baseUrl, ex );
			return null;
		}
		catch ( FailedAuthenticationException ex ) {
			logger.error( "Authentication to {} failed", baseUrl, ex );
			return null;
		}
		catch ( IOException ex ) {
			logger.error( "There were some I/O-errors accessing a dataset from {}", baseUrl, ex );
		}
		catch ( DatasetNotFoundException ex ) {
			logger.error( "Dataset with uuid {} not found", uuid, ex );
			return null;
		}

		return dataSet;
	}
}
