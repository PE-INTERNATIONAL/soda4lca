package eu.europa.ec.jrc.lca.registry.service;

import java.util.List;

public interface SendMailService {

	/**
	 * @param to - list of recipients
	 * @param subject
	 * @param messageText
	 */
	void sendMail(List<String> to, String subject, String messageText);

	/**
	 * @param to - list of recipients
	 * @param subject
	 * @param messageText
	 */
	void sendMail(String to, String subject, String messageText);
	
	/**
	 * @param to - list of recipients
	 * @param subjectKey - bundle key
	 * @param messageKey - bundle key
	 * @param subjectParams - bundle key
	 * @param messageParams - bundle key
	 */
	void sendMail(List<String> to, String subjectKey, String messageKey,
			Object[] subjectParams, Object[] messageParams);
	
	/**
	 * @param to - list of recipients
	 * @param subjectKey - bundle key
	 * @param messageKey - bundle key
	 * @param subjectParams - bundle key
	 * @param messageParams - bundle key
	 */
	void sendMail(String to, String subjectKey, String messageKey,
			Object[] subjectParams, Object[] messageParams);
}
