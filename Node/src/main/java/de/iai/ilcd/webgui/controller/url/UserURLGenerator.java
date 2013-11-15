package de.iai.ilcd.webgui.controller.url;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class UserURLGenerator extends AbstractURLGenerator {

	public UserURLGenerator( URLGeneratorBean urlBean ) {
		super( urlBean );
	}

	public String getProfile() {
		return this.getProfile( this.getCurrentStockName() );
	}

	public String getProfile( String stockName ) {
		return "/profile.xhtml?" + super.getStockURLParam( stockName );
	}

	public String getLogout() {
		return this.getLogout( this.getCurrentStockName() );
	}

	public String getLogin() {
		return this.getLogin( this.getCurrentStockName() );
	}

	public String getLogin( String stockName ) {
		final ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
		String queryString = ((HttpServletRequest) econtext.getRequest()).getQueryString();
		if ( !StringUtils.isBlank( queryString ) ) {
			queryString = "?" + queryString;
		}
		else {
			queryString = "";
		}
		return "/login.xhtml?" + super.getStockURLParam( stockName ) + "&src=" + econtext.getRequestServletPath() + queryString;
	}

	public String getLogout( String stockName ) {
		return "/login.xhtml" + super.getStockURLParam( stockName, true );
	}

	public String getRegistration() {
		return this.getRegistration( this.getCurrentStockName() );
	}

	public String getRegistration( String stockName ) {
		return "/registration.xhtml" + this.getStockURLParam( stockName, true );
	}

	/**
	 * Get the URL to create new user (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getNew() {
		return this.getNew( this.getCurrentStockName() );

	}

	/**
	 * Get the URL to create new user
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getNew( String stockName ) {
		return "/admin/newUser.xhtml" + super.getStockURLParam( stockName, true );
	}

	/**
	 * Get the URL to edit user
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( String stockName ) {
		return this.getEdit( null, stockName );
	}

	/**
	 * Get the URL to edit user (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getEdit() {
		return this.getEdit( null, this.getCurrentStockName() );
	}

	/**
	 * Get the URL to edit user
	 * 
	 * @param uid
	 *            ID of the user to edit
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getEdit( Long uid, String stockName ) {
		return "/admin/showUser.xhtml?" + (uid != null ? "userId=" + uid.toString() + "&" : "") + super.getStockURLParam( stockName );
	}

	/**
	 * Get the URL to edit user (on current stock name)
	 * 
	 * @param uid
	 *            ID of the user to edit
	 * @return generated URL
	 */
	public String getEdit( Long uid ) {
		return this.getEdit( uid, this.getCurrentStockName() );
	}

	/**
	 * Get the URL to show user list (on current stock name)
	 * 
	 * @return generated URL
	 */
	public String getShowList() {
		return this.getShowList( this.getCurrentStockName() );

	}

	/**
	 * Get the URL to show user list
	 * 
	 * @param stockName
	 *            name of the stock
	 * @return generated URL
	 */
	public String getShowList( String stockName ) {
		return "/admin/manageUserList.xhtml" + super.getStockURLParam( stockName, true );
	}

}
