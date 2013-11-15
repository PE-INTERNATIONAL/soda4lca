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

package de.iai.ilcd.webgui.controller.admin;

import java.util.Random;

/**
 * Utility class for generation of random string passwords
 */
public class RandomPassword {

	/**
	 * The possible characters for the password to create
	 */
	private static final String ALPHABET = "!0123456789ABCDEFGHIJKLMNOPQRSTUVWXYzabcdefghijklmnopqrstuvwxyz";

	/**
	 * Get a random password
	 * 
	 * @param length
	 *            count of characters
	 * @return created password with specified length
	 */
	public static String getPassword( int length ) {
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < length; i++ ) {
			int pos = rand.nextInt( RandomPassword.ALPHABET.length() );
			sb.append( RandomPassword.ALPHABET.charAt( pos ) );
		}
		return sb.toString();
	}

}
