/*******************************************************************************
 * Copyright (c) 2011 Karlsruhe Institute of Technology (KIT) - Institute for
 * Applied Computer Science (IAI).
 * 
 * This file is part of soda4LCA - the Service-Oriented Life Cycle Data Store.
 * 
 * soda4LCA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * soda4LCA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with soda4LCA. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package de.iai.ilcd.webgui.controller.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.model.DualListModel;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import de.fzk.iai.ilcd.service.model.enums.TypeOfProcessValue;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.common.GeographicalArea;
import de.iai.ilcd.model.dao.NetworkNodeDao;
import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.model.utils.DistributedSearchLog;
import de.iai.ilcd.services.RegistryService;

/**
 * Backing bean for processes search
 */
@ManagedBean
@ViewScoped
public class ProcessesSearchHandler extends ProcessesHandler {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -8234619084745241858L;

	/**
	 * Filter key for lower reference year. Do not change value, is same as in createQueryObject for ProcessDAO!
	 */
	private final static String FILTER_REF_YEAR_LOWER = "referenceYearLower";

	/**
	 * Filter key for upper reference year. Do not change value, is same as in createQueryObject for ProcessDAO!
	 */
	private final static String FILTER_REF_YEAR_UPPER = "referenceYearUpper";

	/**
	 * Filter key for locations. Do not change value, is same as in createQueryObject for ProcessDAO!
	 */
	private final static String FILTER_LOCATIONS = "location";

	/**
	 * Filter key for type. Do not change value, is same as in createQueryObject for ProcessDAO!
	 */
	private final static String FILTER_TYPE = "type";

	/**
	 * Filter key for parameterized only. Do not change value, is same as in createQueryObject for ProcessDAO!
	 */
	private final static String FILTER_PARAMETERIZED = "parameterized";

	/**
	 * Filter key for distributed search. Do not change value, is same as in createQueryObject for DataSetDAO!
	 */
	private final static String FILTER_DISTRIBUTED = "distributed";

	/**
	 * Filter key for description. Do not change value, is same as in createQueryObject for DataSetDAO!
	 */
	private final static String FILTER_DESCRIPTION = "description";

	/**
	 * Filter key for classes. Do not change value, is same as in createQueryObject for DataSetDAO!
	 */
	private final static String FILTER_CLASSES = "classes";

	private static final String FILTER_REGISTRY_UUID = "registeredIn";

	private static final String FILTER_REGISTRY_VIRTUAL = "virtual";

	/**
	 * List of all locations from database
	 */
	private final List<SelectItem> allLocations;

	/**
	 * List of all reference years from database
	 */
	private final List<SelectItem> referenceYears = new ArrayList<SelectItem>();

	/**
	 * List of types from database
	 */
	private final List<SelectItem> types;

	/**
	 * List of all classes as dual list for pick list from database
	 */
	private DualListModel<String> pickAllClasses;

	/**
	 * Flag if search was performed
	 */
	private boolean searched = false;

	private Long registryId;

	private final RegistryService registryService;

	/**
	 * Initialize the handler, get all lists that well be needed
	 */
	public ProcessesSearchHandler() {

		// Load all locations
		this.allLocations = new ArrayList<SelectItem>();
		for ( GeographicalArea geoArea : super.getDaoInstance().getAllLocations() ) {
			this.allLocations.add( new SelectItem( geoArea.getAreaCode(), geoArea.getName() ) );
		}

		// Load all years
		for ( Integer refYear : super.getDaoInstance().getReferenceYears() ) {
			if ( refYear != null ) {
				this.referenceYears.add( new SelectItem( refYear.toString() ) );
			}
		}

		// get all types
		this.types = new ArrayList<SelectItem>();
		for ( TypeOfProcessValue o : TypeOfProcessValue.values() ) {
			this.types.add( new SelectItem( o, o.value() ) );
		}

		// Load all classes for pick list
		List<String> allClassesSource = new ArrayList<String>();
		List<String> allClassesTarget = new ArrayList<String>();
		for ( String topClassStr : super.getDaoInstance().getTopClasses() ) {
			for ( String subClassStr : super.getDaoInstance().getSubClasses( topClassStr, "1" ) ) {
				allClassesSource.add( subClassStr );
			}
		}
		this.pickAllClasses = new DualListModel<String>( allClassesSource, allClassesTarget );

		WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext( FacesContext.getCurrentInstance() );
		this.registryService = ctx.getBean( RegistryService.class );

	}

	/**
	 * Get the select items for the locations multiple selection list
	 * 
	 * @return select items for the locations multiple selection list
	 */
	public List<SelectItem> getAllLocations() {
		return this.allLocations;
	}

	/**
	 * Get the select items for the type selection single selection list
	 * 
	 * @return select items for the type selection single selection list
	 */
	public List<SelectItem> getTypes() {
		return this.types;
	}

