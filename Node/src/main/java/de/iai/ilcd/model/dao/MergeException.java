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

package de.iai.ilcd.model.dao;

/**
 * MergeException will be thrown if a merge operation will fail in a dao object
 */
public class MergeException extends Exception {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -4576474342174544246L;

	/**
	 * Creates a new instance of <code>MergeException</code> without detail message.
	 */
	public MergeException() {
	}

	/**
	 * Constructs an instance of <code>MergeException</code> with the specified detail message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public MergeException( String msg ) {
		super( msg );
	}

	/**
	 * Constructs an instance of <code>MergeException</code> with the specified detail message.
	 * 
	 * @param msg
	 *            the detail message.
	 * @param cause
	 *            cause of the exception
	 */
	public MergeException( String msg, Throwable cause ) {
		super( msg, cause );
	}
}
