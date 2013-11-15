package de.iai.ilcd.webgui.controller.admin;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.Configuration;
import org.apache.velocity.VelocityContext;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.services.VelocityUtil;
import de.iai.ilcd.webgui.controller.ConfigurationBean;

public class RegistrationEmailGenerator {

	public final static String FALLBACK_SITENAME = "KIT SODA4LCA test site";

	private final Configuration properties;

	private final ConfigurationBean confBean = new ConfigurationBean();

	private String siteName = FALLBACK_SITENAME;

	public RegistrationEmailGenerator() {
		this.properties = ConfigurationService.INSTANCE.getProperties();
		this.siteName = this.properties.getString( "mail.sitename", RegistrationEmailGenerator.FALLBACK_SITENAME );
	}

	/**
	 * Generate an activation information mail for the provider of the web site.
	 * Mail will be sent to mail address configured in properties file
	 * (key: <code>user.registration.registrationAddress</code>)
	 * 
	 * @param user
	 *            user object to generate activation mail for
	 * @return the generated message
	 * @throws MessagingException
	 *             if an error occurs
	 */
	public Message genActivationMailForProvider( User user ) throws MessagingException {
		// initialize message, should set from address and mailhost information
		Message message = this.getInitializedMessage();

		String providerRegistrationAddress = this.properties.getString( "user.registration.registrationAddress" );
		// can only be called if address is set right
		if ( providerRegistrationAddress == null || providerRegistrationAddress.equals( "" ) ) {
			// throw new MessagingException( "Address for registration confirmation of provider is not set" );
			// this setting is optional, do not throw an exception, just do nothing
			return null;
		}
		InternetAddress to;
		try {
			to = new InternetAddress( providerRegistrationAddress );
			message.setRecipient( Message.RecipientType.TO, to );
		}
		catch ( MessagingException ex ) {
			throw new MessagingException( "Email address " + providerRegistrationAddress + " for registration confirmation seems to be wrong", ex );
		}

		// set subject of message
		message.setSubject( "A new user " + user.getUserName() + " has registered at your " + siteName + ". Please check the email body for activation details" );
		// set content of message
		VelocityContext context = this.getContext( user, siteName );
		setContentForActivationEmail( message, context, "/providerActivationMail.vm" );

		return message;
	}

	/**
	 * Generate an activation information mail for the user.
	 * 
	 * @param user
	 *            user object to generate activation mail for
	 * @param plainPassword
	 *            the plain text password that is only stored in an encrypted manner in the user object
	 * @return the generated message
	 * @throws MessagingException
	 *             if an error occurs
	 */
	public Message genActivationEmailForUser( User user, String plainPassword ) throws MessagingException {
		// initialize message, should set from address and mailhost information
		Message message = getInitializedMessage();

		// set to address
		InternetAddress to;
		try {
			to = new InternetAddress( user.getEmail() );
			message.setRecipient( Message.RecipientType.TO, to );
		}
		catch ( MessagingException ex ) {
			throw new MessagingException( "Is your email address " + user.getEmail() + " wrong? Cannot set your email address in registration email!", ex );
		}

		// set subject of message
		message.setSubject( "Please confirm your user registration at our " + siteName );
		// set content of message
		VelocityContext context = this.getContext( user, siteName );
		context.put( "plainPassword", plainPassword );
		setContentForActivationEmail( message, context, "/activationMail.vm" );

		return message;
	}

	/**
	 * Generate user information mail for created account
	 * 
	 * @param user
	 *            user object to generate message for
	 * @param plainPassword
	 *            the plain text password that is only stored in an encrypted manner in the user object
	 * @return the generated message
	 * @throws MessagingException
	 *             if an error occurs
	 */
	public Message genUserNotice( User user, String plainPassword ) throws MessagingException {
		// initialize message, should set from address and mailhost information
		Message message = getInitializedMessage();

		// set to address
		InternetAddress to;
		try {
			to = new InternetAddress( user.getEmail() );
			message.setRecipient( Message.RecipientType.TO, to );
		}
		catch ( MessagingException ex ) {
			throw new MessagingException( "Is your email address " + user.getEmail() + " wrong? Cannot set your email address in registration email!", ex );
		}

		// set subject of message
		message.setSubject( "Your user registration at our " + this.siteName );

		// set content of message
		String providerRegistrationAddress = properties.getString( "user.registration.registrationAddress" );

		VelocityContext context = this.getContext( user, this.siteName );
		context.put( "plainPassword", plainPassword );
		context.put( "registrationAddress", providerRegistrationAddress );

		this.setContentForActivationEmail( message, context, "/registrationMail.vm" );

		return message;
	}

	/**
	 * Create
	 * 
	 * @return
	 * @throws MessagingException
	 */
	private Message getInitializedMessage() throws MessagingException {
		String sender = this.properties.getString( "mail.sender", null );
		if ( sender == null ) {
			throw new MessagingException( "Please set a sender email address in configuration file (key: mail.sender)." );
		}

		String mailhost = this.properties.getString( "mail.hostname", null );
		if ( mailhost == null ) {
			throw new MessagingException( "Please set a smtp host name in configuration file (key: mail.hostname)." );
		}

		String port = this.properties.getString( "mail.port", "25" );

		Properties mailProps = new Properties();
		mailProps.put( "mail.smtp.host", mailhost );
		mailProps.put( "mail.smtp.port", port );

		Session session = null;
		Message message = null;
		String auth = this.properties.getString( "mail.auth", null );
		if ( auth != null && auth.contains( "true" ) ) {
			mailProps.setProperty( "mail.smtp.auth", "true" );

			String user = this.properties.getString( "mail.user", null );
			if ( user == null ) {
				throw new MessagingException( "Please set a valid User value in configuration file (key: mail.user)." );
			}

			String pwd = this.properties.getString( "mail.password", null );
			if ( pwd == null ) {
				throw new MessagingException( "Please set a valid Password value for the User in configuration file (key: mail.password)." );
			}

			MailAuthenticator authenticator = new MailAuthenticator( user, pwd );
			mailProps.setProperty( "mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName() );
			session = Session.getDefaultInstance( mailProps, authenticator );
			message = new MimeMessage( session );
		}
		else {
			session = Session.getDefaultInstance( mailProps );
			message = new MimeMessage( session );
		}

		InternetAddress from = null;
		try {
			from = new InternetAddress( sender );
			message.setFrom( from );
		}
		catch ( MessagingException ex ) {
			MessagingException exNew = new MessagingException(
					"Cannot initialize registration email message with sender and host information; Please contact the adminstration of the website", ex );
			throw exNew;
		}

		return message;
	}

	private VelocityContext getContext( User user, String siteName ) {
		// we generate the message with a velocity template
		VelocityContext context = VelocityUtil.getContext();
		context.put( "user", user );

		context.put( "siteName", siteName );

		return context;
	}

	private void setContentForActivationEmail( Message message, VelocityContext context, String templateName ) throws MessagingException {

		String activationUrl = this.confBean.getBaseUri().toString() + "/activation.xhtml";
		context.put( "activationUrl", activationUrl );
		String text = VelocityUtil.parseTemplate( templateName, context );
		// now let's set the body of the message
		try {
			message.setContent( text, "text/html" );
		}
		catch ( MessagingException ex ) {
			throw new MessagingException( "Cannot set content of activation email", ex );
		}
	}
}

class MailAuthenticator extends Authenticator {

	private PasswordAuthentication authentication;

	public MailAuthenticator() {
	}

	public MailAuthenticator( String user, String pwd ) {
		authentication = new PasswordAuthentication( user, pwd );
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return authentication;
	}

}