	/**
	 * Get the select items for the reference year single selection lists
	 * 
	 * @return select items for the reference year single selection lists
	 */
	public List<SelectItem> getReferenceYears() {
		return this.referenceYears;
	}

	/**
	 * Get the parameterized filter value
	 * 
	 * @return parameterized filter value
	 */
	public Boolean getParameterizedFilter() {
		return super.getFilterBoolean( ProcessesSearchHandler.FILTER_PARAMETERIZED );
	}

	/**
	 * Set the parameterized filter value
	 * 
	 * @param parameterized
	 *            new value to set
	 */
	public void setParameterizedFilter( Boolean parameterized ) {
		if ( Boolean.TRUE.equals( parameterized ) ) {
			super.setFilter( ProcessesSearchHandler.FILTER_PARAMETERIZED, Boolean.TRUE );
		}
		else {
			super.setFilter( ProcessesSearchHandler.FILTER_PARAMETERIZED, null );
		}
	}

	/**
	 * Get the parameterized filter value
	 * 
	 * @return parameterized filter value
	 */
	public Boolean getDistributedFilter() {
		return super.getFilterBoolean( ProcessesSearchHandler.FILTER_DISTRIBUTED );
	}

	/**
	 * Set the parameterized filter value
	 * 
	 * @param parameterized
	 *            new value to set
	 */
	public void setDistributedFilter( Boolean parameterized ) {
		if ( Boolean.TRUE.equals( parameterized ) ) {
			super.setFilter( ProcessesSearchHandler.FILTER_DISTRIBUTED, Boolean.TRUE );
			super.getLazyDataModel().setLog( new DistributedSearchLog() );
			if ( !ConfigurationService.INSTANCE.isRegistryBasedNetworking() ) {
				super.setFilter( ProcessesSearchHandler.FILTER_REGISTRY_VIRTUAL, Boolean.TRUE );
			}
		}
		else {
			super.setFilter( ProcessesSearchHandler.FILTER_DISTRIBUTED, null );
			super.setFilter( ProcessesSearchHandler.FILTER_REGISTRY_UUID, null );
			super.setFilter( ProcessesSearchHandler.FILTER_REGISTRY_VIRTUAL, null );
		}
	}

	/**
	 * Set the selected type
	 * 
	 * @param type
	 *            type to set
	 */
	public void setTypeFilter( String type ) {
		super.setFilter( ProcessesSearchHandler.FILTER_TYPE, type );
	}

	/**
	 * Get the selected type
	 * 
	 * @return selected type
	 */
	public String getTypeFilter() {
		return super.getFilter( ProcessesSearchHandler.FILTER_TYPE );
	}

	/**
	 * Set the description filter value
	 * 
	 * @param descFilter
	 *            description filter value to set
	 */
	public void setDescriptionFilter( String descFilter ) {
		super.setFilter( ProcessesSearchHandler.FILTER_DESCRIPTION, descFilter );
	}

	/**
	 * Get the description filter value
	 * 
	 * @return description filter value
	 */
	public String getDescriptionFilter() {
		return super.getFilter( ProcessesSearchHandler.FILTER_DESCRIPTION );
	}

	/**
	 * Get the selected locations
	 * 
	 * @return selected locations
	 */
	public List<String> getLocationsFilter() {
		String[] val = super.getFilterStringArr( ProcessesSearchHandler.FILTER_LOCATIONS );
		if ( val != null ) {
			return Arrays.asList( val );
		}
		else {
			return new ArrayList<String>();
		}
	}

	/**
	 * Set the selected locations
	 * 
	 * @param locations
	 *            locations to set
	 */
	public void setLocationsFilter( List<String> locations ) {
		String[] val;
		if ( locations != null && !locations.isEmpty() ) {
			val = locations.toArray( new String[0] );
		}
		else {
			val = null;
		}
		super.setFilter( ProcessesSearchHandler.FILTER_LOCATIONS, val );
	}

	/**
	 * Get the filter value for lower reference year
	 * 
	 * @return filter value for lower reference year
	 */
	public Integer getReferenceYearLowerFilter() {
		// ugly but required until criteria API is used in DAO routines
		return this.parseInteger( this.getFilter( ProcessesSearchHandler.FILTER_REF_YEAR_LOWER ) );
	}

	/**
	 * Set the filter value for lower reference year
	 * 
	 * @param refYearLower
	 *            new value to set
	 */
	public void setReferenceYearLowerFilter( Integer refYearLower ) {
		// ugly but required until criteria API is used in DAO routines
		if ( refYearLower != null && refYearLower.intValue() != 0 ) {
			this.setFilter( ProcessesSearchHandler.FILTER_REF_YEAR_LOWER, refYearLower.toString() );
		}
		else {
			this.setFilter( ProcessesSearchHandler.FILTER_REF_YEAR_LOWER, null );
		}
	}

