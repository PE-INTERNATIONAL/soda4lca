package de.iai.ilcd.webgui.controller.url;

import de.iai.ilcd.model.flow.Flow;

/**
 * URL Generator for flow links
 */
public class FlowURLGenerator extends AbstractDataSetURLGenerator<Flow> {

	/**
	 * Create the generator
	 * 
	 * @param bean
	 *            URL bean
	 */
	public FlowURLGenerator( URLGeneratorBean bean ) {
		super( bean, "flow", "flows" );
	}

}
