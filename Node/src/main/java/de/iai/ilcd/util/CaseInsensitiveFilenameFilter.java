package de.iai.ilcd.util;

import java.io.File;
import java.io.FilenameFilter;

public class CaseInsensitiveFilenameFilter implements FilenameFilter {

	private String nameToMatch = null;

	public CaseInsensitiveFilenameFilter( String nameToMatch ) {
		this.nameToMatch = nameToMatch;
	}

	@Override
	public boolean accept( File dir, String name ) {
		return this.nameToMatch.equalsIgnoreCase( name );
	}

}
