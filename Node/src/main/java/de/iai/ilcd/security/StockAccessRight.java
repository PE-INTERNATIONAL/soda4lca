package de.iai.ilcd.security;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.iai.ilcd.model.datastock.AbstractDataStock;
import de.iai.ilcd.model.datastock.RootDataStock;
import de.iai.ilcd.model.security.User;
import de.iai.ilcd.model.security.UserGroup;

/**
 * Access right for a data stock.
 * <p>
 * Reference to stock and user/group is not done via JPA but only via their id. This is done for a simple reason:
 * performance. <br/>
 * The operation to get all rights is being performed on login. Therefore all we need to know is: user or group + id and
 * id of stock. This implementation avoids several joins / multiple access right implementations and tables.
 * </p>
 */
@Entity
@Table( name = "stock_access_right", uniqueConstraints = { @UniqueConstraint( columnNames = { "stock_id", "access_right_type", "ug_id" } ) } )
public class StockAccessRight {

	/**
	 * Type of access right
	 */
	public enum AccessRightType {
		/**
		 * User right
		 */
		User,
		/**
		 * Group right
		 */
		Group
	}

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private long id;

	/**
	 * ID of the stock
	 */
	@Column( name = "stock_id" )
	private long stockId;

	/**
	 * Flag to indicate if the referenced {@link #ugId} is
	 * an {@link User user id} or a {@link UserGroup group id}
	 */
	@Enumerated( EnumType.STRING )
	@Column( name = "access_right_type" )
	private AccessRightType accessRightType;

	/**
	 * Depending on {@link #accessRightType} this is the
	 * ID of the referenced {@link User} or {@link UserGroup}.
	 */
	@Basic
	@Column( name = "ug_id" )
	private long ugId;

	/**
	 * Numerical access right, see {@link ProtectionType} for details.
	 */
	@Basic
	@Column( name = "value" )
	private int value;

	/**
	 * Default constructor for JPA
	 */
	protected StockAccessRight() {

	}

	/**
	 * Create an access right
	 * 
	 * @param stockId
	 *            id of stock
	 * @param rootStock
	 *            <code>true</code> for {@link RootDataStock}
	 * @param accessRightType
	 *            type of access right
	 * @param ugId
	 *            user or group id
	 * @param value
	 *            access value (meaning see {@link ProtectionType})
	 */
	public StockAccessRight( long stockId, AccessRightType accessRightType, long ugId, int value ) {
		super();
		this.stockId = stockId;
		this.accessRightType = accessRightType;
		this.ugId = ugId;
		this.value = value;
	}

	/**
	 * Create an access right
	 * 
	 * @param stock
	 *            stock to control access to
	 * @param u
	 *            user to grant access for
	 * @param value
	 *            access value (meaning see {@link ProtectionType})
	 */
	public StockAccessRight( AbstractDataStock stock, User u, int value ) {
		this( stock.getId(), AccessRightType.User, u.getId(), value );
	}

	/**
	 * Create an access right
	 * 
	 * @param stock
	 *            stock to control access to
	 * @param g
	 *            group to grant access for
	 * @param value
	 *            access value (meaning see {@link ProtectionType})
	 */
	public StockAccessRight( AbstractDataStock stock, UserGroup g, int value ) {
		this( stock.getId(), AccessRightType.Group, g.getId(), value );
	}

	/**
	 * Get the numerical value of the access right. For its
	 * meaning see {@link ProtectionType}.
	 * 
	 * @return numerical access value
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Set the numerical access right value.For its
	 * meaning see {@link ProtectionType}.
	 * 
	 * @param value
	 *            numerical access value
	 */
	public void setValue( int value ) {
		this.value = value;
	}

	/**
	 * Get the stock ID
	 * 
	 * @return stock OD
	 */
	public long getStockId() {
		return this.stockId;
	}

	/**
	 * Set the user or group id (which one is determined by {@link #getAccessRightType()})
	 * 
	 * @return user/group id
	 */
	public long getUgId() {
		return this.ugId;
	}

	/**
	 * Type of access right, either for users or groups
	 * 
	 * @return access right type
	 */
	public AccessRightType getAccessRightType() {
		return this.accessRightType;
	}

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}
}
