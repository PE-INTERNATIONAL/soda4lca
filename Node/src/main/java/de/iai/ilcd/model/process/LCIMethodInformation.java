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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import de.fzk.iai.ilcd.service.model.enums.LCIMethodApproachesValue;
import de.fzk.iai.ilcd.service.model.enums.LCIMethodPrincipleValue;
import de.fzk.iai.ilcd.service.model.process.ILCIMethodInformation;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "lcimethodinformation" )
public class LCIMethodInformation implements Serializable, ILCIMethodInformation {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@Enumerated( EnumType.STRING )
	protected LCIMethodPrincipleValue methodPrinciple;

	@ElementCollection( )
	@CollectionTable( name = "process_lcimethodapproaches", joinColumns = @JoinColumn( name = "processId" ) )
	@Column( name = "approach" )
	@Enumerated( EnumType.STRING )
	protected Set<LCIMethodApproachesValue> approaches = new HashSet<LCIMethodApproachesValue>();

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	@Override
	public Set<LCIMethodApproachesValue> getApproaches() {
		return approaches;
	}

	/**
	 * Convenience method for returning LCI method approaches as List in order to user p:dataList (primefaces)
	 * 
	 * @return List of LCI method approaches
	 */
	public List<LCIMethodApproachesValue> getApproachesAsList() {
		return new ArrayList<LCIMethodApproachesValue>( this.getApproaches() );
	}

	protected void setApproaches( Set<LCIMethodApproachesValue> allocationApproaches ) {
		this.approaches = allocationApproaches;
	}

	public void addApproach( LCIMethodApproachesValue allocationApproach ) {
		if ( !approaches.contains( allocationApproach ) )
			approaches.add( allocationApproach );
	}

	@Override
	public LCIMethodPrincipleValue getMethodPrinciple() {
		return methodPrinciple;
	}

	public void setMethodPrinciple( LCIMethodPrincipleValue methodPrinciple ) {
		this.methodPrinciple = methodPrinciple;
	}

}
