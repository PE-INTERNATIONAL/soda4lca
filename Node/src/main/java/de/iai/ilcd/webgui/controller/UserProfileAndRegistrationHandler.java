package de.iai.ilcd.webgui.controller;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.common.GeographicalArea;
import de.iai.ilcd.model.dao.GeographicalAreaDao;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.security.IlcdSecurityRealm;
import de.iai.ilcd.security.SecurityUtil;
import de.iai.ilcd.security.UserAccessBean;
import de.iai.ilcd.webgui.controller.admin.RandomPassword;
import de.iai.ilcd.webgui.controller.admin.RegistrationEmailGenerator;

@ViewScoped
@ManagedBean( name = "uparHandler" )
public class UserProfileAndRegistrationHandler extends AbstractHandler {

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger( UserProfileAndRegistrationHandler.class );

	/**
	 * User access bean
	 */
	@ManagedProperty( value = "#{user}" )
	private UserAccessBean userBean;

	/**
	 * User DAO
	 */
	private final UserDao dao = new UserDao();

	/**
	 * The plain password
	 */
	private String plainPassword;

	/**
	 * The repeated password (verification)
	 */
	private String verifyPassword;

	/**
	 * User instance
	 */
	private User user = new User();

	/**
	 * List of countries
	 */
	private final List<GeographicalArea> countries;

	/**
	 * Value of spam slider
	 */
	private int spamSliderValue = 0;

	/**
	 * Chosen country of address
	 */
	private GeographicalArea chosenCountry;

	public UserProfileAndRegistrationHandler() {
		GeographicalAreaDao dao = new GeographicalAreaDao();
		this.countries = dao.getCountries();
	}

	public void postViewParamInitProfile() {

		if ( !this.userBean.isLoggedIn() ) {
			this.addI18NFacesMessage( "facesMsg.user.changeProfile", FacesMessage.SEVERITY_ERROR );
			return;
		}
		User currentUser = this.dao.getUser( this.userBean.getUserName() );
		if ( currentUser == null ) {
			this.addI18NFacesMessage( "facesMsg.user.noProfile", FacesMessage.SEVERITY_ERROR, this.userBean.getUserName() );
			return;
		}

		// throws exception if permission not present
		SecurityUtil.assertCanWrite( currentUser, this.getGenericRoleError() );

		this.user = currentUser;

		// load the chosen country
		if ( this.user != null && this.user.getAddress() != null && !StringUtils.isBlank( this.user.getAddress().getCountry() ) ) {
			GeographicalAreaDao dao = new GeographicalAreaDao();
			this.chosenCountry = dao.getArea( this.user.getAddress().getCountry() );
		}

	}

	/**
	 * Register a new user
	 * 
	 * @return target view
	 * @throws Exception
	 */
	public String doRegister() throws Exception {
		Configuration conf = ConfigurationService.INSTANCE.getProperties();

		if ( this.spamSliderValue != 100 ) {
			this.addI18NFacesMessage( "facesMsg.registration.captcha.slider.invalidValue", FacesMessage.SEVERITY_ERROR );
			return null;
		}

		// check properties first
		if ( !conf.getBoolean( "user.registration.activated", Boolean.FALSE ) ) {
			this.addI18NFacesMessage( "facesMsg.user.noRegistration", FacesMessage.SEVERITY_ERROR );
			return null;
		}

		UserDao dao = new UserDao();

		if ( this.user.getUserName() == null ) {
			this.addI18NFacesMessage( "facesMsg.user.uniqueUserName", FacesMessage.SEVERITY_ERROR );
			return null;
		}

		User existingUser = dao.getUser( this.user.getUserName() );
		if ( existingUser != null ) {
			this.addI18NFacesMessage( "facesMsg.user.alreadyExists", FacesMessage.SEVERITY_ERROR );
			return null;
		}

		// generate password, salt and encrypted password and activation key
		String plainPassword = RandomPassword.getPassword( 8 );
		this.user.setPasswordHashSalt( this.generateSalt() );
		this.user.setPasswordHash( IlcdSecurityRealm.getEncryptedPassword( plainPassword, this.user.getPasswordHashSalt() ) );

		String registrationKey = RandomPassword.getPassword( 20 );
		this.user.setRegistrationKey( registrationKey );

		// generate Email messages
		Message userMessage = null;
		Message providerMessage = null;
		RegistrationEmailGenerator emailGenerator = new RegistrationEmailGenerator();
		boolean userSelfActivation = false;
		// get property, how user activation should be performed
		userSelfActivation = conf.getBoolean( "user.registration.selfActivation", Boolean.FALSE );
		try {
			if ( userSelfActivation ) {
				// we send only an email to the user because he can activate his account himself
				userMessage = emailGenerator.genActivationEmailForUser( this.user, plainPassword );
			}
			else {
				// user only get's a notification about the completed registration
				userMessage = emailGenerator.genUserNotice( this.user, plainPassword );
				// provider get's an email to activate the user
				providerMessage = emailGenerator.genActivationMailForProvider( this.user );
			}
		}
		catch ( MessagingException ex ) {
			// OK, cannot generate email, let's show the error message to the user
			FacesContext.getCurrentInstance().addMessage( null, new FacesMessage( FacesMessage.SEVERITY_ERROR, ex.getMessage(), null ) );
			UserProfileAndRegistrationHandler.logger.error( ex.getMessage(), ex );
			return null;
		}

		try {
			dao.persist( this.user );
			if ( userMessage != null ) {
				Transport.send( userMessage );
			}
			if ( providerMessage != null ) {
				Transport.send( providerMessage );
			}
		}
		catch ( Exception ex ) {
			dao.remove( this.user );
			this.addI18NFacesMessage( "facesMsg.user.registrationError", FacesMessage.SEVERITY_ERROR );
			UserProfileAndRegistrationHandler.logger.error( "There was an error while saving registration information or sending registration email", ex );
			return null;
		}

		return "registrated";

	}

