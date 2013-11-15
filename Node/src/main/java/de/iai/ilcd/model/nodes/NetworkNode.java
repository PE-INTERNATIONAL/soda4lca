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

package de.iai.ilcd.model.nodes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.iai.ilcd.model.registry.Registry;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "networknode" )
public class NetworkNode implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( unique = true, nullable = false )
	private String nodeId;

	@Column( unique = true, nullable = false )
	private String name;

	private String description;

	private String operator;

	@Column( unique = true, nullable = false )
	private String baseUrl;

	private String adminName;

	private String adminEmailAddress;

	private String adminWwwAddress;

	private String adminPhone;

	@ManyToOne
	@JoinColumn( name = "REGISTRY_ID" )
	private Registry registry;

	public NetworkNode() {
	}

	public NetworkNode( String nodeId, String name ) {
		this.nodeId = nodeId;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName( String adminName ) {
		this.adminName = adminName;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl( String baseUrl ) {
		this.baseUrl = baseUrl;
	}

	public String getAdminEmailAddress() {
		return adminEmailAddress;
	}

	public void setAdminEmailAddress( String adminEmailAddress ) {
		this.adminEmailAddress = adminEmailAddress;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId( String nodeId ) {
		this.nodeId = nodeId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (nodeId != null ? nodeId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof NetworkNode) ) {
			return false;
		}
		NetworkNode other = (NetworkNode) object;

		if ( nodeId != null && other.getNodeId() != null && nodeId.equals( other.getNodeId() ) ) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.nodes.NetworkNode[nodeId=" + nodeId + "]";
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription( String description ) {
		this.description = description;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator( String operator ) {
		this.operator = operator;
	}

	/**
	 * @return the adminWwwAddress
	 */
	public String getAdminWwwAddress() {
		return adminWwwAddress;
	}

	/**
	 * @param adminWwwAddress
	 *            the adminWwwAddress to set
	 */
	public void setAdminWwwAddress( String adminWwwAddress ) {
		this.adminWwwAddress = adminWwwAddress;
	}

	/**
	 * @return the adminPhone
	 */
	public String getAdminPhone() {
		return adminPhone;
	}

	/**
	 * @param adminPhone
	 *            the adminPhone to set
	 */
	public void setAdminPhone( String adminPhone ) {
		this.adminPhone = adminPhone;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry( Registry registry ) {
		this.registry = registry;
	}

}
