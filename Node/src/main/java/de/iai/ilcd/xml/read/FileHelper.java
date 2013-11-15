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

package de.iai.ilcd.xml.read;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;

/**
 * 
 * @author clemens.duepmeier
 */
public class FileHelper {

	public static String convertFileToString( String fileName ) throws FileNotFoundException, IOException {
		FileInputStream inputStream = new FileInputStream( fileName );
		return convertStreamToString( inputStream );
	}

	public static String convertFileToString( File file ) throws FileNotFoundException, IOException {
		FileInputStream inputStream = new FileInputStream( file );
		return convertStreamToString( inputStream );
	}

	public static String convertStreamToString( InputStream is ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			sb.append( line ).append( "\n" );
		}
		is.close();
		return sb.toString();
	}

}
