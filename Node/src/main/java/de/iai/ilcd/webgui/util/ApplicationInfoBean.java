package de.iai.ilcd.webgui.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.iai.ilcd.configuration.ConfigurationService;

@Component
@Scope( "application" )
public class ApplicationInfoBean implements Serializable {

	private static final long serialVersionUID = 5116590227690230589L;

	private String version;

	private String versionTag;

	private String buildTimestamp;

	private String appTitle;

	private String appTitleShort;

	@PostConstruct
	private void init() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession( false );
		version = getParamFromManifest( session, "VERSION" );
		versionTag = getParamFromManifest( session, "VERSION-TAG" );
		buildTimestamp = getParamFromManifest( session, "APP-TITLE" );
		appTitle = getParamFromManifest( session, "APP-TITLE-SHORT" );
		appTitleShort = getParamFromManifest( session, "BUILD-TIMESTAMP" );
	}

	private String getParamFromManifest( HttpSession session, String param ) {
		ServletContext application = session.getServletContext();
		InputStream inputStream = application.getResourceAsStream( "/META-INF/MANIFEST.MF" );
		try {
			Manifest manifest = new Manifest( inputStream );
			Attributes atts = manifest.getMainAttributes();
			return atts.getValue( param );
		}
		catch ( IOException e ) {
			return "unknown param";
		}
	}

	public String getVersion() {
		return version;
	}

	public String getVersionTag() {
		return versionTag;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	public String getAppTitle() {
		return appTitle;
	}

	public String getAppTitleShort() {
		return appTitleShort;
	}

	public boolean isRegistryBasedNetworking() {
		return ConfigurationService.INSTANCE.isRegistryBasedNetworking();
	}
}
