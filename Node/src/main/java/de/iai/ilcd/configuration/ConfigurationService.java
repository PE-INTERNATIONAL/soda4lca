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

package de.iai.ilcd.configuration;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.exception.FlywayException;
import com.googlecode.flyway.core.validation.ValidationException;

import de.fzk.iai.ilcd.service.client.impl.vo.NodeInfo;

/**
 * 
 * @author clemens.duepmeier
 */
public enum ConfigurationService {

	INSTANCE;

	private final Logger logger = LoggerFactory.getLogger( ConfigurationService.class );

	// initialize basePath while loading class
	private final String basePath;

	private URI baseURI = null;

	private String contextPath = null;

	private String versionTag = null;

	private Configuration fileConfig;

	private Configuration appConfig;

	private String featureNetworking;

	private final NodeInfo nodeInfo = new NodeInfo();

	private final String defaultPropertiesFile = System.getProperty( "catalina.base" ) + File.separator + "conf" + File.separator + "soda4LCA.properties";

	private List<String> preferredLanguages = null;

	ConfigurationService() {
		try {
			this.appConfig = new PropertiesConfiguration( "app.properties" );
		}
		catch ( ConfigurationException e ) {
			throw new RuntimeException( "FATAL ERROR: application properties could not be initialized", e );
		}

		// log application version message
		this.versionTag = this.appConfig.getString( "version.tag" );
		this.logger.info( this.versionTag );

		// validate/migrate database schema
		this.migrateDatabaseSchema();

		URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource( "log4j.properties" );
		String decodedPath = "";
		// now extract path and decode it
		try {
			// note, that URLs getPath() method does not work, because it don't
			// decode encoded Urls, but URI's does this
			decodedPath = resourceUrl.toURI().getPath();
		}
		catch ( URISyntaxException ex ) {
			this.logger.error( "Cannot extract base path from resource files", ex );
		}

		// base path it relative to web application root directory
		this.basePath = decodedPath.replace( "/WEB-INF/classes/log4j.properties", "" );
		this.logger.info( "base path of web application: {}", this.basePath );

		// Obtain our environment naming context
		Context initCtx;
		Context envCtx;
		String propertiesFilePath = null;
		try {
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup( "java:comp/env" );
			propertiesFilePath = (String) envCtx.lookup( "soda4LCAProperties" );
		}
		catch ( NamingException e1 ) {
			this.logger.error( e1.getMessage() );
		}

		if ( propertiesFilePath == null ) {
			this.logger.info( "using default application properties at {}", this.defaultPropertiesFile );
			propertiesFilePath = this.defaultPropertiesFile;
		}
		else {
			this.logger.info( "reading application configuration properties from {}", propertiesFilePath );
		}

		try {
			// OK, now load configuration file
			this.fileConfig = new PropertiesConfiguration( propertiesFilePath );
			this.featureNetworking = this.fileConfig.getString( "feature.networking" );
			configureLanguages();
		}
		catch ( ConfigurationException ex ) {
			this.logger
					.error( "Cannot find application configuration properties file under {}, either put it there or set soda4LCAProperties environment entry via JNDI.",
							propertiesFilePath, ex );
			throw new RuntimeException( "application configuration properties not found", ex );
		}

	}

	@SuppressWarnings( "unchecked" )
	public void configureLanguages() {
		this.preferredLanguages = this.fileConfig.getList( "preferredlanguages" );
		if ( this.preferredLanguages == null || this.preferredLanguages.isEmpty() ) {
			this.preferredLanguages = new ArrayList<String>();
			this.preferredLanguages.add( "en" );
			this.preferredLanguages.add( "de" );
			this.preferredLanguages.add( "fr" );
		}
	}

	public void configureNodeInfo( String ctxPath ) {
		if ( this.contextPath == null ) {
			this.contextPath = ctxPath;
			this.confNodeInfo();
		}
	}

	private void confNodeInfo() {
		String detectedHostName = null;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			detectedHostName = inetAddress.getHostName();
		}
		catch ( UnknownHostException e ) {
			this.logger.error( "Could not detect hostname", e );
		}

		String configuredHostName = this.fileConfig.getString( "service.url.hostname" );
		int port = this.fileConfig.getInteger( "service.url.port", 80 );

		String hostName;

		if ( configuredHostName != null ) {
			hostName = configuredHostName;
		}
		else {
			hostName = detectedHostName;
		}

		try {
			URI newUri = new URI( "http", null, hostName, (port == 80 ? -1 : port), this.contextPath, null, null );
			this.logger.info( "application base URI: " + newUri.toString() );
			this.baseURI = newUri;
			this.nodeInfo.setBaseURL( newUri.toString() + "/resource/" );
		}
		catch ( URISyntaxException e ) {
			throw new RuntimeException( "FATAL ERROR: could not determine base URL for service interface", e );
		}

