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

package de.iai.ilcd.gui.utils;

import org.apache.commons.configuration.Configuration;

import de.iai.ilcd.configuration.ConfigurationService;

/**
 * TODO: delete when velocity was removed
 * 
 * @author clemens.duepmeier
 */
public class ConfigurationTool {

	private Configuration config = ConfigurationService.INSTANCE.getProperties();

	public String getLayoutDirectory() {
		String directory = config.getString( "theme.layout.directory" );
		if ( directory != null )
			return directory;
		return "/layout/default";
	}

	public String getResourceDirectory() {
		String directory = config.getString( "theme.resource.directory" );
		if ( directory != null )
			return directory;
		return "res/default";
	}

	public boolean isRegistrationActivated() {
		boolean registrationActivated = config.getBoolean( "user.registration.activated", false );

		return registrationActivated;
	}

	public boolean isAccessRestricted() {
		boolean accessRestricted = config.getBoolean( "security.guest.metadataOnly", true );

		return accessRestricted;
	}

}