	/**
	 * Get the filter value for upper reference year
	 * 
	 * @return filter value for upper reference year
	 */
	public Integer getReferenceYearUpperFilter() {
		// ugly but required until criteria API is used in DAO routines
		return this.parseInteger( this.getFilter( ProcessesSearchHandler.FILTER_REF_YEAR_UPPER ) );
	}

	/**
	 * Set the filter value for upper reference year
	 * 
	 * @param refYearLower
	 *            new value to set
	 */
	public void setReferenceYearUpperFilter( Integer refYearUpper ) {
		// ugly but required until criteria API is used in DAO routines
		if ( refYearUpper != null && refYearUpper.intValue() != 0 ) {
			this.setFilter( ProcessesSearchHandler.FILTER_REF_YEAR_UPPER, refYearUpper.toString() );
		}
		else {
			this.setFilter( ProcessesSearchHandler.FILTER_REF_YEAR_UPPER, null );
		}
	}

	/**
	 * Get the pick list classes
	 * 
	 * @return pick list classes
	 */
	public DualListModel<String> getPickAllClasses() {
		return this.pickAllClasses;
	}

	/**
	 * Set pick list model for all classes
	 * 
	 * @param pickAllClasses
	 *            list model to set
	 */
	public void setPickAllClasses( DualListModel<String> pickAllClasses ) {
		this.pickAllClasses = pickAllClasses;

		if ( pickAllClasses != null ) {
			List<String> lstClasses = new ArrayList<String>();
			for ( String target : pickAllClasses.getTarget() ) {
				lstClasses.add( target );
			}
			if ( lstClasses.isEmpty() ) {
				super.setFilter( ProcessesSearchHandler.FILTER_CLASSES, null );
			}
			else {
				super.setFilter( ProcessesSearchHandler.FILTER_CLASSES, lstClasses.toArray( new String[0] ) );
			}
		}
	}

	/**
	 * Determined if a search was performed
	 * 
	 * @return <code>true</code> if search was performed and result list shall be displayed, else <code>false</code>
	 */
	public boolean getSearched() {
		return this.searched;
	}

	/**
	 * Load the form, returns <code>null</code> (no redirect). Sets internal {@link #getSearched() search flag} to
	 * <code>false</code>
	 * 
	 * @return <code>null</code>
	 */
	public String loadForm() {
		this.searched = false;
		return null;
	}

	/**
	 * Do the search, sets internal {@link #getSearched() search flag} to <code>true</code>, trigger {@link #doFilter()}
	 * and return <code>null</code> (no
	 * redirect)
	 * 
	 * @return <code>null</code>
	 */
	public String search() {
		this.searched = true;
		this.doFilter();
		return null;
	}

	/**
	 * Parse integer. Null string, empty string or non-integer string will result in <code>null</code> <strong>Remove as
	 * soon as criteria API is used for DAO
	 * routines</strong>
	 * 
	 * @param s
	 *            string to parse
	 * @return value as Integer or <code>null</code> if not possible
	 */
	private Integer parseInteger( String s ) {
		if ( s == null || s.trim().isEmpty() ) {
			return null;
		}
		try {
			return new Integer( s );
		}
		catch ( Exception e ) {
			return null;
		}
	}

	public String getRegistry() {
		return super.getFilter( ProcessesSearchHandler.FILTER_REGISTRY_UUID );
	}

	public void setRegistry( String registry ) {
		if ( registry != null && !registry.equals( "" ) ) {
			Registry reg = registryService.findByUUID( registry );
			// if(!reg.getVirtual()){
			// super.setFilter(ProcessesSearchHandler.FILTER_REGISTRY_VIRTUAL, Boolean.FALSE);
			// }else{
			// super.setFilter(ProcessesSearchHandler.FILTER_REGISTRY_VIRTUAL, Boolean.TRUE);
			// }

			super.setFilter( ProcessesSearchHandler.FILTER_REGISTRY_VIRTUAL, Boolean.FALSE );
			this.setFilter( ProcessesSearchHandler.FILTER_REGISTRY_UUID, registry );
			registryId = reg.getId();

		}
		else {
			this.setFilter( ProcessesSearchHandler.FILTER_REGISTRY_UUID, null );
			super.setFilter( ProcessesSearchHandler.FILTER_REGISTRY_VIRTUAL, null );
		}
	}

	public Long getRegistryId() {
		return this.registryId;
	}

	public String getNodeBaseUrl( String nodeId ) {
		NetworkNodeDao nnd = new NetworkNodeDao();
		if ( this.registryId != null ) {
			return nnd.getNetworkNode( nodeId, this.registryId ).getBaseUrl();
		}
		else {
			return nnd.getNetworkNode( nodeId ).getBaseUrl();
		}
	}

	public boolean isLogEmpty() {
		return this.getLazyDataModel().getLog() == null || this.getLazyDataModel().getLog().isEmpty();
	}
}
