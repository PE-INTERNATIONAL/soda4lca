package de.iai.ilcd.util;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ProjectStage;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.AuthorizationException;

import de.iai.ilcd.webgui.controller.admin.RandomPassword;

/**
 * Handler for faces exceptions
 */
public class FacesExceptionHandler extends ExceptionHandlerWrapper {

	/**
	 * Wrapped exception
	 */
	private final ExceptionHandler wrapped;

	/**
	 * Create the exception
	 * 
	 * @param exception
	 *            wrapped exception
	 */
	FacesExceptionHandler( ExceptionHandler exception ) {
		this.wrapped = exception;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle() throws FacesException {
		FacesContext fc = FacesContext.getCurrentInstance();
		for ( Iterator<ExceptionQueuedEvent> i = this.getUnhandledExceptionQueuedEvents().iterator(); i.hasNext(); ) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			Throwable t = context.getException();
			if ( fc.isProjectStage( ProjectStage.Development ) ) {
				t.printStackTrace();
			}
			do {
				if ( t instanceof AuthorizationException ) {
					this.handleAuthorizationException( fc, (AuthorizationException) t, i );
					return;
				}
				if ( t instanceof ViewExpiredException ) {
					this.handleViewExpiredException( fc, (ViewExpiredException) t, i );
					return;
				}
				t = t.getCause();
			} while ( t != null );
			this.handleGenericException( fc );
		}
	}

	/**
	 * Handle all exceptions with no special treatment (no message)
	 * 
	 * @param fc
	 *            current faces context
	 */
	private void handleGenericException( FacesContext fc ) {
		this.handleExceptionMsgPage( fc, null, "/error.xhtml", null );

	}

	/**
	 * Handle a {@link ViewExpiredException}
	 * 
	 * @param fc
	 *            current faces context
	 * @param t
	 *            exception to get message from
	 * @param i
	 *            iterator to remove exception from
	 */
	private void handleViewExpiredException( FacesContext fc, ViewExpiredException t, Iterator<ExceptionQueuedEvent> i ) {
		this.handleExceptionMsgPage( fc, i, "/error.xhtml", t.getMessage() );

	}

	/**
	 * Handle an {@link AuthorizationException}
	 * 
	 * @param fc
	 *            current faces context
	 * @param t
	 *            exception to get message from
	 * @param i
	 *            iterator to remove exception from
	 */
	private void handleAuthorizationException( FacesContext fc, AuthorizationException t, Iterator<ExceptionQueuedEvent> i ) {
		this.handleExceptionMsgPage( fc, i, "/roleerror.xhtml", t.getMessage() );
	}

	/**
	 * Handle an exception with just redirects to an error page with a message
	 * 
	 * @param i
	 *            iterator to remove from on success
	 * @param facelet
	 *            JSF view to redirect to
	 * @param msg
	 *            message to display
	 */
	private void handleExceptionMsgPage( FacesContext fc, Iterator<ExceptionQueuedEvent> i, String facelet, String msg ) {

		NavigationHandler nav = fc.getApplication().getNavigationHandler();
		try {
			final String msgKey = RandomPassword.getPassword( 10 );
			Object sessionObj = fc.getExternalContext().getSession( true );
			if ( sessionObj instanceof HttpSession ) {
				((HttpSession) sessionObj).setAttribute( msgKey, msg );
			}

			nav.handleNavigation( fc, null, facelet + "?msgKey=" + msgKey + "&faces-redirect=true" );
			fc.renderResponse();
		}
		catch ( Exception eee ) {
		}
		finally {
			if ( i != null ) {
				i.remove();
			}
		}
	}

}
