package eu.europa.ec.jrc.lca.commons.security.authenticators;

import com.sun.jersey.spi.container.ContainerRequest;

public interface Authenticator {
	void authenticate(ContainerRequest request);
}
