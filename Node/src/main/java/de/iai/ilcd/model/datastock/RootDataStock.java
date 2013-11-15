package de.iai.ilcd.model.datastock;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * <p>
 * Implementation of a root data stock.
 * </p>
 */
@Entity
@DiscriminatorValue( "rds" )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
public class RootDataStock extends AbstractDataStock {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object obj ) {
		if ( obj instanceof RootDataStock ) {
			return super.equals( obj );
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return <code>true</code>
	 */
	@Override
	public boolean isRoot() {
		return true;
	}

}
