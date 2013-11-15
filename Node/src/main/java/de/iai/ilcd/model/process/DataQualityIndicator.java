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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import de.fzk.iai.ilcd.service.model.enums.DataQualityIndicatorName;
import de.fzk.iai.ilcd.service.model.enums.QualityValue;
import de.fzk.iai.ilcd.service.model.process.IDataQualityIndicator;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "dataqualityindicator" )
public class DataQualityIndicator implements Serializable, IDataQualityIndicator {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Column( name = "indicatorName" )
	@Enumerated( EnumType.STRING )
	protected DataQualityIndicatorName name;

	@Column( name = "indicatorValue" )
	@Enumerated( EnumType.STRING )
	protected QualityValue value;

	protected DataQualityIndicator() {

	}

	public DataQualityIndicator( DataQualityIndicatorName name, QualityValue value ) {
		this.name = name;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	@Override
	public DataQualityIndicatorName getName() {
		return name;
	}

	public void setName( DataQualityIndicatorName name ) {
		this.name = name;
	}

	@Override
	public QualityValue getValue() {
		return value;
	}

	public void setValue( QualityValue value ) {
		this.value = value;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataQualityIndicator other = (DataQualityIndicator) obj;
		if (name != other.name)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.process.DataQualityIndicator[id=" + id + "]";
	}

}
