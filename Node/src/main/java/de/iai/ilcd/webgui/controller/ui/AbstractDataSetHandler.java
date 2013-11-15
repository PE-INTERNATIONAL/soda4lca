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

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;

import org.apache.commons.lang.StringUtils;

import de.fzk.iai.ilcd.service.model.IDataSetVO;
import de.iai.ilcd.model.common.DataSet;
import de.iai.ilcd.model.common.DataSetVersion;
import de.iai.ilcd.model.common.exception.FormatException;
import de.iai.ilcd.model.dao.DataSetDao;
import de.iai.ilcd.security.SecurityUtil;
import de.iai.ilcd.webgui.controller.AbstractHandler;
import de.iai.ilcd.webgui.controller.ConfigurationBean;

/**
 * Handler for DataSet view
 * 
 * @param <I>
 *            Type of value object that this handler provides
 * @param <T>
 *            Type of model object that this handler provides
 * @param <D>
 *            Type of the Data Access Object that is being used by this handler
 */
public abstract class AbstractDataSetHandler<I extends IDataSetVO, T extends DataSet, D extends DataSetDao<T, ?, I>> extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -3151934201431439509L;

	/**
	 * Instance to be hold
	 */
	private I dataSet;

	/**
	 * DAO for data retrieval
	 */
	private final D daoInstance;

	/**
	 * The resource directory
	 */
	private final String resourceDir;

	/**
	 * {@link DataSet#getId() Data set id} as string
	 */
	private String dataSetIdString = null;

	/**
	 * {@link DataSet#getUuid() Data set uuid} as string
	 */
	private String dataSetUuidString = null;

	/**
	 * {@link DataSet#getVersion() Data set version} as string
	 */
	private String dataSetVersionString = null;

	/**
	 * Source node as string
	 */
	private String sourceNodeIdString = null;

	/**
	 * All other versions
	 */
	private List<T> otherVersions;

	/**
	 * Configuration bean
	 */
	@ManagedProperty( value = "#{conf}" )
	private ConfigurationBean conf;

	private Long registryId;

	private String registryUUID;

	/**
	 * Initialize handler
	 * 
	 * @param daoInstance
	 *            matching DAO
	 * @param resourceDir
	 *            the directory for the RESTful service (e.g. <code>processes</code>)
	 */
	public AbstractDataSetHandler( D daoInstance, String resourceDir ) {
		super();
		this.daoInstance = daoInstance;
		this.resourceDir = resourceDir;
	}

	/**
	 * Called when a local data set was loaded successfully by {@link #postViewParamInit(ComponentSystemEvent)}.
	 * Override this method when this information is
	 * required in sub-classes.
	 * 
	 * @param dataset
	 *            loaded data set
	 */
	protected void datasetLoaded( T dataset ) {
		// No operation
	}

	/**
	 * Method to be called after view parameters have been initialized. This is actually loading the data set! What
	 * happens:
	 * <ul>
	 * <li>{@link #getDataSetIdString() database ID} specified: local data set loaded by database ID (all other
	 * parameter are being ignored!)</li>
	 * <li>only {@link #getDataSetUuidString() UUID} specified: local data set loaded by UUID, most recent version</li>
	 * <li>only {@link #getDataSetUuidString() UUID} and {@link #getDataSetVersionString() version} specified: local
	 * data set loaded by UUID and given version</li>
	 * <li>only {@link #getDataSetUuidString() UUID} and {@link #getSourceNodeIdString() source node} specified: remote
	 * data set loaded by UUID, most recent version</li>
	 * <li style="color: gray;"><i>only {@link #getDataSetUuidString() UUID} and {@link #getDataSetVersionString()
	 * version} and {@link #getSourceNodeIdString()
	 * source node} specified: remote data set loaded by UUID and given version (once ServiceAPI supports this)</i></li>
	 * </ul>
	 * 
	 * @param event
	 *            event
	 * @throws AbortProcessingException
	 *             in case of errors
	 */
	@SuppressWarnings( "unchecked" )
	public final void postViewParamInit( ComponentSystemEvent event ) throws AbortProcessingException {
		if ( this.dataSet == null ) {
			this.otherVersions = null;

			T tmp = null;

			// ID specified => load local
			if ( !StringUtils.isBlank( this.dataSetIdString ) ) {
				tmp = this.daoInstance.getByDataSetId( this.dataSetIdString );
			}
			else {
				// no database ID, so ensure UUID is present
				if ( StringUtils.isBlank( this.dataSetUuidString ) ) {
					this.addI18NFacesMessage( "facesMsg.noID", FacesMessage.SEVERITY_ERROR );
					return;
				}

				// check version
				DataSetVersion version = null;
				if ( !StringUtils.isBlank( this.dataSetVersionString ) ) {
					try {
						version = DataSetVersion.parse( this.dataSetVersionString );
					}
					catch ( FormatException e ) {
						this.addI18NFacesMessage( "facesMsg.illegalVersion", FacesMessage.SEVERITY_ERROR );
					}
				}

				// no source node => local
				if ( StringUtils.isBlank( this.sourceNodeIdString ) ) {
					if ( version != null ) {
						tmp = this.daoInstance.getByUuidAndVersion( this.dataSetUuidString, version );
					}
					else {
						tmp = this.daoInstance.getByUuid( this.dataSetUuidString );
					}
				}
				// remote loading
				else {
					if ( version != null ) {
						// TODO: include version
						this.dataSet = this.daoInstance.getForeignDataSet( this.sourceNodeIdString, this.dataSetUuidString, this.registryId );
					}
					else {
						this.dataSet = this.daoInstance.getForeignDataSet( this.sourceNodeIdString, this.dataSetUuidString, this.registryId );
					}
				}
			}

			if ( tmp != null ) {
				// check read privileges (will throw exception if not present)
				SecurityUtil.assertCanRead( tmp );
				// assign data set
				this.dataSet = (I) tmp;
				// load other versions (currently only for local data sets)
				this.otherVersions = this.daoInstance.getOtherVersions( tmp );
				// let the subclasses know
				this.datasetLoaded( tmp );
			}
		}
	}

	/**
	 * Determine if the data set is a foreign data set
	 * 
	 * @return <code>true</code> is data set is a foreign data set, else <code>false</code>
	 * @deprecated will be removed once data stocks are fully implemented due to lack of foreign data sets
	 */
	@Deprecated
	public boolean isForeignDataSet() {
		return !StringUtils.isBlank( this.sourceNodeIdString );
	}

	/**
	 * Get the local resource URL for XML of data set
	 * 
	 * @return local resource URL for XML of data set
	 */
	public String getLocalXMLResourceURL() {
		return this.getLocalResourceUrl( "xml" );
	}

	/**
	 * Get the local resource URL for HTML view of data set
	 * 
	 * @return local resource URL for HTML view of data set
	 */
	public String getLocalHTMLResourceURL() {
		return this.getLocalResourceUrl( "html" );
	}

	/**
	 * Get the foreign resource URL for XML of data set
	 * 
	 * @return foreign resource URL for XML of data set or <code>null</code> if this is not
	 * @deprecated will be removed once data stocks are fully implemented due to lack of foreign data sets
	 */
	@Deprecated
	public String getForeignXMLResourceURL() {
		return this.getForeignResourceUrl( "xml" );
	}

	/**
	 * Get the foreign resource URL for HTML view of data set
	 * 
	 * @return foreign resource URL for HTML view of data set
	 * @deprecated will be removed once data stocks are fully implemented due to lack of foreign data sets
	 */
	@Deprecated
	public String getForeignHTMLResourceURL() {
		return this.getForeignResourceUrl( "html" );
	}

	/**
	 * Get local resource URL
	 * 
	 * @param format
	 *            format (xml or html)
	 * @return created resource URL
	 */
	private String getLocalResourceUrl( String format ) {
		return this.getResourceUrl( this.conf.getContextPath() + "/", format );
	}

	/**
	 * Get foreign resource URL
	 * 
	 * @param format
	 *            format (xml or html)
	 * @return created resource URL
	 * @deprecated will be removed once data stocks are fully implemented due to lack of foreign data sets
	 */
	@Deprecated
	private String getForeignResourceUrl( String format ) {
		return this.getResourceUrl( this.dataSet.getHref(), format );
	}

	/**
	 * Get resource URL
	 * 
	 * @param pathPrefix
	 *            prefix (context path, foreign URL,...) before <code>resource/...</code> of the resource URL
	 * @param format
	 *            format (xml or html)
	 * @return created resource URL
	 */
	private String getResourceUrl( String pathPrefix, String format ) {
		return pathPrefix + "resource/" + this.resourceDir + "/" + this.dataSet.getUuidAsString() + "?format=" + format
				+ (StringUtils.isBlank( this.dataSetVersionString ) ? "" : "&amp;version=" + this.dataSet.getDataSetVersion());
	}

	/**
	 * Setter for managed property for configuration
	 * 
	 * @param conf
	 *            configuration bean to set
	 */
	public void setConf( ConfigurationBean conf ) {
		this.conf = conf;
	}

	/**
	 * Set the data set
	 * 
	 * @param dataSet
	 *            data set to set
	 */
	public void setDataSet( I dataSet ) {
		this.dataSet = dataSet;
	}

	/**
	 * Get the data set
	 * 
	 * @return data set
	 */
	public I getDataSet() {
		return this.dataSet;
	}

	/**
	 * Get the data set id as string
	 * 
	 * @return data set id as string
	 */
	public String getDataSetIdString() {
		return this.dataSetIdString;
	}

	/**
	 * Determine if at least one other version is present
	 * 
	 * @return <code>true</code> if at least one other version is present, else <code>false</code>
	 */
	public boolean isOtherVersionPresent() {
		return this.otherVersions != null && !this.otherVersions.isEmpty();
	}

	/**
	 * Set the data set id as string
	 * 
	 * @param dataSetIdString
	 *            id to set
	 */
	public void setDataSetIdString( String dataSetIdString ) {
		this.dataSetIdString = dataSetIdString;
	}

	/**
	 * Get the data set uuid as string
	 * 
	 * @return data set uuid as string
	 */
	public String getDataSetUuidString() {
		return this.dataSetUuidString;
	}

	/**
	 * Set the data set uuid as string
	 * 
	 * @param dataSetUuidString
	 *            uuid to set
	 */
	public void setDataSetUuidString( String dataSetUuidString ) {
		this.dataSetUuidString = dataSetUuidString;
	}

	/**
	 * Get the data set version as string
	 * 
	 * @return data set version as string
	 */
	public String getDataSetVersionString() {
		return this.dataSetVersionString;
	}

	/**
	 * Set the data set version as string
	 * 
	 * @param dataSetVersionString
	 *            version to set
	 */
	public void setDataSetVersionString( String dataSetVersionString ) {
		this.dataSetVersionString = dataSetVersionString;
	}

	/**
	 * Get the source node id as string
	 * 
	 * @return source node id as string
	 */
	public String getSourceNodeIdString() {
		return this.sourceNodeIdString;
	}

	/**
	 * Set the source node id as string
	 * 
	 * @param sourceNodeIdString
	 *            id to set
	 */
	public void setSourceNodeIdString( String sourceNodeIdString ) {
		this.sourceNodeIdString = sourceNodeIdString;
	}

	/**
	 * Get the other versions for this data set
	 * 
	 * @return other versions for this data set
	 */
	public List<T> getOtherVersions() {
		return this.otherVersions;
	}

	/**
	 * Get the DAO instance of this handler
	 * 
	 * @return DAO instance of this handler
	 */
	protected final D getDaoInstance() {
		return this.daoInstance;
	}

	/**
	 * Determine if string is <code>null</code> or {@link String#indexOf(int) empty}. Please note that if string is not
	 * <code>null</code>, it is {@link String#trim() trimmed} before empty check.
	 * 
	 * @param s
	 *            string to test
	 * @return <code>true</code> if <code>null</code> or empty, else <code>false</code>
	 */
	protected final boolean isStringNullOrEmpty( String s ) {
		return s == null || s.trim().isEmpty();
	}

	public Long getRegistryId() {
		return this.registryId;
	}

	public void setRegistryId( Long registryId ) {
		this.registryId = registryId;
	}

	public String getRegistryUUID() {
		return this.registryUUID;
	}

	public void setRegistryUUID( String registryUUID ) {
		this.registryUUID = registryUUID;
	}

}
