package eu.europa.ec.jrc.lca.commons.security.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import eu.europa.ec.jrc.lca.commons.security.authenticators.Authenticator;

@Component
public class SecurityFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SecurityFilter.class);
	
	@Autowired
	private Authenticator authenticator;
	
	public ContainerRequest filter(ContainerRequest request) {
		ByteArrayOutputStream baos = getClonedInputStream(request.getEntityInputStream());
		request.setEntityInputStream(new ByteArrayInputStream(baos.toByteArray())); 
		authenticator.authenticate(request);
		request.setEntityInputStream(new ByteArrayInputStream(baos.toByteArray())); 
		return request;
	}
	
	
	private ByteArrayOutputStream getClonedInputStream(InputStream str){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int len;
	    try {
			while ((len = str.read(buffer)) > 0 ) {
			    baos.write(buffer, 0, len);
			}
			baos.flush();
		} catch (IOException e) {
			LOGGER.error("[getClonedInputStream]",e);
		}
	    finally{
	    	try {
				str.close();
			} catch (IOException e) {
				LOGGER.error("[getClonedInputStream]",e);
			}
	    }
	    return baos;
	}
}
