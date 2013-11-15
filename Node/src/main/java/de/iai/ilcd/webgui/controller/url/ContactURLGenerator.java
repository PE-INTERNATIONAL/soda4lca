package de.iai.ilcd.webgui.controller.url;

import de.iai.ilcd.model.contact.Contact;

/**
 * URL Generator for contact links
 */
public class ContactURLGenerator extends AbstractDataSetURLGenerator<Contact> {

	/**
	 * Create the generator
	 * 
	 * @param bean
	 *            URL bean
	 */
	public ContactURLGenerator( URLGeneratorBean bean ) {
		super( bean, "contact", "contacts" );
	}

}
