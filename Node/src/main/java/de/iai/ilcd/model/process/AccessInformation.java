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
import java.util.HashMap;
import java.util.Map;

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
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.fzk.iai.ilcd.service.model.enums.LicenseTypeValue;
import de.fzk.iai.ilcd.service.model.process.IAccessInformation;
import de.iai.ilcd.util.lstring.IStringMapProvider;
import de.iai.ilcd.util.lstring.MultiLangStringMapAdapter;

/**
 * 
 * @author clemens.duepmeier
 */
@Entity
@Table( name = "process_accessinformation" )
public class AccessInformation implements Serializable, IAccessInformation {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	protected long id;

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}
	protected boolean copyright = false;

	@Enumerated( EnumType.STRING )
	protected LicenseTypeValue licenseType;

	@ElementCollection
	@Column( name = "value", columnDefinition = "TEXT" )
	@CollectionTable( name = "process_userestrictions", joinColumns = @JoinColumn( name = "process_accessinformation_id" ) )
	@MapKeyColumn( name = "lang" )
	protected final Map<String, String> useRestrictions = new HashMap<String, String>();

	/**
	 * Adapter for API backwards compatibility.
	 */
	@Transient
	private final MultiLangStringMapAdapter useRestrictionsAdapter = new MultiLangStringMapAdapter( new IStringMapProvider() {

		@Override
		public Map<String, String> getMap() {
			return AccessInformation.this.useRestrictions;
		}
	} );

	@Override
	public boolean isCopyright() {
		return copyright;
	}

	public void setCopyright( boolean copyright ) {
		this.copyright = copyright;
	}

	@Override
	public LicenseTypeValue getLicenseType() {
		return licenseType;
	}

	public void setLicenseType( LicenseTypeValue licenseType ) {
		this.licenseType = licenseType;
	}

	@Override
	public IMultiLangString getUseRestrictions() {
		return this.useRestrictionsAdapter;
	}

	public void setUseRestrictions( IMultiLangString useRestrictions ) {
		this.useRestrictionsAdapter.overrideValues( useRestrictions );
	}
}
