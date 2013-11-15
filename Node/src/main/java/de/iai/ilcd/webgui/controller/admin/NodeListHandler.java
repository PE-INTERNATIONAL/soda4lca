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

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.iai.ilcd.model.dao.NetworkNodeDao;
import de.iai.ilcd.model.nodes.NetworkNode;
import de.iai.ilcd.webgui.controller.AbstractHandler;

/**
 * Admin node list handler
 */
@ManagedBean( name = "nodeListHandler" )
@RequestScoped
public class NodeListHandler extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -2078948867527652211L;

	private List<GuiListManagerItem<NetworkNode>> nodeDescriptions = new ArrayList<GuiListManagerItem<NetworkNode>>();

	private NetworkNodeDao dao = new NetworkNodeDao();

	public NodeListHandler() {
	}

	public List<GuiListManagerItem<NetworkNode>> getNodeDescriptions() {

		List<NetworkNode> nodes = this.dao.getNetworkNodes();
		GuiListManagerUtils<NetworkNode> utils = new GuiListManagerUtils<NetworkNode>();
		this.nodeDescriptions = utils.createGuiItemList( nodes );

		return this.nodeDescriptions;
	}

	public void deleteSelected() {

		List<GuiListManagerItem<NetworkNode>> removeList = new ArrayList<GuiListManagerItem<NetworkNode>>();

		for ( GuiListManagerItem<NetworkNode> desc : this.nodeDescriptions ) {
			if ( desc.isShouldBeDeleted() ) {
				this.dao.remove( desc.getItem() );
				removeList.add( desc );
				this.addI18NFacesMessage( "facesMsg.removeSuccess", FacesMessage.SEVERITY_INFO, desc.getItem().getNodeId() );
			}
		}
		for ( GuiListManagerItem<NetworkNode> rDesc : removeList ) {
			this.nodeDescriptions.remove( rDesc );
		}
	}
}
