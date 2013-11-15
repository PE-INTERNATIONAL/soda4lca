package de.iai.ilcd.util.lstring;

import java.util.Map;

/**
 * Provider for a map with language strings
 */
public interface IStringMapProvider {

	/**
	 * Get the map with the strings for multiple languages
	 * (may be <code>null</code>, is being taken care of)
	 * @return the map with the data
	 */
	public Map<String, String> getMap();

}
