package de.iai.ilcd.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Fixes Problem with PF 3.x Character Encoding
 */
public class CharsetFilter implements Filter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter( ServletRequest req, ServletResponse resp, FilterChain chain ) throws IOException, ServletException {
		req.setCharacterEncoding( "UTF-8" );
		chain.doFilter( req, resp );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init( FilterConfig conf ) throws ServletException {
	}

}
