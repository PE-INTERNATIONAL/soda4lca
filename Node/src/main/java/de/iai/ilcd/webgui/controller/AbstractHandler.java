package de.iai.ilcd.webgui.controller;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

/**
 * Base implementation of a handler with support for internationalized Faces messages.
 * 
 * @see #addI18NFacesMessage(String, Severity)
 * @see #addI18NFacesMessage(String, Exception)
 * @see #addI18NFacesMessage(String, String, Severity, Exception, Object...)
 */
public abstract class AbstractHandler implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 2409478268598177954L;

	/**
	 * Message bundle, set via dependency injection
	 */
	@ManagedProperty( value = "#{i18n}" )
	private ResourceBundle i18n;

	/**
	 * Get the bundle (used via dependency injection)
	 * 
	 * @return the bundle
	 */
	public ResourceBundle getI18n() {
		return this.i18n;
	}

	/**
	 * Set the bundle (used via dependency injection)
	 * 
	 * @param i18n
	 *            the bundle to use
	 */
	public void setI18n( ResourceBundle i18n ) {
		this.i18n = i18n;
	}

	/**
	 * Add a message to Faces context context with clientId and Exception details <code>null</code>
	 * 
	 * @param i18nkey
	 *            the key to get message in language bundle from (injected via {@link #setI18n(ResourceBundle)})
	 * @param severity
	 *            severity of the message
	 */
	public void addI18NFacesMessage( String i18nkey, Severity severity ) {
		this.addI18NFacesMessage( null, i18nkey, severity, null );
	}

	/**
	 * Add a message to Faces context with clientId <code>null</code> and details of the Exception
	 * 
	 * @param i18nkey
	 *            the key to get message in language bundle from (injected via {@link #setI18n(ResourceBundle)})
	 * @param exceptionDetails
	 *            the details of the Exception, may be <code>null</code> and <i>will not</i> be loaded from language
	 *            bundle
	 */
	public void addI18NFacesMessage( String i18nkey, Exception exceptionDetails ) {
		this.addI18NFacesMessage( null, i18nkey, FacesMessage.SEVERITY_ERROR, exceptionDetails );
	}

	/**
	 * Add a message to Faces context with clientId and Exception details <code>null</code>
	 * 
	 * @param i18nkey
	 *            the key to get message in language bundle from (injected via {@link #setI18n(ResourceBundle)})
	 * @param severity
	 *            severity of the message
	 * @param msgParams
	 *            message param(s) (not loaded from language bundle)
	 */
	public void addI18NFacesMessage( String i18nkey, Severity severity, Object... msgParams ) {
		this.addI18NFacesMessage( null, i18nkey, severity, null, msgParams );
	}

	/**
	 * Add a message to Faces context
	 * 
	 * @param clientId
	 *            the client id, may be <code>null</code>
	 * @param i18nkey
	 *            the key to get message in language bundle from (injected via {@link #setI18n(ResourceBundle)})
	 * @param severity
	 *            severity of the message
	 * @param exceptionDetails
	 *            the details of the Exception, may be <code>null</code> and <i>will not</i> be loaded from language
	 *            bundle
	 * @param msgParams
	 *            message param(s) (not loaded from language bundle)
	 */
	public void addI18NFacesMessage( String clientId, String i18nkey, Severity severity, Exception exceptionDetails, Object... msgParams ) {
		FacesMessage message = new FacesMessage();
		message.setSeverity( severity );
		String completeMsg = this.i18n.getString( i18nkey );
		if ( msgParams != null ) {
			completeMsg = MessageFormat.format( completeMsg, msgParams );
		}
		message.setSummary( completeMsg );
		if ( exceptionDetails != null ) {
			message.setDetail( exceptionDetails.getMessage() );
		}
		FacesContext.getCurrentInstance().addMessage( clientId, message );
	}

	/**
	 * Get the generic role error string from message bundle
	 * 
	 * @return generic role error string from message bundle
	 */
	protected String getGenericRoleError() {
		return this.getI18n().getString( "public.error.role.msg" );
	}
}
