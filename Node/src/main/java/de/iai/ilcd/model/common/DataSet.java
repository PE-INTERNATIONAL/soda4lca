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

package de.iai.ilcd.model.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;

import de.fzk.iai.ilcd.service.model.IDataSetVO;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.model.ILongIdObject;
import de.iai.ilcd.model.datastock.DataStock;
import de.iai.ilcd.model.datastock.RootDataStock;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@MappedSuperclass
public abstract class DataSet implements Serializable, IDataSetVO, ILongIdObject {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	protected Long id;

	// we use the field branch for optimistic concurrent locking;
	// i.e. collisions of concurrent updates will look for incremented branch numbers
	// branch will be incremented with every update of the data set
	@Version
	protected int branch = 0;

	// // if it's an remote data set node contains information about the remote network node
	// @Transient
	// private NetworkNode sourceNode=null;
	@Transient
	private String href;

	// we need this, if our data sets will be send to other servers, tools
	@Transient
	private String sourceId = ConfigurationService.INSTANCE.getNodeId();

	// @Embedded
	// @AttributeOverride( name = "defaultValue", column = @Column( name = "name" ) )
	// @AssociationOverride( name = "lStringMap", joinTable = @JoinTable( name = "dataset_names", joinColumns =
	// @JoinColumn( name = "dataset_id", referencedColumnName = "ID" ) ) )
	// protected MultiLanguageString name = new MultiLanguageString();

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> name = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	protected final MultiLangStringMapAdapter nameAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return DataSet.this.name;
		}
	} );

	@Embedded
	protected Uuid uuid = new Uuid();

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> description = new HashMap<String, String>();

	// @Embedded
	// @AttributeOverride( name = "defaultValue", column = @Column( name = "descriptsion" ) )
	// @AssociationOverride( name = "lStringMap", joinTable = @JoinTable( name = "dataset_descriptions", joinColumns =
	// @JoinColumn( name = "dataset_id", referencedColumnName = "ID" ) ) )
	// protected MultiLanguageText description = new MultiLanguageText();

	@ManyToOne( cascade = CascadeType.ALL )
	protected Classification classification = new Classification();

	@ManyToOne
	@JoinColumn( name = "root_stock_id" )
	private RootDataStock rootDataStock;

	protected String permanentUri;

	@Embedded
	protected DataSetVersion version;

	@Enumerated( EnumType.STRING )
	protected DataSetState releaseState = DataSetState.UNRELEASED;

	@OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinColumn( name = "XMLFILE_ID" )
	XmlFile xmlFile;

	/**
	 * Flag to indicate if this is the most recent version of this data set
	 */
	@Basic
	private boolean mostRecentVersion;

	/**
	 * Cache for the name column - used for filtering &amp; order in queries.
	 * 255 character limit should be sufficient.
	 */
	@SuppressWarnings( "unused" )
	@Basic
	@Column( name = "name_cache", length = 255 )
	private String nameCache;

	/**
	 * Cache for the classification - used for filtering &amp; order in queries.
	 * 100 character limit should be sufficient.
	 */
	@SuppressWarnings( "unused" )
	@Basic
	@Column( name = "classification_cache", length = 100 )
	private String classificationCache;

	/**
	 * Get all data stocks that this data set is contained in
	 * 
	 * @return data stocks that this data set is contained in
	 */
	public abstract Set<DataStock> getContainingDataStocks();

	/**
	 * Add the data set to the data stock. Only do the <code>addXXXX</code> call,
	 * everything else is done be {@link #addToDataStock(DataStock)}.
	 * 
	 * @param stock
	 *            stock to add to
	 */
	protected abstract void addSelfToDataStock( DataStock stock );

	/**
	 * Remove the data set from the data stock. Only do the <code>removeXXXX</code> call,
	 * everything else is done be {@link #removeFromDataStock(DataStock)}.
	 * 
	 * @param stock
	 *            stock to add to
	 */
	protected abstract void removeSelfFromDataStock( DataStock stock );

	/**
	 * Add to data stock
	 * 
	 * @param stock
	 *            stock to add to
	 */
	public final void addToDataStock( DataStock stock ) {
		final Set<DataStock> containingDataStocks = this.getContainingDataStocks();
		if ( !containingDataStocks.contains( stock ) ) {
			containingDataStocks.add( stock );
			this.addSelfToDataStock( stock );
		}
	}

	/**
	 * Remove from data stock
	 * 
	 * @param stock
	 *            stock to remove from
	 */
	public final void removeFromDataStock( DataStock stock ) {
		final Set<DataStock> containingDataStocks = this.getContainingDataStocks();
		if ( containingDataStocks.contains( stock ) ) {
			containingDataStocks.remove( stock );
			this.removeSelfFromDataStock( stock );
		}
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	/**
	 * Get the root data stock
	 * 
	 * @return the root data stock
	 */
	public RootDataStock getRootDataStock() {
		return this.rootDataStock;
	}

	/**
	 * Set the root data stock
	 * 
	 * @param rootDataStock
	 *            the root data stock to set
	 */
	public void setRootDataStock( RootDataStock rootDataStock ) {
		this.rootDataStock = rootDataStock;
	}

	// public NetworkNode getSourceNode() {
	// return sourceNode;
	// }
	//
	// public void setSourceNode(NetworkNode sourceNode) {
	// this.sourceNode = sourceNode;
	// }

	/**
	 * Write the {@link #getName() name} default value
	 * to the cache field.
	 */
	@PrePersist
	protected void applyDataSetCache() {
		this.nameCache = StringUtils.substring( this.getNameAsStringForCache(), 0, 255 );
		if ( this.classification != null ) {
			this.classificationCache = StringUtils.substring( this.getClassificationHierarchyAsStringForCache(), 0, 100 );
		}
		else {
			this.classificationCache = null;
		}

	}

	public int getBranch() {
		return this.branch;
	}

	public void setBranch( int branch ) {
		this.branch = branch;
	}

	/**
	 * Get the hierarchy of classification as string. May be overridden by
	 * sub-classes if {@link #getClassification()} shall not represent
	 * the hierarchy.
	 * 
	 * @return delegates to {@link #getClassification()}.{@link Classification#getClassHierarchyAsString()
	 *         getClassHierarchyAsString()}
	 */
	protected String getClassificationHierarchyAsStringForCache() {
		if ( this.classification != null ) {
			return this.classification.getClassHierarchyAsString();
		}
		return null;
	}

	/**
	 * Get the name as string. May be overridden by sub-classes if {@link #getName()} shall not represent the name.
	 * 
	 * @return delegates to {@link #getName()}.{@link MultiLanguageString#getDefaultValue() getDefaultValue()}
	 */
	protected String getNameAsStringForCache() {
		if ( this.name != null ) {
			return this.nameAdapter.getDefaultValue();
		}
		return null;
	}

	/**
	 * Determine if this is the most recent version of this data set
	 * 
	 * @return <code>true</code> if this is the most recent version of this data set, else <code>false</code>
	 */
	public boolean isMostRecentVersion() {
		return this.mostRecentVersion;
	}

	/**
	 * Set the flag to indicate if this data set is the most recent version of the data set
	 * 
	 * @param mostRecentVersion
	 *            flag to set
	 */
	public void setMostRecentVersion( boolean mostRecentVersion ) {
		this.mostRecentVersion = mostRecentVersion;
	}

	@Override
	public Classification getClassification() {
		return this.classification;
	}

	public void setClassification( Classification classification ) {
		this.classification = classification;
	}

	@Transient
	private final MultiLangStringMapAdapter descriptionAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return DataSet.this.description;
		}
	} );

	// @Override
	// public MultiLanguageText getDescription() {
	// return this.description;
	// }
	//
	// public void setDescription( MultiLanguageText description ) {
	// this.description = description;
	// }

	@Override
	public IMultiLangString getDescription() {
		return this.descriptionAdapter;
	}

	public void setDescription( IMultiLangString description ) {
		this.descriptionAdapter.overrideValues( description );
	}

	@Override
	public IMultiLangString getName() {
		return this.nameAdapter;
	}

	@Override
	public String getDefaultName() {
		return this.nameAdapter.getDefaultValue();
	}

	public void setName( IMultiLangString name ) {
		this.nameAdapter.overrideValues( name );
	}

	public Uuid getUuid() {
		return this.uuid;
	}

	@Override
	public String getUuidAsString() {
		return this.uuid.getUuid();
	}

	public void setUuid( Uuid uuid ) {
		this.uuid = uuid;
	}

	@Override
	public String getDataSetVersion() {
		return this.version != null ? this.version.getVersionString() : null;
	}

	public DataSetVersion getVersion() {
		return this.version;
	}

	public void setVersion( DataSetVersion version ) {
		this.version = version;
	}

	@Override
	public String getPermanentUri() {
		return this.permanentUri;
	}

	public void setPermanentUri( String permanentUri ) {
		this.permanentUri = permanentUri;
	}

	public DataSetState getReleaseState() {
		return this.releaseState;
	}

	public void setReleaseState( DataSetState releaseState ) {
		this.releaseState = releaseState;
	}

	public XmlFile getXmlFile() {
		return this.xmlFile;
	}

	public void setXmlFile( XmlFile xmlFile ) {
		this.xmlFile = xmlFile;
	}

	@Override
	public String getHref() {
		// @TODO - should be generated using the default host name specified in configuration
		// the access part for the dataset type and the UUID value
		return this.href;
	}

	@Override
	public void setHref( String href ) {
		this.href = href;
	}

	@Override
	public String getSourceId() {
		return this.sourceId;
	}

	@Override
	public void setSourceId( String sourceId ) {
		this.sourceId = sourceId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// object is null or no DataSet
		if ( !(object instanceof DataSet) ) {
			return false;
		}

		// check if same "subclass"
		if ( !this.getClass().equals( object.getClass() ) ) {
			return false;
		}

		DataSet other = (DataSet) object;
		// compare id, if set
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}

		// compare UUID, if set
		if ( (this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals( other.uuid )) ) {
			return false;
		}

		// compare version, if set
		if ( (this.version == null && other.version != null) || (this.version != null && !this.version.equals( other.version )) ) {
			return false;
		}

		// same class, same id, same UUID, same version [same means also: both null]
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.common.DataSet[id=" + this.id + "]";
	}

}
