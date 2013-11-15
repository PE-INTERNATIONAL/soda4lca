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

package de.iai.ilcd.model.process;

/**
 * 
 * @author clemens.duepmeier
 */
public enum ExchangeDirection {
	INPUT( "Output" ), OUTPUT( "Input" );

	private String value;

	ExchangeDirection( String value ) {
		this.value = value;
	}

	public static ExchangeDirection fromValue( String value ) {
		for ( ExchangeDirection enumValue : ExchangeDirection.values() ) {
			if ( enumValue.getValue().equals( value ) )
				return enumValue;
		}
		return null;
	}

	public String getValue() {
		return value;
	}
}
