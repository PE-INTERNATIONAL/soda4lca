package de.iai.ilcd.util;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class FacesExceptionHandlerFactory extends ExceptionHandlerFactory {

	private final ExceptionHandlerFactory parent;

	// this injection handles jsf
	public FacesExceptionHandlerFactory( ExceptionHandlerFactory parent ) {
		this.parent = parent;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new FacesExceptionHandler( this.parent.getExceptionHandler() );
	}

}
