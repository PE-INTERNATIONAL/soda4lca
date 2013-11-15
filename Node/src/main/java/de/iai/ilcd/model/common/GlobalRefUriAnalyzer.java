package de.iai.ilcd.model.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.iai.ilcd.model.common.exception.FormatException;

/**
 * Analyzer for the extraction of UUID and version from data set URIs
 */
public class GlobalRefUriAnalyzer {

	/**
	 * Pattern for matching UUID and version. Matcher groups will be as listed below (bold = used groups):
	 * <ul>
	 * <li>0 &rArr; <i>uuid</i> or <i>uuid</i>_<i>versionString</i> (complete match)</li>
	 * <li><b>1 &rArr; <i>uuid</i></b></li>
	 * <li>2 &rArr; _<i>versionString or <code>null</code> if no version specified</i></li>
	 * <li><b>3 &rArr; <i>versionString or <code>null</code> if no version specified</i></b></li>
	 * </ul>
	 */
	private final static Pattern URI_UUID_VERSION_PATTERN = Pattern.compile(
			"([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})([_]([0-9]{2}[.][0-9]{2}[.][0-9]{3}))?", Pattern.CASE_INSENSITIVE );

	/**
	 * Detected UUID, <code>null</code> means invalid URI
	 */
	public final Uuid uuid;

	/**
	 * Detected version (may be null even for valid URIs)
	 */
	public final DataSetVersion version;

	/**
	 * Analyzer for data set URIs. Please note: no exe
	 * 
	 * @param refUri
	 *            the URI to analyze.
	 */
	public GlobalRefUriAnalyzer( String refUri ) {
		Uuid uuid = null;
		DataSetVersion version = null;

		// only if a URI is present
		if ( refUri != null ) {
			// strip .xml extension if there
			if ( refUri.endsWith( ".xml" ) ) {
				refUri = refUri.substring( 0, refUri.length() - 4 );
			}

			// Try to match the UUID and version
			Matcher m;
			synchronized ( GlobalRefUriAnalyzer.URI_UUID_VERSION_PATTERN ) {
				m = GlobalRefUriAnalyzer.URI_UUID_VERSION_PATTERN.matcher( refUri );
			}

			// if UUID/version pattern found
			if ( m.find() ) {
				// group 1: the UUID string (null check not required, because it has to be there if RegEx matched)
				uuid = new Uuid( m.group( 1 ) );
				// group 3: version string
				String tmp = m.group( 3 );
				if ( tmp != null ) {
					try {
						version = DataSetVersion.parse( tmp );
					}
					catch ( FormatException e ) {
						// should not happen, RegEx ensures correct format
					}
				}
			}
		}

		this.uuid = uuid;
		this.version = version;
	}

	/**
	 * Get the detected UUID
	 * 
	 * @return detected UUID
	 */
	public Uuid getUuid() {
		return this.uuid;
	}

	/**
	 * Get the detected UUID as string
	 * 
	 * @return detected UUID as string
	 */
	public String getUuidAsString() {
		if ( this.uuid == null ) {
			return null;
		}

		return uuid.getUuid();
	}

	/**
	 * Get the detected data set version
	 * 
	 * @return detected data set version
	 */
	public DataSetVersion getDataSetVersion() {
		return this.version;
	}
}
