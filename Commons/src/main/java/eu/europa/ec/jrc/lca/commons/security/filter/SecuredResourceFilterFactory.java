package eu.europa.ec.jrc.lca.commons.security.filter;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

import eu.europa.ec.jrc.lca.commons.security.annotations.Secured;
import eu.europa.ec.jrc.lca.commons.util.ApplicationContextHolder;

@Component
public class SecuredResourceFilterFactory implements ResourceFilterFactory {

	@Override
	public List<ResourceFilter> create(AbstractMethod am) {
		if (am.isAnnotationPresent(Secured.class)) {
			return Collections
					.<ResourceFilter> singletonList(new SecurityResourceFilter());
		}
		return null;
	}

	private class SecurityResourceFilter implements ResourceFilter {

		@Override
		public ContainerRequestFilter getRequestFilter() {
			return ApplicationContextHolder.getApplicationContext().getBean(SecurityFilter.class);
		}

		@Override
		public ContainerResponseFilter getResponseFilter() {
			return null;
		}

	}
}

