package de.iai.ilcd.webgui.controller.url;

import de.iai.ilcd.model.lciamethod.LCIAMethod;

/**
 * URL Generator for flow links
 */
public class LCIAMethodURLGenerator extends AbstractDataSetURLGenerator<LCIAMethod> {

	/**
	 * Create the generator
	 * 
	 * @param bean
	 *            URL bean
	 */
	public LCIAMethodURLGenerator( URLGeneratorBean bean ) {
		super( bean, "LCIAMethod", "LCIAMethod", "lciamethods" );
	}

}
