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

package de.iai.ilcd.webgui.controller.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.webgui.controller.AbstractHandler;

@ManagedBean
public class DownloadAllHandler extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -1279348075136230465L;

	private Logger logger = LoggerFactory.getLogger( "DownloadAllHandler" );

	private StreamedContent file;

	public DownloadAllHandler() {

		String zipDirectory = ConfigurationService.INSTANCE.getZipFileDirectory();
		File zipFile = new File( zipDirectory + "/ilcd.zip" );
		if ( !zipFile.canRead() ) {
			this.addI18NFacesMessage( "facesMsg.zipNotAvailable", FacesMessage.SEVERITY_ERROR );
			this.logger.error( "Cannot find zip file {}", zipFile.getAbsoluteFile() );
			return;
		}
		try {
			InputStream fileStream = new FileInputStream( zipFile );
			this.file = new DefaultStreamedContent( fileStream, "application/zip", "ilcd.zip" );
		}
		catch ( Exception e ) {
			this.addI18NFacesMessage( "facesMsg.zipOpeningError", FacesMessage.SEVERITY_ERROR );
			this.logger.error( "Cannot open zip file with all data sets" );
			return;
		}
	}

	public StreamedContent getFile() {
		return this.file;
	}

	public void setFile( StreamedContent file ) {
		this.file = file;
	}
}
