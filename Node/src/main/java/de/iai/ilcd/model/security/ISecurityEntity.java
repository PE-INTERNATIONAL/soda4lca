package de.iai.ilcd.model.security;

import de.iai.ilcd.model.ILongIdObject;

/**
 * Interface for common ID access of security entities
 */
public interface ISecurityEntity extends ILongIdObject {

	/**
	 * Get the display name
	 * 
	 * @return display name
	 */
	public String getDisplayName();

}
