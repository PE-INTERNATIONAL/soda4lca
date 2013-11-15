package eu.europa.ec.jrc.lca.registry.domain;

public enum NodeOperation {
	
	REGISTRATION(false),
	REGISTRATION_ACCEPTANCE(false),
	REGISTRATION_REJECTION(false),
	DEREGISTRATION(false),
	
	DATASET_REGISTRATION_REQUEST(true),
	DATASET_REGISTRATION_REQUEST_ACCEPTANCE(true),
	DATASET_REGISTRATION_ACCEPTANCE(true),
	DATASET_REGISTRATION_REJECTION(true),
	DATASET_DEREGISTRATION_BY_NODE(true),
	DATASET_DEREGISTRATION_BY_REGISTRY(true);
	
	private boolean affectsDataset;
	
	NodeOperation(boolean affectsDataset){
		this.affectsDataset = affectsDataset;
	}

	public boolean isAffectsDataset() {
		return affectsDataset;
	}
	
}
