package de.iai.ilcd.webgui.controller.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.iai.ilcd.model.process.Process;
import de.iai.ilcd.model.registry.Registry;
import de.iai.ilcd.services.RegistryService;
import de.iai.ilcd.webgui.util.Consts;
import eu.europa.ec.jrc.lca.commons.rest.dto.DataSetRegistrationResult;

@Component( "datasetRegistrationSummaryBean" )
@Scope( "request" )
public class DatasetRegistrationSummaryBean implements Serializable {

	private static final long serialVersionUID = -3144746029685291342L;

	@Autowired
	private RegistryService registryService;

	private Registry registry;

	private int accepted;

	private int rejected;

	private List<Process> rejectedProcesses;

	private List<Process> rejectedCompliance;

	private List<Process> rejectedNoDifference;

	@PostConstruct
	private void init() {
		List<DataSetRegistrationResult> result = (List<DataSetRegistrationResult>) FacesContext.getCurrentInstance().getExternalContext().getFlash().get(
				Consts.REGISTRATION_RESULT );

		List<Process> processes = (List<Process>) FacesContext.getCurrentInstance().getExternalContext().getFlash().get( Consts.SELECTED_DATASETS );

		Long registryId = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get( Consts.SELECTED_REGISTRY );

		registry = registryService.findById( registryId );
		displaySummaryMessage( processes, result );
	}

	private void displaySummaryMessage( List<Process> datasets, List<DataSetRegistrationResult> result ) {

		rejectedCompliance = groupBy( DataSetRegistrationResult.REJECTED_COMPLIANCE, result, datasets );

		rejectedNoDifference = groupBy( DataSetRegistrationResult.REJECTED_NO_DIFFERENCE, result, datasets );

		rejected = rejectedCompliance.size() + rejectedNoDifference.size();
		accepted = result.size() - rejected;

		rejectedProcesses = new ArrayList<Process>( rejectedNoDifference );
		rejectedProcesses.addAll( rejectedCompliance );
	}

	private List<Process> groupBy( DataSetRegistrationResult regResult, List<DataSetRegistrationResult> result, List<Process> datasets ) {
		List<Process> groupped = new ArrayList<Process>();
		for ( int i = 0; i < result.size(); i++ ) {
			if ( result.get( i ) == regResult ) {
				groupped.add( datasets.get( i ) );
			}
		}
		return groupped;
	}

	public Registry getRegistry() {
		return registry;
	}

	public int getAccepted() {
		return accepted;
	}

	public int getRejected() {
		return rejected;
	}

	public List<Process> getRejectedProcesses() {
		return rejectedProcesses;
	}

	public List<Process> getRejectedCompliance() {
		return rejectedCompliance;
	}

	public List<Process> getRejectedNoDifference() {
		return rejectedNoDifference;
	}

	public boolean isRejectedCompliance( Process process ) {
		return rejectedCompliance.contains( process );
	}
}
