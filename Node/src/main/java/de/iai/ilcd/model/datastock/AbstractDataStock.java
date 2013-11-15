package de.iai.ilcd.model.datastock;

import java.text.Collator;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.model.ILongIdObject;
import de.iai.ilcd.model.common.Uuid;
import de.iai.ilcd.model.security.Organization;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * Base implementation of a data stock
 */
@Entity
@Table( name = "datastock", uniqueConstraints = { @UniqueConstraint( columnNames = "name" ) } )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "datastock_type", length = 3, discriminatorType = DiscriminatorType.STRING )
public abstract class AbstractDataStock implements IDataStockMetaData, ILongIdObject {

	/**
	 * Database ID
	 */
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	/**
	 * Name (may only contain <code>[A-Za-z0-9]</code>)
	 */
	@Basic
	@Column( name = "name" )
	private String name;

	/**
	 * Long title
	 */
	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "datastock_longtitle", joinColumns = @JoinColumn( name = "datastock_id" ) )
	@MapKeyColumn( name = "lang" )
	private final Map<String, String> longTitle = new HashMap<String, String>();

	// /**
	// * Description
	// */
	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "datastock_description", joinColumns = @JoinColumn( name = "datastock_id" ) )
	@MapKeyColumn( name = "lang" )
	private final Map<String, String> description = new HashMap<String, String>();

	/**
	 * Owner organization
	 */
	@ManyToOne
	@JoinColumn( name = "owner_organization" )
	private Organization ownerOrganization;

	/**
	 * UUID for global identification
	 */
	@Embedded
	protected Uuid uuid = new Uuid();

	/**
	 * Convenience method to determine if stock is a {@link RootDataStock}
	 * 
	 * @return <code>true</code> for {@link RootDataStock}s, <code>false</code> otherwise
	 */
	@Override
	public abstract boolean isRoot();

	/**
	 * Get the owner organization
	 * 
	 * @return owner organization
	 */
	public Organization getOwnerOrganization() {
		return this.ownerOrganization;
	}

	/**
	 * Set the owner organization
	 * 
	 * @param owner
	 *            owner organization to set
	 */
	public void setOwnerOrganization( Organization owner ) {
		this.ownerOrganization = owner;
	}

	/**
	 * Get the database ID
	 * 
	 * @return database ID
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Set the database ID
	 * 
	 * @param id
	 *            database ID to set
	 */
	public void setId( Long id ) {
		this.id = id;
	}

	/**
	 * Get the name of the data stock
	 * 
	 * @return name of the data stock
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of the data stock
	 * 
	 * @param name
	 *            name of the data stock to set, (may only contain <code>[A-Za-z0-9]+</code>)
	 * @throws IllegalArgumentException
	 *             if name contains other characters than <code>[A-Za-z0-9]+</code> or is <code>null</code>
	 */
	public void setName( String name ) {
		if ( name == null || !name.matches( "[A-Za-z0-9]+" ) ) {
			throw new IllegalArgumentException( "Data stock name must contain only letters and numbers, no whitespaces or something else and must not be null!" );
		}
		this.name = name;
	}

	/**
	 * Adapter for API backwards compatibility.
	 */
	// Anonyme Providerinstanz die immer aktuelle Referenz zurueckgibt ist noetig, falls sich die referenz durch JPA mal
	// aendert!!
	@Transient
	// wichtig, dass JPA nicht verwirrt wird ;)
	private final MultiLangStringMapAdapter longTitleAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {
		@Override
		public Map<String, String> getMap() {
			return AbstractDataStock.this.longTitle;
		}
	} );

	@Transient
	private final MultiLangStringMapAdapter descriptionAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return AbstractDataStock.this.description;
		}
	} );

	/**
	 * Get the long title of the data stock
	 * 
	 * @return long title of the data stock
	 */
	// neuer Ruckgabetyp: das interface, nicht mehr die impl ==> hatten wir ja so abgesprochen
	public IMultiLangString getLongTitle() {
		return this.longTitleAdapter; // hier den adapter rausgeben
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLongTitleValue() {
		return this.getLongTitle().getValue();
	}

	/**
	 * Set the long title of the data stock
	 * 
	 * @param longTitle
	 *            long title of the data stock to set
	 */
	// Argumenttyp: auch hier interface, nicht impl
	public void setLongTitle( IMultiLangString longTitle ) {
		this.longTitleAdapter.overrideValues( longTitle ); // Die Werte innerhalb der Map werden damit ueberschrieben
	}

	/**
	 * Get the description
	 * 
	 * @return description
	 */
	public IMultiLangString getDescription() {
		return this.descriptionAdapter;
	}

	/**
	 * Set the description
	 * 
	 * @param description
	 *            description to set
	 */
	public void setDescription( IMultiLangString description ) {
		this.descriptionAdapter.overrideValues( description );
	}

	/**
	 * Get the UUID
	 * 
	 * @return the UUID
	 */
	public Uuid getUuid() {
		return this.uuid;
	}

	/**
	 * Set the UUID
	 * 
	 * @param uuid
	 *            the UUID to set
	 */
	public void setUuid( Uuid uuid ) {
		this.uuid = uuid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof AbstractDataStock) ) {
			return false;
		}
		AbstractDataStock other = (AbstractDataStock) obj;
		if ( this.id == null ) {
			if ( other.id != null ) {
				return false;
			}
		}
		else if ( !this.id.equals( other.id ) ) {
			return false;
		}
		if ( this.name == null ) {
			if ( other.name != null ) {
				return false;
			}
		}
		else if ( !this.name.equals( other.name ) ) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo( IDataStockMetaData o ) {
		return Collator.getInstance().compare( this.getName(), o.getName() );
	}

}
