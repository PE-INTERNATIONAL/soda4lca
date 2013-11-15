package de.iai.ilcd.webgui.controller.url;

import de.iai.ilcd.model.flowproperty.FlowProperty;

/**
 * URL Generator for flow property links
 */
public class FlowPropertyURLGenerator extends AbstractDataSetURLGenerator<FlowProperty> {

	/**
	 * Create the generator
	 * 
	 * @param bean
	 *            URL bean
	 */
	public FlowPropertyURLGenerator( URLGeneratorBean bean ) {
		super( bean, "flowproperty", "flowproperties" );
	}

}
