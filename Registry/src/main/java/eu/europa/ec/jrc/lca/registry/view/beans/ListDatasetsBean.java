package eu.europa.ec.jrc.lca.registry.view.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.service.exceptions.DatasetIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.view.model.ILCDLazyDataModel;
import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;
import eu.europa.ec.jrc.lca.registry.domain.Compliance;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.DataSet_;
import eu.europa.ec.jrc.lca.registry.service.DataSetAcceptanceService;
import eu.europa.ec.jrc.lca.registry.service.DataSetDeregistrationService;
import eu.europa.ec.jrc.lca.registry.service.DataSetService;

@Component
@Scope("view")
public class ListDatasetsBean implements Serializable {

	private static final long serialVersionUID = -1036320786375758293L;

	@Autowired
	private DataSetService dataSetService;
	
	@Autowired
	private DataSetDeregistrationService dataSetDeregistrationService;
	
	@Autowired
	private DataSetAcceptanceService dataSetAcceptanceService;

	private LazyDataModel<DataSet> lazyModel;

	private DataSet selectedDataSet;

	private DataSet[] selectedDatasets;
	
	private boolean onlyApproved = false;
	
	private UIData data;
	
	private String reason;

	private Boolean approved = Boolean.FALSE;
	@PostConstruct
	public void initBean() {
		Boolean approvedParam = geApprovedParameter();
		SearchParameters sp = new SearchParameters();
		
		if(approvedParam){
			sp.addFilter(DataSet_.status.getName(), DataSetStatus.ACCEPTED);
		}
		else{
			sp.addFilter(DataSet_.status.getName(), new Object[]{DataSetStatus.NEW, DataSetStatus.UPDATE, DataSetStatus.NEW_ALTERNATIVE});
		}
		lazyModel = new ILCDLazyDataModel<DataSet>(dataSetService, sp);
		onlyApproved = approvedParam;
	}

	public LazyDataModel<DataSet> getDatasets() {
		return lazyModel;
	}

	public DataSet getSelectedDataSet() {
		return selectedDataSet;
	}

	public void setSelectedDataSet(DataSet selectedDataSet) {
		this.selectedDataSet = selectedDataSet;
	}

	private Boolean geApprovedParameter() {
		String notApproved = ((HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest())
				.getParameter("approved");
		if(notApproved!=null){
			approved = Boolean.valueOf(notApproved);
			return approved;
		}
		return Boolean.FALSE;
	}

	public DataSet[] getSelectedDatasets() {
		return selectedDatasets;
	}

	public void setSelectedDatasets(DataSet[] selectedDatasets) {
		this.selectedDatasets = Arrays.copyOf(selectedDatasets, selectedDatasets.length);
	}
	
	public void acceptSelected() {
		flterUpdate();
		if(!selectionValid()){
			return;
		}
		dataSetAcceptanceService.acceptDataSets(Arrays.asList(selectedDatasets));
		FacesMessage message = Messages.getMessage(null, "datasetsHaveBeenAccepted", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
		
		FacesUtils.redirectToPage("/secured/datasets?approved=true");
	}
	
	public void rejectSelected() {
		flterUpdate();
		if(!selectionValid()){
			return;
		}
		dataSetAcceptanceService.rejectDataSets(Arrays.asList(selectedDatasets), reason);
		
		FacesMessage message = Messages.getMessage(null, "datasetsHaveBeenRejected", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
		
		FacesUtils.redirectToPage("/secured/datasets?approved=false");
	}
	
	public void deregisterSelected() {
		if(!selectionValid()){
			return;
		}
		try {
			dataSetDeregistrationService.deregisterDataSets(Arrays.asList(selectedDatasets));
			dataSetDeregistrationService.broadcastDataSetsDeregistration(Arrays.asList(selectedDatasets), reason);
			
			FacesMessage message = Messages.getMessage(null, "datasetsHaveBeenDeregistered", null);
			message.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (DatasetIllegalStatusException e) {
			FacesMessage message = Messages.getMessage(null, "updateDatasets_datasetIllegalStatus", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		
		FacesUtils.redirectToPage("/secured/datasets?approved=true");
	}
	
	private boolean selectionValid(){
		if (selectedDatasets == null || selectedDatasets.length==0) {
			FacesMessage message = Messages.getMessage(null, "listDatasets_noDataSetsSelected", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return false;
		}
		return true;
	}

	private void flterUpdate(){
		if(selectedDatasets != null){
			List<DataSet> temporaryList = new ArrayList(Arrays.asList(selectedDatasets));
			for(Iterator<DataSet> dsIter = temporaryList.iterator(); dsIter.hasNext();){
				DataSet ds = dsIter.next();
				if(ds.getStatus()==DataSetStatus.UPDATE || ds.getStatus()==DataSetStatus.NEW_ALTERNATIVE){
					dsIter.remove();
				}
			}
			selectedDatasets = temporaryList.toArray(new DataSet[]{});
		}
	}
	
	public boolean isOnlyApproved() {
		return onlyApproved;
	}
	
	public UIData getData() {
		return data;
	}

	public void setData(UIData data) {
		this.data = data;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public List<String> getSupportedCompliances(){
		if(((LazyDataModel)data.getValue()).getRowIndex()<0){
			return Collections.EMPTY_LIST;
		}
		
		DataSet ds = (DataSet) ((LazyDataModel)data.getValue()).getRowData();
		List<String> result = new ArrayList<String>();
		for(Compliance compliance: dataSetService.getSupportedCompliances()){
			if(ds.getComplianceUUIDs().contains(compliance.getUuid())){
				result.add(compliance.getName());
			}
		}
		return result;
	}

	public Boolean getApproved() {
		return approved;
	}
	
	
}
