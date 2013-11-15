package de.iai.ilcd.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * <p>
 * Enum with all types of protection (e.g. read, write...) that are possible. This is the second part for the shiro
 * {@link WildcardPermission}. Please note that not all protection types are applicable on all {@link ProtectableType
 * protectable types}.
 * </p>
 * <p>
 * Please note that the numerical values of the protection types can be combined via logical OR and stored as a single
 * value (values are unique power of 2)! Use {@link #combine(ProtectionType...)} and {@link #toTypes(int)} and
 * {@link #toTypesCSV(int, String)}.
 * </p>
 */
public enum ProtectionType {

	/**
	 * Read operation (2<sup>0</sup>)
	 */
	READ( 1 ),
	/**
	 * Write operation (2<sup>1</sup>)
	 */
	WRITE( 2 ),
	/**
	 * Import operation (2<sup>2</sup>)
	 */
	IMPORT( 4 ),
	/**
	 * Export operation (2<sup>3</sup>)
	 */
	EXPORT( 8 ),
	/**
	 * Check-in operation (2<sup>4</sup>)
	 */
	CHECKIN( 16 ),
	/**
	 * Check-out operation (2<sup>5</sup>)
	 */
	CHECKOUT( 32 ),
	/**
	 * Release operation (2<sup>6</sup>)
	 */
	RELEASE( 64 ),
	/**
	 * Create operation (2<sup>7</sup>)
	 */
	CREATE( 128 ),
	/**
	 * Delete operation (2<sup>8</sup>)
	 */
	DELETE( 256 );

	/**
	 * Power of 2 value for this protection type
	 */
	private final int numerical;

	/**
	 * Create the protection type. <b>Important:</b> for each protection type use a <b><u>unique power of 2</u></b> as
	 * numerical value. Therefore a single
	 * numerical value (with all permissions combined via logical OR) can be stored in data base.
	 * 
	 * @param numerical
	 *            numerical value.
	 */
	private ProtectionType( int numerical ) {
		this.numerical = numerical;
	}

	/**
	 * Get the numerical (power of 2) value
	 * 
	 * @return numerical (power of 2) value
	 */
	public int getNumerical() {
		return this.numerical;
	}

	/**
	 * Determine if the protection type is contained in the provided combined numerical value.
	 * 
	 * @param combinedNumerical
	 *            a combined (via logical OR) value to check for current protection type
	 * @return <code>true</code> if protection type is contained, <code>false</code> otherwise
	 */
	public boolean isContained( int combinedNumerical ) {
		return (combinedNumerical & this.numerical) > 0;
	}

	/**
	 * Combines all protection types via logical OR of their {@link #getNumerical() numerical values}
	 * 
	 * @param types
	 *            types to combine
	 * @return combined value
	 */
	public static int combine( ProtectionType... types ) {
		// no values => return zero
		if ( types == null || types.length == 0 ) {
			return 0;
		}
		// else combine the types via logical OR
		int result = 0;
		for ( ProtectionType t : types ) {
			result |= t.getNumerical();
		}
		return result;
	}

	/**
	 * Gets all protection types for combined numerical value as array
	 * 
	 * @param combined
	 *            combined numerical value
	 * @return all protection types matched
	 */
	public static ProtectionType[] toTypes( int combined ) {
		List<ProtectionType> types = new ArrayList<ProtectionType>();
		for ( ProtectionType t : ProtectionType.values() ) {
			if ( t.isContained( combined ) ) {
				types.add( t );
			}
		}
		return types.toArray( new ProtectionType[0] );
	}

	/**
	 * Gets all protection types for combined numerical value as array
	 * 
	 * @param combined
	 *            combined numerical value
	 * @return all protection types matched
	 */
	public static String toTypesCSV( int combined, String separator ) {
		List<String> types = new ArrayList<String>();
		for ( ProtectionType t : ProtectionType.values() ) {
			if ( t.isContained( combined ) ) {
				types.add( t.name() );
			}
		}
		return StringUtils.join( types, separator );
	}

	/**
	 * Apply a this type to given value
	 * 
	 * @param apply
	 *            <code>true</code> to add current protection type, <code>false</code> to remove it
	 * @param currentVal
	 *            value to apply to
	 * @return new value
	 */
	public int applyToValue( boolean apply, int currentVal ) {
		if ( apply ) {
			return currentVal | this.numerical;
		}
		else {
			return currentVal & (~this.numerical);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #name()
	 */
	@Override
	public String toString() {
		return super.name();
	}

}
