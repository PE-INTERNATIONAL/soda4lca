package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.jrc.lca.registry.dao.UserDao;
import eu.europa.ec.jrc.lca.registry.domain.DataSet;
import eu.europa.ec.jrc.lca.registry.domain.Node;
import eu.europa.ec.jrc.lca.registry.domain.User;
import eu.europa.ec.jrc.lca.registry.service.NotificationService;
import eu.europa.ec.jrc.lca.registry.service.SendMailService;

@Transactional
@Service("notificationService")
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private SendMailService sendMailService;

	@Value("${registry.name}")
	private String registryName;
	
	private static final String ADMIN_NOTIFICATION_NODE_REGISTRATION_REQUEST_TITLE = "registryAdminNotification.registrationRequest.title";
	private static final String ADMIN_NOTIFICATION_NODE_REGISTRATION_REQUEST_CONTENT = "registryAdminNotification.registrationRequest.content";

	private static final String ADMIN_NOTIFICATION_NODE_DEREGISTRATION_REQUEST_TITLE = "registryAdminNotification.deregistrationRequest.title";
	private static final String ADMIN_NOTIFICATION_NODE_DEREGISTRATION_REQUEST_CONTENT = "registryAdminNotification.deregistrationRequest.content";

	private static final String NODE_ADMIN_NOTIFICATION_REGISTRATION_ACCEPTED_TITLE = "nodeAdminNotification.registrationAccepted.title";
	private static final String NODE_ADMIN_NOTIFICATION_REGISTRATION_ACCEPTED_CONTENT = "nodeAdminNotification.registrationAccepted.content";
	
	private static final String NODE_ADMIN_NOTIFICATION_NODE_DEREGISTRATION_TITLE = "nodeAdminNotification.nodeDeregistration.title";
	private static final String NODE_ADMIN_NOTIFICATION_NODE_DEREGISTRATION_CONTENT = "nodeAdminNotification.nodeDeregistration.content";

	private static final String NODE_ADMIN_NOTIFICATION_REGISTRATION_REJECTED_TITLE = "nodeAdminNotification.registrationRejected.title";
	private static final String NODE_ADMIN_NOTIFICATION_REGISTRATION_REJECTED_CONTENT = "nodeAdminNotification.registrationRejected.content";

	private static final String ADMIN_NOTIFICATION_DS_REGISTRATION_REQUEST_TITLE = "registryAdminNotification.datasetRegistrationRequest.title";
	private static final String ADMIN_NOTIFICATION_DS_REGISTRATION_REQUEST_CONTENT = "registryAdminNotification.datasetRegistrationRequest.content";

	private static final String NODE_USER_NOTIFICATION_DS_REGISTRATION_ACCEPTED_TITLE = "nodeUserNotification.datasetRegistrationAccepted.title";
	private static final String NODE_USER_NOTIFICATION_DS_REGISTRATION_ACCEPTED_CONTENT = "nodeUserNotification.datasetRegistrationAccepted.content";

	private static final String NODE_USER_NOTIFICATION_DS_REGISTRATION_REJECTED_TITLE = "nodeUserNotification.datasetRegistrationRejected.title";
	private static final String NODE_USER_NOTIFICATION_DS_REGISTRATION_REJECTED_CONTENT = "nodeUserNotification.datasetRegistrationRejected.content";

	private static final String NODE_USER_NOTIFICATION_DS_DEREGISTRATION_TITLE = "nodeUserNotification.datasetDeregistration.title";
	private static final String NODE_USER_NOTIFICATION_DS_DEREGISTRATION_CONTENT = "nodeUserNotification.datasetDeregistration.content";

	@Override
	public void notifyRegistryAdminsAboutNodeRegistrationRequest(Node node) {
			sendMailToAllRegistryUsers(
					ADMIN_NOTIFICATION_NODE_REGISTRATION_REQUEST_TITLE,
					ADMIN_NOTIFICATION_NODE_REGISTRATION_REQUEST_CONTENT, null,
					new Object[] { node.getNodeId() });
	}

	@Override
	public void notifyRegistryAdminsAboutNodeDeregistrationRequest(Node node) {
			sendMailToAllRegistryUsers(
					ADMIN_NOTIFICATION_NODE_DEREGISTRATION_REQUEST_TITLE,
					ADMIN_NOTIFICATION_NODE_DEREGISTRATION_REQUEST_CONTENT,
					null, new Object[] { node.getNodeId() });
	}

	@Override
	public void notifyNodeAdminAboutNodeDeregistration(Node node, String reason) {
			sendMailService.sendMail(node.getAdminEmailAddress(),
					NODE_ADMIN_NOTIFICATION_NODE_DEREGISTRATION_TITLE,
					NODE_ADMIN_NOTIFICATION_NODE_DEREGISTRATION_CONTENT,
					null, new Object[] {node.getNodeId(), registryName, reason });
	}

	@Override
	public void notifyNodeAdminAboutNodeAcceptance(Node node) {
			sendMailService.sendMail(node.getAdminEmailAddress(),
					NODE_ADMIN_NOTIFICATION_REGISTRATION_ACCEPTED_TITLE,
					NODE_ADMIN_NOTIFICATION_REGISTRATION_ACCEPTED_CONTENT,
					null, new Object[] { node.getNodeId(), registryName });
	}

	@Override
	public void notifyNodeAdminAboutNodeRejection(Node node, String reason) {
			sendMailService.sendMail(node.getAdminEmailAddress(),
					NODE_ADMIN_NOTIFICATION_REGISTRATION_REJECTED_TITLE,
					NODE_ADMIN_NOTIFICATION_REGISTRATION_REJECTED_CONTENT,
					null, new Object[] {node.getNodeId(),registryName, reason });
	}

	@Override
	public void notifyRegistryAdminsAboutDataSetsRegistrationRequest(Node node,
			int size) {
			sendMailToAllRegistryUsers(
					ADMIN_NOTIFICATION_DS_REGISTRATION_REQUEST_TITLE,
					ADMIN_NOTIFICATION_DS_REGISTRATION_REQUEST_CONTENT, null,
					new Object[] { node.getNodeId(), size});
	}

	@Override
	public void notifyNodeUserAboutDataSetsAcceptance(List<DataSet> datasets) {
		notifyNodeUserAbout(datasets, new Object[] { registryName, datasets.size()},
				NODE_USER_NOTIFICATION_DS_REGISTRATION_ACCEPTED_TITLE,
				NODE_USER_NOTIFICATION_DS_REGISTRATION_ACCEPTED_CONTENT);
	}

	@Override
	public void notifyNodeUserAboutDataSetsRejection(List<DataSet> datasets,
			String reason) {
		notifyNodeUserAbout(datasets, new Object[] { registryName, datasets.size(), reason},
				NODE_USER_NOTIFICATION_DS_REGISTRATION_REJECTED_TITLE,
				NODE_USER_NOTIFICATION_DS_REGISTRATION_REJECTED_CONTENT);
	}

	@Override
	public void notifyNodeUserAboutDataSetsDeregistration(
			List<DataSet> datasets, String reason) {
		notifyNodeUserAbout(datasets, new Object[] {registryName, datasets.size(), reason},
				NODE_USER_NOTIFICATION_DS_DEREGISTRATION_TITLE,
				NODE_USER_NOTIFICATION_DS_DEREGISTRATION_CONTENT);
	}

	private void notifyNodeUserAbout(List<DataSet> datasets, Object[] params,
			String title, String content) {
		Map<String, List<DataSet>> recipients = groupUser(datasets);
		for (Entry<String, List<DataSet>> en : recipients.entrySet()) {
			sendMailService.sendMail(en.getKey(), title, content, null,params);
		}
	}

	private void sendMailToAllRegistryUsers(String subjectKey,
			String messageKey, Object[] subjectParams, Object[] messageParams){
		List<User> users = userDao.findAll();
		sendMailService.sendMail(getRecpientsList(users), subjectKey,
				messageKey, subjectParams, messageParams);
	}

	private List<String> getRecpientsList(List<User> users) {
		List<String> recipients = new ArrayList<String>();
		for (User u : users) {
			recipients.add(u.getEmail());
		}
		return recipients;
	}

	private String getCommaSeparatedUuids(List<DataSet> datasets) {
		StringBuilder uuids = new StringBuilder();
		for (DataSet ds : datasets) {
			uuids.append(ds.getUuid()).append(", ");
		}
		return uuids.toString();
	}

	private Map<String, List<DataSet>> groupUser(List<DataSet> datasets) {
		Map<String, List<DataSet>> grouped = new HashMap<String, List<DataSet>>();
		for (DataSet ds : datasets) {
			if (ds.getUserEmail() != null
					&& !grouped.containsKey(ds.getUserEmail())) {
				grouped.put(ds.getUserEmail(), new ArrayList<DataSet>());
			}
			if (ds.getUserEmail() != null) {
				grouped.get(ds.getUserEmail()).add(ds);
			}
		}
		return grouped;
	}

	@Override
	public void setSendMailService(SendMailService sendMailService) {
		this.sendMailService = sendMailService;
	}

}
