package eu.europa.ec.jrc.lca.registry.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.europa.ec.jrc.lca.registry.service.SendMailService;
import eu.europa.ec.jrc.lca.registry.util.ILCDMessageSource;

@Service("sendMailService")
public class SendMailServiceImpl implements SendMailService {

	private static final String MAIL_SMTP_FROM = "mail.smtp.from";

	private Session mailSession;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SendMailServiceImpl.class);

	@Autowired
	private ILCDMessageSource messageSource;

	public void setMailSession(Session mailSession) {
		this.mailSession = mailSession;
	}

	@Async
	public void sendMail(List<String> to, String subject, String messageText) {
		MimeMessage message = new MimeMessage(mailSession);
		try {
			message.addFrom(new Address[] { new InternetAddress(mailSession
					.getProperty(MAIL_SMTP_FROM)) });
			for (String recipient : to) {
				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(recipient));
			}
			message.setSubject(subject);
			message.setContent(messageText, "text/html; charset=utf-8");
			Transport.send(message);
		} catch (AddressException e) {
			LOGGER.error("[sendMail]", e);
		} catch (MessagingException e) {
			LOGGER.error("[sendMail]", e);
		}
	}

	@Async
	public void sendMail(String to, String subject, String messageText) {
		List<String> recipients = new ArrayList<String>();
		recipients.add(to);
		sendMail(recipients, subject, messageText);
	}

	@Async
	public void sendMail(List<String> to, String subjectKey, String messageKey,
			Object[] subjectParams, Object[] messageParams) {
		String subject = messageSource
				.getTranslation(subjectKey, subjectParams);
		String messageBody = messageSource.getTranslation(messageKey,
				messageParams);
		sendMail(to, subject, messageBody);
	}

	@Async
	public void sendMail(String to, String subjectKey, String messageKey,
			Object[] subjectParams, Object[] messageParams) {
		List<String> recipients = new ArrayList<String>();
		recipients.add(to);
		sendMail(recipients, subjectKey, messageKey, subjectParams,
				messageParams);
	}

}
