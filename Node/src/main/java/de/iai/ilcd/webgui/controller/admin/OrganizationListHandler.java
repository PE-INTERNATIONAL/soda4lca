package de.iai.ilcd.webgui.controller.admin;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.primefaces.model.SortOrder;

import de.iai.ilcd.model.dao.OrganizationDao;
import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.util.SodaUtil;

/**
 * Admin handler for organizations
 */
@ViewScoped
@ManagedBean( name = "orgListHandler" )
public class OrganizationListHandler extends AbstractAdminListHandler<Organization> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -8296677018448858250L;

	/**
	 * DAO for model access
	 */
	private final OrganizationDao dao = new OrganizationDao();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long loadElementCount() {
		return this.dao.getAllCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void postConstruct() {
	}

	/**
	 * Set the selected organizations.
	 * <p>
	 * <b>Do not replace</b> by {@link AbstractAdminListHandler#setSelectedItems(Object[]) setSelectedItems} in Facelets
	 * (see it's documentation for the reason)
	 * </p>
	 * 
	 * @param selected
	 *            selected organizations
	 */
	public void setSelectedOrganizations( Organization[] selected ) {
		super.setSelectedItems( selected );
	}

	/**
	 * Get the selected organizations.
	 * <p>
	 * <b>Do not replace</b> by {@link AbstractAdminListHandler#getSelectedItems() getSelectedItems} in Facelets (see
	 * it's documentation for the reason)
	 * </p>
	 * 
	 * @return selected organizations
	 */
	public Organization[] getSelectedOrganizations() {
		return super.getSelectedItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSelected() {
		final Organization[] selected = this.getSelectedItems();
		if ( selected == null ) {
			return;
		}

		for ( Organization org : selected ) {
			// Default organization not deletable
			if ( ObjectUtils.equals( org.getId(), SodaUtil.DEFAULT_ORGANIZATION_ID ) ) {
				continue;	// not selectable in facelet and not deletable (double check)
			}
			try {
				this.dao.remove( org );
				this.addI18NFacesMessage( "facesMsg.removeSuccess", FacesMessage.SEVERITY_INFO, org.getName() );
			}
			catch ( Exception e ) {
				this.addI18NFacesMessage( "facesMsg.removeError", FacesMessage.SEVERITY_ERROR, org.getName() );
			}
		}

		this.clearSelection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Organization> lazyLoad( int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
		return this.dao.get( first, pageSize );
	}

	/**
	 * Determine if <b>admin</b> user/group management button should be disabled
	 * 
	 * @param o
	 *            organization
	 * @return <code>true</code> if button should be disabled, <code>false</code> otherwise
	 */
	public boolean isAssignAdmUsrGrpBtnDisabled( Organization o ) {
		if ( o == null ) {
			return true;
		}
		// no assigned groups and no assigned users -> button is disabled (return true)
		// either assigned groups or assigned users -> button is enabled (return false)
		return CollectionUtils.isEmpty( o.getUsers() ) && CollectionUtils.isEmpty( o.getGroups() );
	}

}
