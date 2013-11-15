package eu.europa.ec.jrc.lca.registry.view.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("application")
public class ApplicationInfoBean implements Serializable{
	
	private static final long serialVersionUID = 5116590227690230589L;
	
	private String version;
	
	@Value("${theme}")
	private String theme;
	
	@PostConstruct
	private void init(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		version = getVersionFromManifest(session);
	}
	
	private String getVersionFromManifest(HttpSession session){
		ServletContext application = session.getServletContext();
		InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
		try {
			Manifest manifest = new Manifest(inputStream);
			Attributes atts = manifest.getMainAttributes();
			return atts.getValue("artifactId") + " v" + atts.getValue("version");
		} catch (IOException e) {
			return "unknown version";
		}
	}

	public String getVersion() {
		return version;
	}

	public String getTheme() {
		return theme;
	}
	
}
