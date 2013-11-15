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

package de.iai.ilcd.webgui.controller.admin;

import java.io.Serializable;
import java.net.UnknownHostException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientHandlerException;

import de.fzk.iai.ilcd.service.client.FailedAuthenticationException;
import de.fzk.iai.ilcd.service.client.FailedConnectionException;
import de.fzk.iai.ilcd.service.client.impl.ILCDNetworkClient;
import de.fzk.iai.ilcd.service.model.INodeInfo;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.dao.NetworkNodeDao;
import de.iai.ilcd.model.dao.PersistType;
import de.iai.ilcd.model.nodes.NetworkNode;
import de.iai.ilcd.webgui.controller.AbstractHandler;

/**
 * 
 * @author clemens.duepmeier
 */

@ManagedBean
@ViewScoped
public class NodeHandler extends AbstractHandler implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -3864872300701299028L;

	private static final Logger logger = LoggerFactory.getLogger( NodeHandler.class );

	private NetworkNode node = new NetworkNode();

	private final NetworkNode localhostNodeInstance = new NetworkNode();

	private String savedPassword;

	/** Creates a new instance of NodeHandler */
	public NodeHandler() {
		this.copyProperties( this.localhostNodeInstance, ConfigurationService.INSTANCE.getNodeInfo() );

		// check if the page was called with a request parameter specifying the current node
		String nodeId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( "nodeId" );
		if ( nodeId != null ) {
			NetworkNodeDao nodeDao = new NetworkNodeDao();
			NetworkNode existingNode = nodeDao.getNetworkNode( Long.parseLong( nodeId ) );
			if ( existingNode != null ) {
				this.node = existingNode;
				NodeHandler.logger.info( "found node with node ID {} and database ID {}", this.node.getNodeId(), this.node.getId() );
			}
		}
	}

	public NetworkNode getNode() {
		return this.node;
	}

	public NetworkNode getCurrentNode() {
		return this.localhostNodeInstance;
	}

	public void setNode( NetworkNode node ) {
		this.node = node;
	}

	private void copyProperties( NetworkNode node, INodeInfo nodeInfo ) {
		// copy properties to internal entity bean
		node.setNodeId( nodeInfo.getNodeID() );
		node.setName( nodeInfo.getName() );
		node.setOperator( nodeInfo.getOperator() );
		node.setBaseUrl( nodeInfo.getBaseURL() );
		node.setDescription( nodeInfo.getDescription().getValue() );
		node.setAdminName( nodeInfo.getAdminName() );
		node.setAdminPhone( nodeInfo.getAdminPhone() );
		node.setAdminEmailAddress( nodeInfo.getAdminEMail() );
		node.setAdminWwwAddress( nodeInfo.getAdminWWW() );
	}

	public void createNode() {

		// if (registry == null) {
		// FacesMessage message = Messages.getMessage("resources.lang",
		// "registryIsReguired", null);
		// message.setSeverity(FacesMessage.SEVERITY_ERROR);
		// FacesContext.getCurrentInstance().addMessage(null, message);
		// return;
		// }

		NetworkNodeDao dao = new NetworkNodeDao();

		NodeHandler.logger.debug( "trying to retrieve node info from " + this.node.getBaseUrl() );

		ILCDNetworkClient client = null;

		try {
			// retrieve nodeinfo from foreign node
			client = new ILCDNetworkClient( this.node.getBaseUrl() );
			INodeInfo nodeInfo = client.getNodeInfo();

			NodeHandler.logger.debug( "retrieved node info for " + nodeInfo.getNodeID() );

			this.copyProperties( this.node, nodeInfo );

		}
		catch ( FailedConnectionException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( FailedAuthenticationException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( ClientHandlerException e ) {
			if ( e.getCause() instanceof UnknownHostException ) {
				NodeHandler.logger.info( "host not found" );
				this.addI18NFacesMessage( "facesMsg.node.connectError", FacesMessage.SEVERITY_ERROR );
				return;
			}
		}
		catch ( Exception e ) {
			NodeHandler.logger.warn( "unknown error retrieving node info" );
			NodeHandler.logger.warn( e.getMessage() );
			this.addI18NFacesMessage( "facesMsg.node.infoError", FacesMessage.SEVERITY_ERROR );
			return;
		}

		NetworkNode existingNode = dao.getNetworkNode( this.node.getNodeId() );
		if ( existingNode != null ) {
			this.addI18NFacesMessage( "facesMsg.node.alreadyExists", FacesMessage.SEVERITY_ERROR );
			return;
		}

		if ( this.node.getNodeId() == null ) {
			this.addI18NFacesMessage( "facesMsg.node.noID", FacesMessage.SEVERITY_ERROR );
			return;
		}

		if ( dao.checkAndPersist( this.node, PersistType.ONLYNEW, null ) ) {
			this.addI18NFacesMessage( "facesMsg.node.addSuccess", FacesMessage.SEVERITY_INFO );
		}
		else {
			this.addI18NFacesMessage( "facesMsg.node.saveError", FacesMessage.SEVERITY_ERROR );
		}

	}

	public void changeNode() {
		NetworkNodeDao dao = new NetworkNodeDao();

		NetworkNode existingNode = dao.getNetworkNode( this.node.getNodeId() );
		if ( existingNode == null ) {
			this.addI18NFacesMessage( "facesMsg.node.noExist", FacesMessage.SEVERITY_ERROR, this.node.getNodeId() );
			return;
		}
		try {
			NodeHandler.logger.info( "merge node with node name {} and id {}", this.node.getNodeId(), this.node.getId() );
			this.node = dao.merge( this.node );
			this.addI18NFacesMessage( "facesMsg.node.saveSuccess", FacesMessage.SEVERITY_INFO );
		}
		catch ( Exception e ) {
			this.addI18NFacesMessage( "facesMsg.saveDataError", FacesMessage.SEVERITY_ERROR );
		}

	}

	// public Long getRegistry() {
	// return registry;
	// }
	//
	// public void setRegistry(Long registry) {
	// this.registry = registry;
	// }

}
