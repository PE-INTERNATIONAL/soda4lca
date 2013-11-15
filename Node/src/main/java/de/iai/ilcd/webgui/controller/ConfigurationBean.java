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

import java.net.URI;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.datastock.RootDataStock;
import de.iai.ilcd.persistence.PersistenceUtil;

/**
 * Bean to access configuration, is in application scope, all values loaded only once
 */
@ManagedBean( name = "conf" )
@ApplicationScoped
public class ConfigurationBean {

	private static Logger logger = LoggerFactory.getLogger( ConfigurationBean.class );

	private static Configuration conf = ConfigurationService.INSTANCE.getProperties();

	/**
	 * Application version tag
	 */
	private final String appVersion;

	/**
	 * Path to templates
	 */
	private final String templatePath;

	/**
	 * Name of jQuery UI theme
	 */
	private String themeName;

	/**
	 * Context path
	 */
	private final String contextPath;

	/**
	 * Base URI
	 */
	private final URI baseUri;

	/**
	 * Flag if registration is activated
	 */
	private final boolean registrationActivated;

	/**
	 * Flag if the registration settings allow the user himself to activate his new user acccount
	 */
	private final boolean selfActivation;

	/**
	 * Application title
	 */
	private final String applicationTitle;

	/**
	 * Path to logo
	 */
	private final String logoPath;

	/**
	 * ID of the default data stock
	 */
	private long defaultDataStockId;

	/**
	 * Flag to indicate if default data stock is a root data stock
	 */
	private boolean defaultDataStockRoot;

	/**
	 * Initialize configuration bean
	 */
	public ConfigurationBean() {
		this( FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() );
	}

	/**
	 * Use this constructor if not in the context of JSF; i.e. initialize context path by yourself
	 * 
	 * @param request
	 *            the request to get the environment from
	 */
	public ConfigurationBean( HttpServletRequest request ) {
		this( request.getContextPath() );
	}

	/**
	 * Use this constructor if not in the context of JSF; i.e. initialize context path by yourself
	 * 
	 * @param contextPath
	 *            the contextPath of application
	 */
	public ConfigurationBean( String contextPath ) {
		this.contextPath = contextPath;

		this.templatePath = this.buildTemplatePath();

		this.themeName = conf.getString( "theme" );
		if ( !this.getTemplatePath().endsWith( "default" ) ) {
			this.themeName = "none";
		}

		this.applicationTitle = conf.getString( "title" );
		this.appVersion = ConfigurationService.INSTANCE.getAppConfig().getString( "version.tag" );

		this.baseUri = ConfigurationService.INSTANCE.getBaseURI();
		this.registrationActivated = conf.getBoolean( "user.registration.activated", false );
		this.selfActivation = conf.getBoolean( "user.registration.selfActivation", false );

		String logoPath = conf.getString( "logo" );
		if ( logoPath == null || logoPath.toLowerCase().trim().equals( "false" ) ) {
			logoPath = null;
		}
		else {
			logoPath = logoPath.replace( "%contextPath%", this.contextPath );
		}
		this.logoPath = logoPath;

		// Config from DB for default stock:
		try {
			EntityManager em = PersistenceUtil.getEntityManager();
			Object[] confData = (Object[]) em.createNativeQuery( "SELECT `default_datastock_id`,`default_datastock_is_root` FROM `configuration` LIMIT 1" )
					.getSingleResult();

			if ( confData[0] instanceof Long ) {
				this.defaultDataStockId = (Long) confData[0];
			}
			if ( confData[1] instanceof Boolean ) {
				this.defaultDataStockRoot = (Boolean) confData[1];
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Build up the template path
	 * 
	 * @return created template path
	 */
	private String buildTemplatePath() {
		String themeName = conf.getString( "template" );
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append( "/templates/" ).append( themeName );
		return pathBuilder.toString();
	}

	/**
	 * Get the template path
	 * 
	 * @return template path
	 */
	public String getTemplatePath() {
		return this.templatePath;
	}

	/**
	 * Get the jQuery UI Theme name
	 * 
	 * @return jQuery UI Theme name
	 */
	public String getThemeName() {
		return this.themeName;
	}

	/**
	 * Get the context path
	 * 
	 * @return context path
	 */
	public String getContextPath() {
		return this.contextPath;
	}

	/**
	 * Get the base URI
	 * 
	 * @return base URI
	 */
	public URI getBaseUri() {
		return this.baseUri;
	}

	/**
	 * Get the flag if registration is activated
	 * 
	 * @return <code>true</code> if registration shall be allowed, else <code>false</code>
	 */
	public boolean isRegistrationActivated() {
		return this.registrationActivated;
	}

	/**
	 * Convenience method for JSF, delegates to {@link #isRegistrationActivated()}
	 * 
	 * @return {@link #isRegistrationActivated()}
	 */
	public boolean getRegistrationActivated() {
		return this.isRegistrationActivated();
	}

	/**
	 * Get the flag if the user is allowed to activate himself a newly created user account
	 * 
	 * @return <code>true</code> if self activation is allowed, else <code>false</code>
	 */
	public boolean isSelfActivation() {
		return this.selfActivation;
	}

	/**
	 * Convenience method for JSF, delegates to {@link #isSelfActivation()}
	 * 
	 * @return <code>true</code> if self activation is allowed, else <code>false</code>
	 */
	public boolean getSelfActivation() {
		return this.isSelfActivation();
	}

	/**
	 * Determine, if logo was provided (<code>{@link #getLogoPath()} != null</code>)
	 * 
	 * @return <code>true</code> if logo provided, else <code>false</code>
	 */
	public boolean isLogoProvided() {
		return this.logoPath != null;
	}

	/**
	 * Get the logo path
	 * 
	 * @return logo path
	 */
	public String getLogoPath() {
		return this.logoPath;
	}

	/**
	 * Get the title of the application
	 * 
	 * @return title of the application
	 */
	public String getApplicationTitle() {
		return this.applicationTitle;
	}

	/**
	 * Get ID of the default data stock
	 * 
	 * @return ID of the default data stock
	 */
	public long getDefaultDataStockId() {
		return this.defaultDataStockId;
	}

	/**
	 * Set ID of the default data stock
	 * 
	 * @param defaultDataStockId
	 *            the ID of the default data stock to set
	 */
	public void setDefaultDataStockId( long defaultDataStockId ) {
		this.defaultDataStockId = defaultDataStockId;
	}

	/**
	 * Indicates if the default data stock is a {@link RootDataStock}
	 * 
	 * @return <code>true</code> if default data stock is a {@link RootDataStock}, else <code>false</code>
	 */
	public boolean isDefaultDataStockRoot() {
		return this.defaultDataStockRoot;
	}

	/**
	 * Set if the default data stock is a {@link RootDataStock}
	 * 
	 * @param defaultDataStockRoot
	 *            <code>true</code> if default data stock is a {@link RootDataStock}, else <code>false</code>
	 */
	public void setDefaultDataStockRoot( boolean defaultDataStockRoot ) {
		this.defaultDataStockRoot = defaultDataStockRoot;
	}

	public String getAppVersion() {
		return this.appVersion;
	}

}
