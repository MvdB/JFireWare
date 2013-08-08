package de.edvdb.ffw.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.beans.Empfaenger;
import de.edvdb.ffw.beans.Notification;
import de.edvdb.ffw.beans.NotificationType;
import de.edvdb.ffw.db.Database;
import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.system.ServerConfig;

public class Mailer {
	private static Logger log = Logger.getLogger(Mailer.class);

	public static void sendMail(File file, Alarmfax fax) {
		String subject = "Einsatz für " + Config.FFWNAME;

		String text = "Einsatz für " + Config.FFWNAME;
		text += "\n" + fax.getAdresse().toString();
		text += "\nFahrzeuge: " + fax.getFahrzeuge().toString();
		text += "\nEinsatzmittel: " + fax.getEinsatzmittel().toString();

		sendMail(text, subject, file, false, fax);
	}

	public static void sendMail(String text, String subject, File file,
			boolean adminOnly) {
		sendMail(text, subject, file, adminOnly, null);
	}

	@SuppressWarnings("unchecked")
	public static void sendMail(String text, String subject, File file,
			boolean adminOnly, Alarmfax fax) {
		log.debug("Preparing mail..");
		Properties props = new Properties();
		props.put("mail.smtp.host", ServerConfig.MAILSERVER);
		if (ServerConfig.USESSL) {
			props.put("mail.smtp.socketFactory.port", ServerConfig.MAILPORT);
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "true");
		} else {
			props.put("mail.smtp.port", ServerConfig.MAILPORT);
		}
		if (ServerConfig.MAILAUTH) {
			props.put("mail.smtp.auth", ServerConfig.MAILAUTH);
		}

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								ServerConfig.MAILUSER,
								ServerConfig.MAILPASSWORD);
					}
				});

		try {
			MimeMessage message = new MimeMessage(session);
			Multipart mp = new MimeMultipart();

			message.setFrom(new InternetAddress(ServerConfig.MAILFROM));

			if (!adminOnly) {
				List<Empfaenger> alleEmpfaenger = Database.getSession()
						.createCriteria(Empfaenger.class)
						.add(Restrictions.eq("emailEnable", true)).list();
				List<InternetAddress> mailEmpfaenger = new ArrayList<InternetAddress>();
				for (Empfaenger e : alleEmpfaenger) {
					try {
						InternetAddress address = new InternetAddress(e.getEmail(), true);
						mailEmpfaenger.add(address);
						Database.getSession().save(
								new Notification(e, fax,
										NotificationType.EMAIL, null,
										new Date()));
						Database.getSession().flush();
					} catch (Exception ex) {
						log.error("Failed to add recipient '" + e.getEmail()
								+ "'");
					}
				}
				message.addRecipients(Message.RecipientType.TO, mailEmpfaenger.toArray(new Address[mailEmpfaenger.size()]));
				message.addRecipients(Message.RecipientType.CC,
						ServerConfig.ADMINMAIL);
			} else {
				message.addRecipients(Message.RecipientType.TO,
						ServerConfig.ADMINMAIL);
			}
			message.setSubject(subject);

			// Text
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(text);
			mp.addBodyPart(mbp1);

			if ((file != null) && (file.exists())) {
				MimeBodyPart mbp2 = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(file);
				mbp2.setDataHandler(new DataHandler(fds));
				mbp2.setFileName(fds.getName());
				mp.addBodyPart(mbp2);
			}

			message.setContent(mp);
			message.setSentDate(new Date());
			Transport.send(message);
			for (Address a : message.getAllRecipients()) {
				log.debug("Mail sent to '" + a.toString() + "'");
			}
			log.info("Mail has successfully sent");
		} catch (MessagingException e) {
			log.error("Error occured while sending mails", e);
			throw new RuntimeException(e);
		}
	}
}
