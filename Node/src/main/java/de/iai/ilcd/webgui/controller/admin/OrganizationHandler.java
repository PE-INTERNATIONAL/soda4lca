package de.iai.ilcd.webgui.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityTransaction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import de.iai.ilcd.model.common.GeographicalArea;
import de.iai.ilcd.model.dao.GeographicalAreaDao;
import de.iai.ilcd.model.dao.IndustrialSectorDao;
import de.iai.ilcd.model.dao.MergeException;
import de.iai.ilcd.model.dao.OrganizationDao;
import de.iai.ilcd.model.dao.UserDao;
import de.iai.ilcd.model.dao.UserGroupDao;
import de.iai.ilcd.model.security.IndustrialSector;
import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.model.security.UserGroup;
import de.iai.ilcd.persistence.PersistenceUtil;
import de.iai.ilcd.security.SecurityUtil;
import de.iai.ilcd.security.UserAccessBean;
import de.iai.ilcd.util.SodaUtil;
import de.iai.ilcd.webgui.controller.url.URLGeneratorBean;

/**
 * Admin handler for organization entries
 */
@ViewScoped
@ManagedBean( name = "orgHandler" )
public class OrganizationHandler extends AbstractAdminEntryHandler<Organization> {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 7604937473350763008L;

	/**
	 * Index of main information tab
	 */
	public final static int TAB_IDX_MAIN = 0;

	/**
	 * Index of assign user/group tab
	 */
	public final static int TAB_IDX_ASSIGN_USR_GRP = 1;

	/**
	 * Index of assign <b>admin</b> user/group tab
	 */
	public final static int TAB_IDX_ASSIGN_ADMIN_USR_GRP = 2;

	/**
	 * User access bean
	 */
	@ManagedProperty( value = "#{user}" )
	private UserAccessBean userAccessBean;

	/**
	 * List of available sectors
	 */
	private List<IndustrialSector> availableIndustrialSectors = null;

	/**
	 * List of available countries
	 */
	private List<GeographicalArea> availableCountries = null;

	/**
	 * The chosen country
	 */
	private GeographicalArea chosenCountry;

	/**
	 * Dual list model for user assignment
	 */
	private DualListModel<User> dualUserAssignmentList;

	/**
	 * Dual list model for group assignment
	 */
	private DualListModel<UserGroup> dualGroupAssignmentList;

	/**
	 * Get the user access bean
	 * 
	 * @return user access bean
	 */
	public UserAccessBean getUserAccessBean() {
		return this.userAccessBean;
	}

