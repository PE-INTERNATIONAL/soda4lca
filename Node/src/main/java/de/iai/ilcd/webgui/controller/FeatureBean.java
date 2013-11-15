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

package de.iai.ilcd.webgui.controller;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.configuration.Configuration;

import de.iai.ilcd.configuration.ConfigurationService;

@ManagedBean( name = "feature" )
@ApplicationScoped
public class FeatureBean {

	/**
	 * Flag to indicate if the &quot;browse processes&quot; feature shall be enabled
	 */
	private final boolean browseProcesses;

	/**
	 * Flag to indicate if the &quot;browse LCIA methods&quot; feature shall be enabled
	 */
	private final boolean browseLCIAMethods;

	/**
	 * Flag to indicate if the &quot;browse flows&quot; feature shall be enabled
	 */
	private final boolean browseFlows;

	/**
	 * Flag to indicate if the &quot;browse flow properties&quot; feature shall be enabled
	 */
	private final boolean browseFlowProperties;

	/**
	 * Flag to indicate if the &quot;browse unit groups&quot; feature shall be enabled
	 */
	private final boolean browseUnitGroups;

	/**
	 * Flag to indicate if the &quot;browse sources&quot; feature shall be enabled
	 */
	private final boolean browseSources;

	/**
	 * Flag to indicate if the &quot;browse contacts&quot; feature shall be enabled
	 */
	private final boolean browseContacts;

	/**
	 * Flag to indicate if the &quot;search processes&quot; feature shall be enabled
	 */
	private final boolean searchProcesses;

	/**
	 * Initialize feature bean
	 */
	public FeatureBean() {
		Configuration c = ConfigurationService.INSTANCE.getProperties();

		this.browseProcesses = c.getBoolean( "feature.browse.processes", Boolean.TRUE );
		this.browseLCIAMethods = c.getBoolean( "feature.browse.lciamethods", Boolean.TRUE );
		this.browseFlows = c.getBoolean( "feature.browse.flows", Boolean.TRUE );
		this.browseFlowProperties = c.getBoolean( "feature.browse.flowproperties", Boolean.TRUE );
		this.browseUnitGroups = c.getBoolean( "feature.browse.unitgroups", Boolean.TRUE );
		this.browseSources = c.getBoolean( "feature.browse.sources", Boolean.TRUE );
		this.browseContacts = c.getBoolean( "feature.browse.contacts", Boolean.TRUE );

		this.searchProcesses = c.getBoolean( "feature.search.processes", Boolean.TRUE );
	}

	/**
	 * Determine, if the &quot;browse processes&quot; feature shall be enabled
	 * 
	 * @return <code>true</code> if feature is enabled, else <code>false</code>
	 */
	public boolean isBrowseProcesses() {
		return this.browseProcesses;
	}

	/**
	 * Determine, if the &quot;browse processes&quot; feature shall be enabled
	 * 
	 * @return <code>true</code> if feature is enabled, else <code>false</code>
	 */
	public boolean isBrowseLCIAMethods() {
		return this.browseLCIAMethods;
	}

	/**
	 * Determine, if the &quot;browse flows&quot; feature shall be enabled
	 * 
	 * @return <code>true</code> if feature is enabled, else <code>false</code>
	 */
	public boolean isBrowseFlows() {
		return this.browseFlows;
	}

	/**
	 * Determine, if the &quot;browse flow properites&quot; feature shall be enabled
	 * 
	 * @return <code>true</code> if feature is enabled, else <code>false</code>
	 */
	public boolean isBrowseFlowProperties() {
		return this.browseFlowProperties;
	}

	/**
	 * Determine, if the &quot;browse unit groups&quot; feature shall be enabled
	 * 
	 * @return <code>true</code> if feature is enabled, else <code>false</code>
	 */
	public boolean isBrowseUnitGroups() {
		return this.browseUnitGroups;
	}

	/**
	 * Determine, if the &quot;browse sources&quot; feature shall be enabled
	 * 
	 * @return <code>true</code> if feature is enabled, else <code>false</code>
	 */
	public boolean isBrowseSources() {
		return this.browseSources;
	}

	/**
	 * Determine, if the &quot;browse contacts&quot; feature shall be enabled
	 * 
	 * @return <code>true</code> if feature is enabled, else <code>false</code>
	 */
	public boolean isBrowseContacts() {
		return this.browseContacts;
	}

	/**
	 * Determine, if the &quot;search processes&quot; feature shall be enabled
	 * 
	 * @return <code>true</code> if feature is enabled, else <code>false</code>
	 */
	public boolean isSearchProcesses() {
		return this.searchProcesses;
	}

}
