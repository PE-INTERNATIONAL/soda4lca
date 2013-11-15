package de.iai.ilcd.webgui.controller.admin;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;

import de.iai.ilcd.model.ILongIdObject;
import de.iai.ilcd.persistence.PersistenceUtil;
import de.iai.ilcd.webgui.controller.AbstractHandler;
import de.iai.ilcd.webgui.controller.url.URLGeneratorBean;

/**
 * Base class for all lists manages beans
 * 
 * @param <T>
 *            list item type
 */
public abstract class AbstractAdminEntryHandler<T extends ILongIdObject> extends AbstractHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 6415662747167696159L;

	/**
	 * Selected items
	 */
	private T entry;

	/**
	 * The entry id
	 */
	private String entryIdString;

	/**
	 * The index of the active (shown) tab in PrimeFaces TabView
	 */
	private Integer activeTabIndex;

	/**
	 * Flag to indicate if current view is a create view
	 */
	private boolean createView;

	/**
	 * URL generator for saveXXXX response
	 */
	@ManagedProperty( "#{url}" )
	private URLGeneratorBean urlGenerator;

	/**
	 * Create the handler
	 */
	public AbstractAdminEntryHandler() {
	}

	/**
	 * Get the entry id as string
	 * 
	 * @return entry id as string
	 */
	public String getEntryIdString() {
		return this.entryIdString;
	}

	/**
	 * Set the entry id as string
	 * 
	 * @param entryIdString
	 *            id as string to set
	 */
	public void setEntryIdString( String entryIdString ) {
		this.entryIdString = entryIdString;
	}

	/**
	 * Get the index of the active tab
	 * 
	 * @return the index of the active tab
	 */
	public Integer getActiveTabIndex() {
		return this.activeTabIndex;
	}

	/**
	 * Set the the index of the active tab
	 * 
	 * @param activeTabIndex
	 *            the index of the active tab
	 */
	public void setActiveTabIndex( Integer activeTabIndex ) {
		this.activeTabIndex = activeTabIndex;
	}

	/**
	 * Determine if this is a create view
	 * 
	 * @return <code>true</code> for create view, <code>false</code> otherwise
	 */
	public boolean isCreateView() {
		return this.createView;
	}

	/**
	 * Triggered after setting of view params
	 */
	public final void postViewParamInit() {
		if ( this.entry == null ) {
			if ( StringUtils.isBlank( this.entryIdString ) ) {
				this.entry = this.createEmptyEntryInstance();
				this.createView = true;
			}
			else {
				try {
					this.entry = this.loadEntryInstance( Long.parseLong( this.entryIdString ) );
					this.createView = false;
				}
				catch ( Exception e ) {
					this.entry = this.createEmptyEntryInstance();
					this.createView = true;
				}
			}
			this.postEntrySet();
		}
	}

	/**
	 * Create new, empty instance of an entry
	 * 
	 * @return new entry instance
	 */
	protected abstract T createEmptyEntryInstance();

	/**
	 * Load instance of an entry by id
	 * 
	 * @param id
	 *            id of entry
	 * @return loaded entry instance
	 * @throws Exception
	 *             if no entry found or errors occurred
	 */
	protected abstract T loadEntryInstance( long id ) throws Exception;

	/**
	 * Do everything here that depends on injected beans
	 */
	protected abstract void postConstruct();

	/**
	 * Override this method to do anything that depends on a set entry
	 */
	protected void postEntrySet() {

	}

	/**
	 * Called after dependency injection
	 */
	@PostConstruct
	public final void init() {
		this.postConstruct();
	}

	/**
	 * Get the entry
	 * 
	 * @return entry
	 */
	public T getEntry() {
		return this.entry;
	}

	/**
	 * Set the entry
	 * 
	 * @param entry
	 *            entry to set
	 */
	public void setEntry( T entry ) {
		this.entry = entry;
	}

	/**
	 * Create the entry
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	public abstract boolean createEntry();

	/**
	 * Change the entry
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	public abstract boolean changeAttachedEntry();

	/**
	 * First re-attach entry to EM by calling {@link EntityManager#merge(Object)},
	 * then call {@link #changeAttachedEntry()}.
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	public boolean changeEntry() {
		this.ensureEntryIsAttachedToEM();
		return this.changeAttachedEntry();
	}

	/**
	 * Ensure an object is managed by current EM. If not ({@link EntityManager#contains(Object)} returns
	 * <code>false</code>)
	 * attach it via {@link EntityManager#merge(Object)}.
	 * 
	 * @param <TYPE>
	 *            object type
	 * @param o
	 *            object to check
	 * @return attached object
	 */
	public <TYPE> TYPE ensureObjectIsAttachedToEM( TYPE o ) {
		EntityManager em = PersistenceUtil.getEntityManager();
		if ( !em.contains( o ) ) {
			return (TYPE) em.merge( o );
		}
		else {
			return o;
		}
	}

	/**
	 * Ensure that current entry is attached to EM
	 */
	public void ensureEntryIsAttachedToEM() {
		if ( this.entry != null ) {
			this.entry = this.ensureObjectIsAttachedToEM( this.entry );
		}
	}

	/**
	 * Get the URL for edit entry, used by {@link #saveEntry()}
	 * 
	 * @param url
	 *            URL generator
	 * @param id
	 *            ID of entry
	 * @return edit URL
	 */
	public abstract String getEditEntryUrl( URLGeneratorBean url, Long id );

	/**
	 * Get the URL for edit entry, used by {@link #saveCloseEntry()}
	 * 
	 * @param url
	 *            URL generator
	 * @return close URL (list view)
	 */
	public abstract String getCloseEntryUrl( URLGeneratorBean url );

	/**
	 * Get the URL for new entry, used by {@link #saveNewEntry()}
	 * 
	 * @param url
	 *            URL generator
	 * @return new URL (list view)
	 */
	public abstract String getNewEntryUrl( URLGeneratorBean url );

	/**
	 * Save the entry, this means:
	 * <ul>
	 * <li><code>{@link #isCreateView()}==true</code> &rArr; {@link #createEntry()}</li>
	 * <li><code>{@link #isCreateView()}==false</code> &rArr; {@link #changeEntry()}</li>
	 * </ul>
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	private boolean saveEntryInternal() {
		if ( this.isCreateView() ) {
			return this.createEntry();
		}
		else {
			return this.changeEntry();
		}
	}

	/**
	 * Save Entry and get edit URL
	 * 
	 * @return URL to edit entry (including faces redirect if currently in {@link #isCreateView() create view}
	 * @see #getEditEntryUrl(URLGeneratorBean,Long)
	 */
	public final String saveEntry() {
		if ( this.saveEntryInternal() ) {
			if ( this.isCreateView() ) {
				return this.getEditEntryUrl( this.urlGenerator, this.getEntry().getId() ) + "&" + URLGeneratorBean.FACES_REDIRECT_TRUE;
			}
			else {
				return null; // stay on view
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Save entry and get list url
	 * 
	 * @return URL to list, always includes faces redirect
	 * @see #getCloseEntryUrl(URLGeneratorBean)
	 */
	public final String saveCloseEntry() {
		if ( this.saveEntryInternal() ) {
			return this.getCloseEntryUrl( this.urlGenerator ) + "&" + URLGeneratorBean.FACES_REDIRECT_TRUE;
		}
		else {
			return null;
		}
	}

	/**
	 * Save Entry and get new URL
	 * 
	 * @return URL to edit entry (including faces redirect if currently <b>not</b> in {@link #isCreateView() create
	 *         view}
	 * @see #getNewEntryUrl(URLGeneratorBean)
	 */
	public final String saveNewEntry() {
		if ( this.saveEntryInternal() ) {
			this.setEntry( this.createEmptyEntryInstance() );
			return this.getNewEntryUrl( this.urlGenerator ) + "&" + URLGeneratorBean.FACES_REDIRECT_TRUE;
		}
		else {
			return null;
		}
	}

	/**
	 * Getter for URL generator (injected)
	 * 
	 * @return the injected URL generator
	 */
	public URLGeneratorBean getUrlGenerator() {
		return this.urlGenerator;
	}

	/**
	 * Set the URL generator (is being injected)
	 * 
	 * @param urlGenerator
	 *            generator to set
	 */
	public void setUrlGenerator( URLGeneratorBean urlGenerator ) {
		this.urlGenerator = urlGenerator;
	}
}
