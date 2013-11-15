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

package de.iai.ilcd.model.adapter.dataset;

import java.util.Set;

import de.fzk.iai.ilcd.service.client.impl.vo.dataset.ProcessDataSetVO;
import de.fzk.iai.ilcd.service.client.impl.vo.types.process.AccessInformationType;
import de.fzk.iai.ilcd.service.client.impl.vo.types.process.LCIMethodInformationType;
import de.fzk.iai.ilcd.service.client.impl.vo.types.process.TimeInformationType;
import de.fzk.iai.ilcd.service.model.IProcessListVO;
import de.fzk.iai.ilcd.service.model.IProcessVO;
import de.fzk.iai.ilcd.service.model.process.IComplianceSystem;
import de.fzk.iai.ilcd.service.model.process.ILCIMethodInformation;
import de.iai.ilcd.model.adapter.ComplianceSystemAdapter;
import de.iai.ilcd.model.adapter.GlobalReferenceAdapter;
import de.iai.ilcd.model.adapter.LStringAdapter;
import de.iai.ilcd.model.adapter.QuantitativeReferenceAdapter;
import de.iai.ilcd.model.adapter.ReviewAdapter;

/**
 * Adapter for Processes
 */
public class ProcessVOAdapter extends AbstractDatasetAdapter<ProcessDataSetVO, IProcessListVO, IProcessVO> {

	/**
	 * Create adapter for {@link IProcessListVO process list value object}
	 * 
	 * @param adaptee
	 *            list value object to adapt
	 */
	public ProcessVOAdapter( IProcessListVO adaptee ) {
		super( new ProcessDataSetVO(), adaptee );
	}

	/**
	 * Create adapter for the {@link IProcessVO value object}
	 * 
	 * @param adaptee
	 *            value object to adapt
	 */
	public ProcessVOAdapter( IProcessVO adaptee ) {
		super( new ProcessDataSetVO(), adaptee );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IProcessListVO src, ProcessDataSetVO dst ) {
		AccessInformationType accessInformation = new AccessInformationType();
		LStringAdapter.copyLStrings( src.getAccessInformation().getUseRestrictions(), accessInformation.getUseRestrictions() );
		accessInformation.setLicenseType( src.getAccessInformation().getLicenseType() );
		dst.setAccessInformation( accessInformation );
		dst.setContainsProductModel( src.getContainsProductModel() );

		TimeInformationType timeInformation = new TimeInformationType();
		timeInformation.setReferenceYear( src.getTimeInformation().getReferenceYear() );
		timeInformation.setValidUntil( src.getTimeInformation().getValidUntil() );
		dst.setTimeInformation( timeInformation );

		dst.setType( src.getType() );

		ILCIMethodInformation i = src.getLCIMethodInformation();
		if ( i != null ) {
			LCIMethodInformationType lciMethodInformation = new LCIMethodInformationType();
			lciMethodInformation.setMethodPrinciple( i.getMethodPrinciple() );
			lciMethodInformation.getApproaches().addAll( i.getApproaches() );
			dst.setLCIMethodInformation( lciMethodInformation );
		}

		dst.setLocation( src.getLocation() );

		Set<IComplianceSystem> srcComplSystems = src.getComplianceSystems();
		if ( srcComplSystems != null ) {
			Set<IComplianceSystem> destComplSystems = dst.getComplianceSystems();
			for ( IComplianceSystem compSys : srcComplSystems ) {
				destComplSystems.add( new ComplianceSystemAdapter( compSys ) );
			}
		}

		dst.setOverallQuality( src.getOverallQuality() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copyValues( IProcessVO src, ProcessDataSetVO dst ) {
		this.copyValues( (IProcessListVO) src, dst );

		dst.setApprovedBy( new GlobalReferenceAdapter( src.getApprovedBy() ) );

		LStringAdapter.copyLStrings( src.getBaseName(), dst.getBaseName() );

		dst.setCompletenessProductModel( src.getCompletenessProductModel() );

		dst.setFormat( src.getFormat() );

		dst.setOwnerReference( new GlobalReferenceAdapter( src.getOwnerReference() ) );

		dst.setQuantitativeReference( new QuantitativeReferenceAdapter( src.getQuantitativeReference() ) );

		ReviewAdapter.copyReviews( src.getReviews(), dst.getReviews() );

		LStringAdapter.copyLStrings( src.getSynonyms(), dst.getSynonyms() );
		LStringAdapter.copyLStrings( src.getTechnicalPurpose(), dst.getTechnicalPurpose() );
		LStringAdapter.copyLStrings( src.getUseAdvice(), dst.getUseAdvice() );
	}

}