	/**
	 * Change the profile
	 */
	public void changeProfile() {

		SecurityUtil.assertCanWrite( this.user, "You are not permitted to edit this profile." );

		UserAccessBean userAccessBean = new UserAccessBean();
		if ( userAccessBean == null || !userAccessBean.isLoggedIn() ) {
			String msg1 = this.getI18n().getString( "facesMsg.user.notLoggedIn" );
			String msg2 = this.getI18n().getString( "facesMsg.user.noUserManagementRights" );
			String completeMsg = msg1.concat( " " ).concat( msg2 );
			FacesContext.getCurrentInstance().addMessage( null, new FacesMessage( FacesMessage.SEVERITY_ERROR, completeMsg, null ) );
			return;
		}

		if ( this.user.getUserName() == null ) {
			this.addI18NFacesMessage( "facesMsg.user.uniqueUserName", FacesMessage.SEVERITY_ERROR );
			return;
		}
		if ( this.plainPassword == null || this.plainPassword.trim().isEmpty() ) {
			// OK, we don't change the password
		}
		// check if verify entry and plain text password are the same
		else if ( this.verifyPassword == null || !this.verifyPassword.equals( this.plainPassword ) ) {
			this.addI18NFacesMessage( "facesMsg.user.pwNotSame", FacesMessage.SEVERITY_ERROR );
			return;
		}
		// OK, let's change the PW
		else {
			// new salt
			this.user.setPasswordHashSalt( this.generateSalt() );
			// encrpyt new pw
			this.user.setPasswordHash( IlcdSecurityRealm.getEncryptedPassword( this.plainPassword, this.user.getPasswordHashSalt() ) );
		}

		final Long currentId = this.user.getId();
		User existingUser = this.dao.getUser( this.user.getUserName() );
		if ( existingUser != null && !existingUser.getId().equals( currentId ) ) {
			this.addI18NFacesMessage( "facesMsg.user.alreadyExists", FacesMessage.SEVERITY_ERROR );
			return;
		}

		try {
			this.user = this.dao.merge( this.user );
			this.addI18NFacesMessage( "facesMsg.user.accountChanged", FacesMessage.SEVERITY_INFO );
		}
		catch ( Exception e ) {
			this.addI18NFacesMessage( "facesMsg.saveDataError", FacesMessage.SEVERITY_ERROR );
		}

	}

	public User getUser() {
		return this.user;
	}

	public String getPlainPassword() {
		return this.plainPassword;
	}

	public void setPlainPassword( String plainPassword ) {
		this.plainPassword = plainPassword;
	}

	public void setVerifyPassword( String verifyPassword ) {
		this.verifyPassword = verifyPassword;
	}

	public String getVerifyPassword() {
		return this.verifyPassword;
	}

	public List<GeographicalArea> getCountries() {
		return this.countries;
	}

	/**
	 * Set the chosen country
	 * 
	 * @param a
	 *            country to set
	 */
	public void setChosenCountry( GeographicalArea a ) {
		this.chosenCountry = a;
		this.user.getAddress().setCountry( a != null ? a.getAreaCode() : "" );
	}

	/**
	 * Get the chosen country
	 * 
	 * @return chosen country
	 */
	public GeographicalArea getChosenCountry() {
		return this.chosenCountry;
	}

	/**
	 * Generate a new salt (ensures correct length)
	 * 
	 * @return new salt
	 */
	protected String generateSalt() {
		return RandomPassword.getPassword( 20 );
	}

	/**
	 * Method for the injection of user access bean
	 * 
	 * @param userBean
	 *            user access bean to set
	 */
	public void setUserBean( UserAccessBean userBean ) {
		this.userBean = userBean;
	}

	/**
	 * Get the user access bean
	 * 
	 * @return user access bean
	 */
	public UserAccessBean getUserBean() {
		return this.userBean;
	}

	/**
	 * Check if create view (PW field required)
	 * 
	 * @return <code>false</code>
	 */
	public boolean isCreateView() {
		return false;
	}

	/**
	 * Get the value for the spam slider
	 * 
	 * @return value of the spam slider
	 */
	public int getSpamSliderValue() {
		return this.spamSliderValue;
	}

	/**
	 * Set the value for the spam slider
	 * 
	 * @param spamSliderValue
	 *            value to set
	 */
	public void setSpamSliderValue( int spamSliderValue ) {
		this.spamSliderValue = spamSliderValue;
	}

	public User getEntry() {
		return this.user;
	}
}