		try {
			this.nodeInfo.setNodeID( this.fileConfig.getString( "service.node.id" ) );
		}
		catch ( Exception e ) {
			this.logger.error( "Cannot set nodeid from configuration file", e );
		}
		try {
			this.nodeInfo.setName( this.fileConfig.getString( "service.node.name" ) );
		}
		catch ( Exception e ) {
			this.logger.error( "Cannot set nodename from configuration file", e );
		}

		this.nodeInfo.setOperator( this.fileConfig.getString( "service.node.operator" ) );
		this.nodeInfo.setDescription( this.fileConfig.getString( "service.node.description" ) );

		// override baseURL only if it is explicitly set in the configuration file
		if ( this.fileConfig.getString( "service.node.baseURL" ) != null ) {
			String url = this.fileConfig.getString( "service.node.baseURL" );
			if ( !url.endsWith( "/" ) )
				url += "/";
			this.nodeInfo.setBaseURL( url );
		}

		this.nodeInfo.setAdminName( this.fileConfig.getString( "service.admin.name" ) );
		this.nodeInfo.setAdminEMail( this.fileConfig.getString( "service.admin.email" ) );
		this.nodeInfo.setAdminPhone( this.fileConfig.getString( "service.admin.phone" ) );
		this.nodeInfo.setAdminWWW( this.fileConfig.getString( "service.admin.www" ) );

	}

	private void migrateDatabaseSchema() {
		try {
			Context ctx = new InitialContext();
			DataSource dataSource = (DataSource) ctx.lookup( "java:comp/env/jdbc/soda4LCAdbconnection" );

			Flyway flyway = new Flyway();
			flyway.setDataSource( dataSource );
			flyway.setBasePackage( "de.iai.ilcd.db.migrations" );
			flyway.setBaseDir( "sql/migrations" );

			this.logSchemaStatus( flyway );

			try {
				flyway.validate();
			}
			catch ( ValidationException e ) {
				this.logger.error( "database schema: could not successfully validate database status, database needs to be initialized" );
				throw new RuntimeException( "FATAL ERROR: database schema is not properly initialized", e );
			}

			int migrations = flyway.migrate();

			if ( migrations > 0 ) {
				this.logger.info( "database schema: successfully migrated" );
				this.logSchemaStatus( flyway );
			}

		}
		catch ( FlywayException e ) {
			this.logger.error( "error migrating database schema", e );
			throw new RuntimeException( "FATAL ERROR: database schema is not properly initialized", e );
		}
		catch ( NamingException e ) {
			this.logger.error( "error looking up datasource", e );
			throw new RuntimeException( "FATAL ERROR: could not lookup datasource", e );
		}

	}

	private void logSchemaStatus( Flyway flyway ) {
		if ( flyway.status() != null ) {
			this.logger.info( "database schema: current version is " + flyway.status().getVersion() );
		}
		else {
			this.logger.info( "database schema: no migration has been applied yet." );
		}
	}

	public String getVersionTag() {
		return this.versionTag;
	}

	public String getNodeId() {
		return this.nodeInfo.getNodeID();
	}

	public String getNodeName() {
		return this.nodeInfo.getName();
	}

	public Configuration getProperties() {
		return this.fileConfig;
	}

	public NodeInfo getNodeInfo() {
		return this.nodeInfo;
	}

	public String getBasePath() {
		return this.basePath;
	}

	public String getContextPath() {
		return this.contextPath;
	}

	public String getZipFileDirectory() {
		String zipPath = this.fileConfig.getString( "files.location.zipfiles", this.getBasePath() + "/WEB-INF/var/zips" );
		File dir = new File( zipPath );
		try {
			if ( !dir.exists() ) {
				FileUtils.forceMkdir( dir );
			}
		}
		catch ( IOException e ) {
			this.logger.error( "could not create zip files directory at ", zipPath );
		}

		return zipPath;
	}

	public String getDigitalFileBasePath() {
		return this.fileConfig.getString( "files.location.datafiles", this.getBasePath() + "/WEB-INF/var/files" );
	}

	public String getUploadDirectory() {
		return this.fileConfig.getString( "files.location.uploads", this.getBasePath() + "/WEB-INF/var/uploads" );
	}

	public String getUniqueUploadFileName( String prefix, String extension ) {
		StringBuilder buffer = new StringBuilder();
		Date date = new Date();
		buffer.append( prefix ).append( date.getTime() ).append( UUID.randomUUID() ).append( "." ).append( extension );

		return this.getUploadDirectory() + "/" + buffer.toString();
	}

	public URI getBaseURI() {
		return this.baseURI;
	}

	public Configuration getAppConfig() {
		return this.appConfig;
	}

	public boolean isRegistryBasedNetworking() {
		if ( this.featureNetworking == null ) {
			return true;
		}
		else {
			if ( "nodes".equals( this.featureNetworking ) ) {
				return false;
			}
			else {
				return true;
			}
		}
	}

	public List<String> getPreferredLanguages() {
		return preferredLanguages;
	}

	public String getDefaultLanguage() {
		return preferredLanguages.get( 0 );
	}
}
