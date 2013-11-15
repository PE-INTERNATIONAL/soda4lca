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
package de.iai.ilcd.services;

import java.io.IOException;
import java.io.StringWriter;

import javax.ws.rs.core.UriInfo;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.EscapeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.gui.utils.ConfigurationTool;
import de.iai.ilcd.security.UserAccessBean;

/**
 * 
 * @author clemens.duepmeier
 */
public class VelocityUtil {

	private static final Logger logger = LoggerFactory.getLogger( VelocityUtil.class );

	static {
		try {
			String basePath = ConfigurationService.INSTANCE.getBasePath();

			String tplBasePath = basePath + "/WEB-INF/templates/services";

			logger.debug( "Template base path = " + tplBasePath );

			Velocity.setProperty( "file.resource.loader.path", tplBasePath );
			Velocity.setProperty( "runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem" );
			Velocity.init();

		}
		catch ( Exception ex ) {
			logger.error( "cannot initialize velocity", ex );
		}
	}

	public static VelocityContext getServicesContext( UriInfo uriInfo ) {
		VelocityContext context = getContext();

		ServiceContext serviceContext = new ServiceContext();
		context.put( "context", serviceContext );
		UserAccessBean user = new UserAccessBean();
		context.put( "user", user );
		ConfigurationTool conf = new ConfigurationTool();
		context.put( "conf", conf );

		return context;
	}

	public static VelocityContext getContext() {
		VelocityContext context = new VelocityContext();
		EscapeTool escapeTool = new EscapeTool();
		context.put( "escape", escapeTool );

		return context;
	}

	public static String parseTemplate( String templName, VelocityContext velocityContext ) {

		Template templ = null;
		try {
			templ = Velocity.getTemplate( templName );
		}
		catch ( ResourceNotFoundException ex ) {
			logger.error( "Cannot find template " + templName, ex );
		}
		catch ( ParseErrorException ex ) {
			logger.error( "There are errors in the velocity template " + templName, ex );
		}
		catch ( Exception ex ) {
			logger.error( "There was an unknown error in parsing the template " + templName, ex );
		}
		StringWriter writer = new StringWriter();

		if ( templ != null ) {
			try {
				templ.merge( velocityContext, writer );
			}
			catch ( IOException ex ) {
				logger.error( "Cannot write parsed template " + templName + " to output stream", ex );
			}
		}

		return writer.toString();
	}

	public static String errorPage( UriInfo context, String title, String errorText ) {
		VelocityContext velocityContext = VelocityUtil.getServicesContext( context );
		velocityContext.put( "errorRef", context.getRequestUri().toString() );
		velocityContext.put( "title", title );
		velocityContext.put( "errorText", errorText );

		return VelocityUtil.parseTemplate( "/html/notfound.vm", velocityContext );
	}
}
