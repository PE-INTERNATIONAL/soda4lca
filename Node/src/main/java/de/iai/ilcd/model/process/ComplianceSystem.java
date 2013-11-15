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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.fzk.iai.ilcd.service.model.enums.ComplianceValue;
import de.fzk.iai.ilcd.service.model.process.IComplianceSystem;
import de.iai.ilcd.model.common.GlobalReference;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "compliancesystem" )
public class ComplianceSystem implements Serializable, IComplianceSystem {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@ManyToOne( cascade = CascadeType.ALL )
	GlobalReference sourceReference;

	@Enumerated( EnumType.STRING )
	private ComplianceValue overallCompliance;

	@Enumerated( EnumType.STRING )
	private ComplianceValue nomenclatureCompliance;

	@Enumerated( EnumType.STRING )
	private ComplianceValue methodologicalCompliance;

	@Enumerated( EnumType.STRING )
	private ComplianceValue reviewCompliance;

	@Enumerated( EnumType.STRING )
	private ComplianceValue documentationCompliance;

	@Enumerated( EnumType.STRING )
	private ComplianceValue qualityCompliance;

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	@Override
	public String getName() {
		if ( sourceReference == null ) {
			return "";
		}
		if ( sourceReference.getShortDescription() == null ) {
			return "";
		}
		return sourceReference.getShortDescription().getDefaultValue();
	}

	@Override
	public ComplianceValue getDocumentationCompliance() {
		return documentationCompliance;
	}

	public void setDocumentationCompliance( ComplianceValue documentationCompliance ) {
		this.documentationCompliance = documentationCompliance;
	}

	@Override
	public ComplianceValue getMethodologicalCompliance() {
		return methodologicalCompliance;
	}

	public void setMethodologicalCompliance( ComplianceValue methodologicalCompliance ) {
		this.methodologicalCompliance = methodologicalCompliance;
	}

	@Override
	public ComplianceValue getNomenclatureCompliance() {
		return nomenclatureCompliance;
	}

	public void setNomenclatureCompliance( ComplianceValue nomenclatureCompliance ) {
		this.nomenclatureCompliance = nomenclatureCompliance;
	}

	@Override
	public ComplianceValue getOverallCompliance() {
		return overallCompliance;
	}

	public void setOverallCompliance( ComplianceValue overallCompliance ) {
		this.overallCompliance = overallCompliance;
	}

	@Override
	public ComplianceValue getQualityCompliance() {
		return qualityCompliance;
	}

	public void setQualityCompliance( ComplianceValue qualityCompliance ) {
		this.qualityCompliance = qualityCompliance;
	}

	@Override
	public ComplianceValue getReviewCompliance() {
		return reviewCompliance;
	}

	public void setReviewCompliance( ComplianceValue reviewCompliance ) {
		this.reviewCompliance = reviewCompliance;
	}

	@Override
	public GlobalReference getReference() {
		return sourceReference;
	}

	public void setSourceReference( GlobalReference sourceReference ) {
		this.sourceReference = sourceReference;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals( Object object ) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if ( !(object instanceof ComplianceSystem) ) {
			return false;
		}
		ComplianceSystem other = (ComplianceSystem) object;
		if ( (this.id == null && other.id != null) || (this.id != null && !this.id.equals( other.id )) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.iai.ilcd.model.process.ComlianceSystem[id=" + id + "]";
	}
}
