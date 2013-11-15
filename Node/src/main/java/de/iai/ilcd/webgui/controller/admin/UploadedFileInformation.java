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

import de.fzk.iai.ilcd.api.dataset.ILCDTypes;

public class UploadedFileInformation implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 7380920987598229033L;

	private String pathName = null;

	private String fileName = null;

	private ILCDTypes ilcdType = null;

	private String fileType = null;

	private String message;

	private long fileSize = 0;

	private boolean addAsNew = true;

	private boolean overwrite = false;

	public UploadedFileInformation() {

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName( String fileName ) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType( String fileType ) {
		this.fileType = fileType;
	}

	public ILCDTypes getIlcdType() {
		return ilcdType;
	}

	public void setIlcdType( ILCDTypes ilcdType ) {
		this.ilcdType = ilcdType;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName( String pathName ) {
		this.pathName = pathName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize( long fileSize ) {
		this.fileSize = fileSize;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage( String message ) {
		this.message = message;
	}

	public boolean isAddAsNew() {
		return addAsNew;
	}

	public void setAddAsNew( boolean addAsNew ) {
		this.addAsNew = addAsNew;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite( boolean overwrite ) {
		this.overwrite = overwrite;
	}

}
