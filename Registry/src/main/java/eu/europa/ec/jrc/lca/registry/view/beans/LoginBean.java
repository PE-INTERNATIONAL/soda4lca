package eu.europa.ec.jrc.lca.registry.view.beans;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;
import eu.europa.ec.jrc.lca.registry.domain.User;
import eu.europa.ec.jrc.lca.registry.service.AuthenticationService;
import eu.europa.ec.jrc.lca.registry.view.util.Consts;

@Component
@Scope(value = "request")
public class LoginBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private AuthenticationService authenticationService;

	private static final String LANDING_PAGE = "/secured/main.xhtml";
	private static final String LOGIN_PAGE = "/index.xhtml";

	private String login;
	private String password;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String login() throws IOException {
		User u = authenticationService.authenticate(login, password);
		if (u == null) {
			FacesMessage message = Messages.getMessage(null, "wrongUserOrPassword", null);
			message.setSeverity(FacesMessage.SEVERITY_WARN);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return null;
		} else {
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put(Consts.AUTHENTICATED_USER, u);
			FacesUtils.redirectToPage(LANDING_PAGE);
		}
		return null;
	}
	
	public void logout(ActionEvent even){
		((HttpSession)FacesContext.getCurrentInstance().getExternalContext()
		.getSession(false)).invalidate();
		FacesUtils.redirectToPage(LOGIN_PAGE);
	}

	public void checkLogin(ComponentSystemEvent event) {
		if (isLoggedIn()) {
			FacesUtils.redirectToPage(LANDING_PAGE);
		}
	}

	private boolean isLoggedIn() {
		if (FacesContext.getCurrentInstance().getExternalContext()
				.getSession(false) != null) {
			return ((HttpSession) FacesContext.getCurrentInstance()
					.getExternalContext().getSession(false))
					.getAttribute(Consts.AUTHENTICATED_USER) != null;
		}
		return false;
	}
}
