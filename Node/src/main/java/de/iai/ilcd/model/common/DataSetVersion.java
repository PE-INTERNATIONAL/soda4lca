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

import javax.persistence.Embeddable;

import de.iai.ilcd.model.common.exception.FormatException;

/**
 * 
 * @author clemens.duepmeier
 */
@Embeddable
public class DataSetVersion implements Serializable, Comparable<DataSetVersion> {

	private int majorVersion = 0;

	private int minorVersion = 0;

	private int subMinorVersion = 0;

	public DataSetVersion() {

	}

	public DataSetVersion( int major, int minor, int subMinor ) throws FormatException {
		if ( major < 0 || minor < 0 || subMinor < 0 )
			throw new FormatException( "Every version number must be greater or equal to 0" );
		this.majorVersion = major;
		this.minorVersion = minor;
		this.subMinorVersion = subMinor;
	}

	public DataSetVersion( int major, int minor ) throws FormatException {
		this( major, minor, 0 );
	}

	public DataSetVersion( int major ) throws FormatException {
		this( major, 0, 0 );
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion( int majorVersion ) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion( int minorVersion ) {
		this.minorVersion = minorVersion;
	}

	public int getSubMinorVersion() {
		return subMinorVersion;
	}

	public void setSubMinorVersion( int subMinorVersion ) {
		this.subMinorVersion = subMinorVersion;
	}

	public static DataSetVersion parse( String versionString ) throws FormatException {
		DataSetVersion version = null;

		// versionString should contain three unsigned int number separated by colon
		String[] numbers = versionString.split( "\\." );
		if ( numbers.length < 1 || numbers.length > 3 )
			throw new FormatException( "The version number should be a string with the form xx.yy.zzz where xx, yy, zzz are numbers" );
		try {
			int major = Integer.parseInt( numbers[0] );
			int minor = 0;
			if ( numbers.length > 1 )
				minor = Integer.parseInt( numbers[1] );
			int subMinor = 0;
			if ( numbers.length > 2 )
				subMinor = Integer.parseInt( numbers[2] );

			if ( major < 0 || minor < 0 || subMinor < 0 )
				throw new FormatException( "All parts of the version string xx.yy.zzz must have values greater or equal 0" );
			version = new DataSetVersion( major, minor, subMinor );
		}
		catch ( NumberFormatException ex ) {
			throw new FormatException( "One of the parts contained in the version number string xx.yy.zzz could no be parsed to an Integer" );
		}

		return version;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		final DataSetVersion other = (DataSetVersion) obj;
		if ( this.majorVersion != other.majorVersion ) {
			return false;
		}
		if ( this.minorVersion != other.minorVersion ) {
			return false;
		}
		if ( this.subMinorVersion != other.subMinorVersion ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + this.majorVersion;
		hash = 97 * hash + this.minorVersion;
		return hash;
	}

	@Override
	public int compareTo( DataSetVersion other ) {

		if ( this.majorVersion > other.majorVersion )
			return 1;
		else if ( this.majorVersion < other.majorVersion )
			return -1;
		if ( this.minorVersion > other.minorVersion )
			return 1;
		else if ( this.minorVersion < other.minorVersion )
			return -1;
		if ( this.subMinorVersion > other.subMinorVersion )
			return 1;
		else if ( this.subMinorVersion < other.subMinorVersion )
			return -1;
		// now they must be equal
		return 0;
	}

	public String getVersionString() {
		return this.toString();
	}

	@Override
	public String toString() {
		return String.format( "%02d.%02d.%03d", majorVersion, minorVersion, subMinorVersion );
	}

}
