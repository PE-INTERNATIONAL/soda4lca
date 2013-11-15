package de.iai.ilcd.webgui.controller.url;

import de.iai.ilcd.model.source.Source;

/**
 * URL Generator for source links
 */
public class SourceURLGenerator extends AbstractDataSetURLGenerator<Source> {

	/**
	 * Create the generator
	 * 
	 * @param bean
	 *            URL bean
	 */
	public SourceURLGenerator( URLGeneratorBean bean ) {
		super( bean, "source", "sources" );
	}

}
