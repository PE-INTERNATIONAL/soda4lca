package eu.europa.ec.jrc.lca.registry.service;

import java.util.List;

import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.Node;

public interface NotificationService {
	/**
	 * Sends mail to registry admins with information about node registration request
	 * @param node
	 */
	void notifyRegistryAdminsAboutNodeRegistrationRequest(Node node);
	
	/**
	 * Sends mail to registry admins with information about node deregistration request
	 * @param node
	 */
	void notifyRegistryAdminsAboutNodeDeregistrationRequest(Node node);
	
	/**
	 * Sends mail to node admin with information about node registration request acceptance
	 * @param node
	 */
	void notifyNodeAdminAboutNodeAcceptance(Node node);
	
	/**
	 * Sends mail to node admin with information about node registration request rejection
	 * @param node
	 * @param reason
	 */
	void notifyNodeAdminAboutNodeRejection(Node node, String reason);
	
	/**
	 * Sends mail to node admin with information about node deregistration
	 * @param node
	 * @param reason
	 */
	void notifyNodeAdminAboutNodeDeregistration(Node node, String reason);
	
	/**
	 * Sends mail to node user with information about dataset registration acceptance
	 * @param datasets
	 */
	void notifyNodeUserAboutDataSetsAcceptance(List<DataSet> datasets);
	
	/**
	 * Sends mail to node user with information about dataset registration rejection
	 * @param datasets
	 * @param reason
	 */
	void notifyNodeUserAboutDataSetsRejection(List<DataSet> datasets, String reason);
	
	/**
	 * Sends mail to node user with information about dataset deregistration
	 * @param datasets
	 * @param reason
	 */
	void notifyNodeUserAboutDataSetsDeregistration(List<DataSet> datasets, String reason);
	
	/**
	 * Sends mail to registry admins with information about dataset registration request
	 * @param node
	 * @param size
	 */
	void notifyRegistryAdminsAboutDataSetsRegistrationRequest(Node node,
			int size);
	
	//For test purposes only
	void setSendMailService(SendMailService sendMailService);

}