	/**
	 * Set the user access bean
	 * 
	 * @param userAccessBean
	 *            user access bean to set
	 */
	public void setUserAccessBean( UserAccessBean userAccessBean ) {
		this.userAccessBean = userAccessBean;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Organization createEmptyEntryInstance() {
		return new Organization();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Organization loadEntryInstance( long id ) throws Exception {
		OrganizationDao dao = new OrganizationDao();
		return dao.getById( id );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void postEntrySet() {
		final Organization o = this.getEntry();
		if ( o != null && o.getAddress() != null && !StringUtils.isBlank( o.getAddress().getCountry() ) ) {
			GeographicalAreaDao gDao = new GeographicalAreaDao();
			this.chosenCountry = gDao.getArea( o.getAddress().getCountry() );
		}

		UserDao uDao = new UserDao();
		UserGroupDao ugDao = new UserGroupDao();
		final List<User> orgUsers = o.getUsers();
		final List<UserGroup> orgGroup = o.getGroups();

		final List<User> availableOrgLessUsers = uDao.getOrgLessUsers();
		final List<UserGroup> availableOrgLessGroups = ugDao.getOrgLessGroups();

		this.dualUserAssignmentList = new DualListModel<User>( availableOrgLessUsers, orgUsers );
		this.dualGroupAssignmentList = new DualListModel<UserGroup>( availableOrgLessGroups, orgGroup );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void postConstruct() {
		SecurityUtil.assertSuperAdminPermission();

		IndustrialSectorDao secDao = new IndustrialSectorDao();
		this.availableIndustrialSectors = secDao.getAllOrdered();

		GeographicalAreaDao gDao = new GeographicalAreaDao();
		this.availableCountries = gDao.getCountries();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createEntry() {
		OrganizationDao dao = new OrganizationDao();
		if ( this.checkForDuplicate( dao, this.getEntry() ) ) {
			try {
				final Organization o = this.getEntry();
				dao.persist( o );
				this.addI18NFacesMessage( "facesMsg.org.saveSuccess", FacesMessage.SEVERITY_INFO );
				return true;
			}
			catch ( Exception e ) {
				this.addI18NFacesMessage( "facesMsg.org.saveError", FacesMessage.SEVERITY_ERROR );
				return false;
			}
		}
		else {
			this.addI18NFacesMessage( "facesMsg.org.alreadyExists", FacesMessage.SEVERITY_ERROR );
			return false;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean changeAttachedEntry() {
		OrganizationDao dao = new OrganizationDao();
		final Organization o = this.getEntry();

		// check if admin user still in organization
		if ( o.getAdminUser() != null && !this.dualUserAssignmentList.getTarget().contains( o.getAdminUser() ) ) {
			this.addI18NFacesMessage( "facesMsg.org.adminUserNoMember", FacesMessage.SEVERITY_ERROR );
			return false;
		}

		// check if admin group still in organization
		if ( o.getAdminGroup() != null && !this.dualGroupAssignmentList.getTarget().contains( o.getAdminGroup() ) ) {
			this.addI18NFacesMessage( "facesMsg.org.adminGroupNoMember", FacesMessage.SEVERITY_ERROR );
			return false;
		}

		if ( this.checkForDuplicate( dao, o ) ) {
			try {
				this.setEntry( dao.merge( this.getEntry() ) );
				if ( !this.handleOrganizationAssignment( o, this.dualUserAssignmentList, this.dualGroupAssignmentList ) ) {
					this.addI18NFacesMessage( "facesMsg.org.changeError", FacesMessage.SEVERITY_ERROR );
					return false;
				}
				this.addI18NFacesMessage( "facesMsg.org.changeSuccess", FacesMessage.SEVERITY_INFO );
				return true;
			}
			catch ( Exception e ) {
				this.addI18NFacesMessage( "facesMsg.org.changeError", FacesMessage.SEVERITY_ERROR );
				return false;
			}
		}
		else {
			this.addI18NFacesMessage( "facesMsg.org.alreadyExists", FacesMessage.SEVERITY_ERROR );
			return false;
		}
	}

	/**
	 * Handle the organization assignment
	 * 
	 * @param o
	 *            organization
	 * @param userAssignment
	 *            dual list with assigned users
	 * @param groupAssignment
	 *            dual list with assigned groups
	 * @throws MergeException
	 *             on merge errors
	 */
	private boolean handleOrganizationAssignment( Organization o, DualListModel<User> userAssignment, DualListModel<UserGroup> groupAssignment )
			throws MergeException {
		EntityTransaction t = PersistenceUtil.getEntityManager().getTransaction();
		try {
			t.begin();
			UserDao uDao = new UserDao();
			UserGroupDao ugDao = new UserGroupDao();

			List<User> usrToMerge = new ArrayList<User>();
			List<UserGroup> grpToMerge = new ArrayList<UserGroup>();

			// set all source list items organization to null where not already
			for ( User u : userAssignment.getSource() ) {
				if ( u.getOrganization() != null ) {
					u = this.ensureObjectIsAttachedToEM( u );
					u.setOrganization( null );
					usrToMerge.add( u );
				}
			}

			// set all target list items organization to this org where not already
			for ( User u : userAssignment.getTarget() ) {
				if ( u.getOrganization() == null ) {
					u = this.ensureObjectIsAttachedToEM( u );
					u.setOrganization( o );
					usrToMerge.add( u );
				}
			}

			// set all source list items organization to null where not already
			for ( UserGroup g : groupAssignment.getSource() ) {
				if ( g.getOrganization() != null ) {
					g = this.ensureObjectIsAttachedToEM( g );
					g.setOrganization( null );
					grpToMerge.add( g );
				}
			}

			// set all target list items organization to this org where not already
			for ( UserGroup g : groupAssignment.getTarget() ) {
				if ( g.getOrganization() == null ) {
					g = this.ensureObjectIsAttachedToEM( g );
					g.setOrganization( o );
					grpToMerge.add( g );
				}
			}

			t.commit();
			uDao.merge( usrToMerge );
			ugDao.merge( grpToMerge );
			return true;
		}
		catch ( Exception e ) {
			t.rollback();
			return false;
		}
	}

	/**
	 * Check for a duplicate
	 * 
	 * @param dao
	 *            DAO to use
	 * @param o
	 *            organization to check
	 * @return <code>true</code> if check was successful and there is <b>no duplicate</b>, <code>false</code> otherwise
	 */
	private boolean checkForDuplicate( OrganizationDao dao, Organization o ) {
		Organization existing = dao.getByName( o.getName() );
		if ( existing == null ) {
			return true;
		}
		if ( o.getId() != null ) {
			return o.getId().equals( existing.getId() );
		}
		return false;
	}

	/**
	 * Get the available industrial sectors
	 * 
	 * @return available industrial sectors
	 */
	public List<IndustrialSector> getAvailableIndustrialSectors() {
		return this.availableIndustrialSectors;
	}

	/**
	 * Get the available countries
	 * 
	 * @return available countries
	 */
	public List<GeographicalArea> getAvailableCountries() {
		return this.availableCountries;
	}

	/**
	 * Set the chosen country
	 * 
	 * @param a
	 *            country to set
	 */
	public void setChosenCountry( GeographicalArea a ) {
		this.chosenCountry = a;
		this.getEntry().getAddress().setCountry( a != null ? a.getAreaCode() : "" );
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
	 * Dual list model for the pick list to select organization groups
	 * 
	 * @return Dual list model for the pick list to select organization groups
	 */
	public DualListModel<UserGroup> getDualGroupAssignmentList() {
		return this.dualGroupAssignmentList;
	}

	/**
	 * Dual list model for the pick list to select organization users
	 * 
	 * @return Dual list model for the pick list to select organization users
	 */
	public DualListModel<User> getDualUserAssignmentList() {
		return this.dualUserAssignmentList;
	}

	/**
	 * Set the dual list model for the pick list to select organization groups
	 * 
	 * @param dualGroupAssignmentList
	 *            dual list to set
	 */
	public void setDualGroupAssignmentList( DualListModel<UserGroup> dualGroupAssignmentList ) {
		// JSF would set an empty list if PickList was not displayed prior to save action
		if ( SodaUtil.isTotalElementCountEqual( this.dualGroupAssignmentList, dualGroupAssignmentList ) ) {
			this.dualGroupAssignmentList = dualGroupAssignmentList;
		}
	}

	/**
	 * Set the dual list model for the pick list to select organization users
	 * 
	 * @param dualUserAssignmentList
	 *            dual list to set
	 */
	public void setDualUserAssignmentList( DualListModel<User> dualUserAssignmentList ) {
		// JSF would set an empty list if PickList was not displayed prior to save action
		if ( SodaUtil.isTotalElementCountEqual( this.dualUserAssignmentList, dualUserAssignmentList ) ) {
			this.dualUserAssignmentList = dualUserAssignmentList;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditEntryUrl( URLGeneratorBean url, Long id ) {
		return url.getOrg().getEdit( id );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCloseEntryUrl( URLGeneratorBean url ) {
		return url.getOrg().getManageList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNewEntryUrl( URLGeneratorBean url ) {
		return url.getOrg().getNew();
	}

	/**
	 * Determine if <b>admin</b> user/group management tab should be disabled
	 * 
	 * @return <code>true</code> if tab should be disabled, <code>false</code> otherwise
	 */
	public boolean isAssignAdmUsrGrpTabDisabled() {
		// if create view -> tab is disabled (return true)
		if ( this.isCreateView() ) {
			return true;
		}
		else {
			// no assigned groups and no assigned users -> tab is disabled (return true)
			// either assigned groups or assigned users -> tab is enabled (return false)
			return CollectionUtils.isEmpty( this.dualUserAssignmentList.getTarget() ) && CollectionUtils.isEmpty( this.dualGroupAssignmentList.getTarget() );
		}
	}

	/**
	 * @see SodaUtil#noUsersHint(UserGroup)
	 */
	public String noUsersHint( UserGroup g ) {
		return SodaUtil.noUsersHint( g, this.getI18n() );
	}

	/**
	 * @see SodaUtil#adminHint(UserGroup, Organization, java.util.ResourceBundle)
	 */
	public String adminGroupHint( UserGroup g ) {
		return SodaUtil.adminHint( g, this.getEntry(), this.getI18n() );
	}

	/**
	 * @see SodaUtil#adminHint(User, Organization, java.util.ResourceBundle)
	 */
	public String adminUserHint( User u ) {
		return SodaUtil.adminHint( u, this.getEntry(), this.getI18n() );
	}

	/**
	 * Handles transfer of user or group picklist (active tab fix)
	 * 
	 * @param event
	 *            JSF transfer event
	 */
	public void handleUserGroupTransfer( TransferEvent event ) {
		// prevents JSF from jumping to wrong tab in some cases
		this.setActiveTabIndex( OrganizationHandler.TAB_IDX_ASSIGN_USR_GRP );
	}

}
