package eu.europa.ec.jrc.lca.registry.view.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.commons.service.exceptions.DatasetIllegalStatusException;
import eu.europa.ec.jrc.lca.commons.view.util.FacesUtils;
import eu.europa.ec.jrc.lca.commons.view.util.Messages;
import eu.europa.ec.jrc.lca.registry.domain.Compliance;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.DataSetStatus;
import eu.europa.ec.jrc.lca.registry.domain.DataSet_;
import eu.europa.ec.jrc.lca.registry.service.DataSetAcceptanceService;
import eu.europa.ec.jrc.lca.registry.service.DataSetService;
import eu.europa.ec.jrc.lca.registry.util.ILCDMessageSource;

@Component
@Scope("view")
public class UpdateDatasetsBean implements Serializable {

	private static final long serialVersionUID = -135256984526623914L;
	
	private static final String DATA_SET_UPDATED = "datasetDeregistration.updated";
	private static final String DATA_SET_REJECTED_BY_ADMIN = "datasetRegistration.rejected";

	@Autowired
	private DataSetService dataSetService;
	
	@Autowired
	private DataSetAcceptanceService dataSetAcceptanceService;
	
	@Autowired
	private ILCDMessageSource messageSource;

	private List<DataSet> datasets;

	private DataSet selectedDataSet;

	private UIData data;
	
	private String reason;

	@PostConstruct
	public void initBean() {
		String uuid = getSelectedDSUUID();
		SearchParameters sp = new SearchParameters();
		sp.addFilter(DataSet_.uuid.getName(), uuid);
		sp.setFirst(0);
		sp.setPageSize(Integer.MAX_VALUE);
		datasets = dataSetService.loadLazy(sp);
		initSelectedDS();
	}
	
	private void initSelectedDS(){
		for(DataSet ds: datasets){
			if(ds.getStatus().equals(DataSetStatus.ACCEPTED)){
				selectedDataSet = ds;
				break;
			}
		}
	}

	public List<DataSet> getDatasets() {
		return datasets;
	}

	public DataSet getSelectedDataSet() {
		return selectedDataSet;
	}

	public void setSelectedDataSet(DataSet selectedDataSet) {
		this.selectedDataSet = selectedDataSet;
	}

	private String getSelectedDSUUID() {
		return ((HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest())
				.getParameter("uuid");
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
		if(data == null || data.getRowIndex()<0){
			return Collections.EMPTY_LIST;
		}
		
		DataSet ds = (DataSet) data.getRowData();
		List<String> result = new ArrayList<String>();
		for(Compliance compliance: dataSetService.getSupportedCompliances()){
			if(ds.getComplianceUUIDs().contains(compliance.getUuid())){
				result.add(compliance.getName());
			}
		}
		return result;
	}
	
	
	public void acceptRegistration() {
		if(selectedDataSet.getStatus().equals(DataSetStatus.ACCEPTED)){
			dataSetAcceptanceService.rejectDataSets(getDataSetsToReject(), messageSource.getTranslation(DATA_SET_UPDATED));
		}else{
			try {
				dataSetAcceptanceService.acceptUpdateDataSet(selectedDataSet);
			} catch (DatasetIllegalStatusException e) {
				FacesMessage message = Messages.getMessage(null, "updateDatasets_datasetIllegalStatus", null);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				return;
			}
		}
		FacesMessage message = Messages.getMessage(null, "datasetHasBeenUpdated", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
		
		FacesUtils.redirectToPage("/secured/datasets?approved=false");
	}
	
	public void rejectRegistration() {
		dataSetAcceptanceService.rejectDataSets(getDataSetsToReject(), messageSource.getTranslation(DATA_SET_REJECTED_BY_ADMIN));
		
		FacesMessage message = Messages.getMessage(null, "datasetsHaveBeenRejected", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
		
		FacesUtils.redirectToPage("/secured/datasets?approved=false");
	}
	
	
	private List<DataSet> getDataSetsToReject(){
		for(DataSet ds: datasets){
			if(ds.getStatus().equals(DataSetStatus.ACCEPTED)){
				datasets.remove(ds);
				break;
			}
		}
		return datasets;
	}
	

}
