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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.fzk.iai.ilcd.service.client.impl.vo.NodeInfo;
import de.iai.ilcd.configuration.ConfigurationService;

/**
 * REST Web Service
 * 
 * @author oliver.kusche
 */
@Component
@Path( "nodeinfo" )
public class NodeInfoResource {

	private static final Logger logger = LoggerFactory.getLogger( NodeInfoResource.class );

	/** Creates a new instance of NodeInfoResource */
	public NodeInfoResource() {
	}

	@GET
	@Produces( "application/xml" )
	public NodeInfo status() {
		logger.info( "nodeinfo" );

		return ConfigurationService.INSTANCE.getNodeInfo();

	}

}
