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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fzk.iai.ilcd.service.model.common.ILString;
import de.fzk.iai.ilcd.service.model.common.IMultiLangString;
import de.iai.ilcd.configuration.ConfigurationService;

/**
 * 
 * @author clemens.duepmeier
 * @deprecated
 */
// @Embeddable
public class MultiLanguageText implements Serializable, IMultiLangString {

	// @Column( columnDefinition = "text", length = 10000 )
	private String defaultValue = "";

	// @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	// @MapKey( name = "lang" )
	private Map<String, LText> lStringMap = new HashMap<String, LText>();

	public MultiLanguageText() {

	}

	public MultiLanguageText( String value ) {
		this.defaultValue = value;
	}

	public MultiLanguageText( IMultiLangString other ) {
		// this.defaultValue=multiLangString.;
		for ( ILString lString : other.getLStrings() ) {
			if ( lString.getValue() != null && lString.getValue().equals( ConfigurationService.INSTANCE.getDefaultLanguage() ) )
				this.defaultValue = lString.getValue();
			else {
				if ( lString.getLang() != null && lString.getValue() != null )
					lStringMap.put( lString.getLang(), new LText( lString.getLang(), lString.getValue() ) );
			}
		}
	}

	public String getDefaultValue() {
		if ( defaultValue != null && !defaultValue.equals( "" ) )
			return defaultValue;
		if ( lStringMap.isEmpty() )
			return null;
		String textValue = null;
		for ( LText lValue : lStringMap.values() ) {
			textValue = lValue.getValue();
			break;
		}
		return textValue;
	}

	public void setDefaultValue( String defaultValue ) {
		this.defaultValue = defaultValue;
	}

	public Map<String, LText> getLStringMap() {
		return lStringMap;
	}

	protected void setLStringMap( Map<String, LText> multiLangValues ) {
		this.lStringMap = multiLangValues;
	}

	public void addLString( String language, String value ) {
		if ( language != null && value != null )
			lStringMap.put( language, new LText( language, value ) );

	}

	@Override
	public List<ILString> getLStrings() {
		List<ILString> lStrings = new ArrayList<ILString>();
		for ( ILString lValue : lStringMap.values() ) {
			lStrings.add( lValue );
		}
		return lStrings;
	}

	@Override
	public String getValue() {
		return this.getDefaultValue();
	}

	@Override
	public String getValue( String lang ) {
		return lStringMap.get( lang ).getValue();
	}

	@Override
	public void setValue( String value ) {
		this.setDefaultValue( value );
	}

	@Override
	public void setValue( String lang, String value ) {
		lStringMap.put( lang, new LText( lang, value ) );
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		final MultiLanguageText other = (MultiLanguageText) obj;
		if ( (this.getDefaultValue() == null) ? (other.getDefaultValue() != null) : !this.getDefaultValue().equals( other.getDefaultValue() ) ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 43 * hash + (this.getDefaultValue() != null ? this.getDefaultValue().hashCode() : 0);
		return hash;
	}

}
