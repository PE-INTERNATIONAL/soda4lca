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

import de.iai.ilcd.model.contact.Contact;
import de.iai.ilcd.model.flow.Flow;
import de.iai.ilcd.model.flowproperty.FlowProperty;
import de.iai.ilcd.model.lciamethod.LCIAMethod;
import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.source.Source;
import de.iai.ilcd.model.unitgroup.UnitGroup;

/**
 * 
 * @author clemens.duepmeier
 */
public enum DataSetType {
	PROCESS( "process data set" ), FLOW( "flow data set" ), FLOWPROPERTY( "flowproperty data set" ), SOURCE( "source data set" ), UNITGROUP(
			"unitgroup data set" ), CONTACT( "contact data set" ), LCIAMETHOD( "lcia method data set" );

	private String value;

	DataSetType( String value ) {
		this.value = value;
	}

	public static DataSetType fromValue( String value ) {
		for ( DataSetType enumValue : DataSetType.values() ) {
			if ( enumValue.getValue().equals( value ) )
				return enumValue;
		}
		return null;
	}

	public static DataSetType fromClass( Class<? extends DataSet> c ) {
		if ( Process.class.equals( c ) ) {
			return DataSetType.PROCESS;
		}
		if ( Flow.class.equals( c ) ) {
			return DataSetType.FLOW;
		}
		if ( FlowProperty.class.equals( c ) ) {
			return DataSetType.FLOWPROPERTY;
		}
		if ( Source.class.equals( c ) ) {
			return DataSetType.SOURCE;
		}
		if ( UnitGroup.class.equals( c ) ) {
			return DataSetType.UNITGROUP;
		}
		if ( Contact.class.equals( c ) ) {
			return DataSetType.CONTACT;
		}
		if ( LCIAMethod.class.equals( c ) ) {
			return DataSetType.LCIAMETHOD;
		}
		return null;
	}

	public String getValue() {
		return value;
	}
}
