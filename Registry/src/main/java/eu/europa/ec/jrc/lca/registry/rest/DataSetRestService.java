package eu.europa.ec.jrc.lca.registry.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europa.ec.jrc.lca.commons.domain.NodeCredentials;
import eu.europa.ec.jrc.lca.commons.rest.CustomStatus;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDTO;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDeregistrationRequest;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetDeregistrationResult;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationAcceptanceDecision;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationResult;
import eu.europa.ec.jrc.lca.commons.rest.dto.JaxbBaseList;
import eu.europa.ec.jrc.lca.commons.rest.dto.SecuredRequestWrapper;
import eu.europa.ec.jrc.lca.commons.security.annotations.Secured;
import eu.europa.ec.jrc.lca.commons.service.exceptions.NodeIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.service.exceptions.ResourceNotFoundException;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.service.DataSetDeregistrationService;
import eu.europa.ec.jrc.lca.registry.service.DataSetRegistrationService;
import eu.europa.ec.jrc.lca.registry.service.DataSetService;

@Path("/datasetservice")
@Service("dataSetRestService")
public class DataSetRestService {

	@Autowired
	private DataSetRegistrationService dataSetRegistrationService;
	
	@Autowired
	private DataSetDeregistrationService dataSetDeregistrationService;
	
	@Autowired
	private DataSetService dataSetService;
	
	/**
	 * Registering dataset
	 * @param req - SecuredRequestWrapper containing NodeCredentials and List of Datasets to register
	 * @return List of DataSetRegistrationResult
	 */
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_XML)
	@Secured
	public Response registerDataSets(SecuredRequestWrapper req) {
		try {
			JaxbBaseList<DataSet> ds = (JaxbBaseList<DataSet>) req.getEntity();
			String nodeId = ((NodeCredentials)req.getCredentials()).getNodeId();
			List<DataSetRegistrationResult> result = dataSetRegistrationService.registerDataSets(ds.getList(), nodeId);
			JaxbBaseList<DataSetRegistrationResult> list = new JaxbBaseList<DataSetRegistrationResult>(result);
			return Response.ok(list).build();
		} catch (NodeIllegalStatusException e) {
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
	}
	
	/**
	 * @param datasetId
	 * @return Dataset
	 */
	@GET
	@Path("/dataset/{datasetId}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getDataset(@PathParam("datasetId") String datasetId) {
		DataSet dataSet = dataSetService.findById(Long.valueOf(datasetId));
		if(dataSet==null){
			throw new WebApplicationException(CustomStatus.ENTITY_NOT_FOUND.getCode());
		}
		return Response.ok(dataSet).build();
	}
	
	/**
	 * Deregistering datasets
	 * @param req - SecuredRequestWrapper containing NodeCredentials and List of Datasets to deregister
	 * @return List of DataSetDeregistrationResult
	 */
	@POST
	@Path("/deregister")
	@Produces(MediaType.APPLICATION_XML)
	@Secured
	public Response deregisterDataSet(SecuredRequestWrapper req) {
		DataSetDeregistrationRequest dsDeregistrationReq = (DataSetDeregistrationRequest) req.getEntity();
		String nodeId = ((NodeCredentials)req.getCredentials()).getNodeId();
		List<DataSetDeregistrationResult> result = dataSetDeregistrationService.deregisterDataSets(dsDeregistrationReq.getDatasets(), dsDeregistrationReq.getReason(), nodeId);
		JaxbBaseList<DataSetDeregistrationResult> list = new JaxbBaseList<DataSetDeregistrationResult>(result);
		return Response.ok(list).build();
	}
	
	/**
	 * Checking Dataset registration status
	 * @param req - List of datasets DTO objects containing UUIDS and versions
	 * @return list of DataSetRegistrationAcceptanceDecision
	 */
	@POST
	@Path("/checkStatus")
	@Produces(MediaType.APPLICATION_XML)
	@Secured
	public Response checkStatus(SecuredRequestWrapper req) {
		String nodeId = ((NodeCredentials)req.getCredentials()).getNodeId();
		JaxbBaseList<DataSetDTO> dsUUIDs = (JaxbBaseList<DataSetDTO>) req.getEntity();
		List<DataSetRegistrationAcceptanceDecision> result = dataSetService.getStatus(dsUUIDs.getList(), nodeId);
		JaxbBaseList<DataSetRegistrationAcceptanceDecision> list = new JaxbBaseList<DataSetRegistrationAcceptanceDecision>(result);
		return Response.ok(list).build();
	}
	
	/**
	 * Checking if dataset hash is equal to the registered value 
	 * @param dataset
	 * @return verification status
	 */
	@POST
	@Path("/verify")
	@Produces(MediaType.APPLICATION_XML)
	public Response verifyDataSet(DataSet dataset) {
		try{
			boolean status = dataSetService.verifyDataSet(dataset);
			if(status){
				return Response.ok().build();
			}else{
				return Response.status(Status.CONFLICT).build();
			}
		}
		catch(ResourceNotFoundException e){
			throw new WebApplicationException(e, CustomStatus.ENTITY_NOT_FOUND.getCode());
		}
	}
}
