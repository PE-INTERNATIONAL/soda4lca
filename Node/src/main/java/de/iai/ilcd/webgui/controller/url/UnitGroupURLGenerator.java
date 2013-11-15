package de.iai.ilcd.webgui.controller.url;

import de.iai.ilcd.model.unitgroup.UnitGroup;

/**
 * URL Generator for unit group links
 */
public class UnitGroupURLGenerator extends AbstractDataSetURLGenerator<UnitGroup> {

	/**
	 * Create the generator
	 * 
	 * @param bean
	 *            URL bean
	 */
	public UnitGroupURLGenerator( URLGeneratorBean bean ) {
		super( bean, "unitgroup", "unitgroups" );
	}

}
