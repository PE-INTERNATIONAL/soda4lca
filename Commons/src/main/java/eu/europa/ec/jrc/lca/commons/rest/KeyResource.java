package eu.europa.ec.jrc.lca.commons.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europa.ec.jrc.lca.commons.rest.dto.KeyWrapper;
import eu.europa.ec.jrc.lca.commons.security.SecurityContext;

@Path("/key")
@Service("keyService")
public class KeyResource {

	@Autowired
	private SecurityContext securityContext;

	@GET
	@Path("/public")
	@Produces(MediaType.APPLICATION_XML)
	public Response getPublicKey() {
		KeyWrapper key = new KeyWrapper(securityContext.getPublicKey());
		return Response.ok(key).build();
	}
}
