package eu.europa.ec.jrc.lca.commons.view.util;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.context.FacesContext;

public final class FacesUtils {
	private static final String REDIRECT_PARAMETER= "faces-redirect=true";
	private FacesUtils(){
	}
	
	public static void redirectToPage(String page) {
		getNavigationHandler().performNavigation(page+getParameterSeparator(page)+REDIRECT_PARAMETER);
	}
	
	private static String getParameterSeparator(String page){
		if(page.contains("?")){
			return "&";
		}
		return "?";
	}
	
	public static void forwardToPage(String page) {
		getNavigationHandler().performNavigation(page);
	}
	
	private static ConfigurableNavigationHandler getNavigationHandler(){
		FacesContext context = FacesContext.getCurrentInstance();
		return (ConfigurableNavigationHandler) context
				.getApplication().getNavigationHandler();
	}

}
