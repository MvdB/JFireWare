package de.edvdb.ffw.web;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.beans.Empfaenger;
import de.edvdb.ffw.beans.Notification;
import de.edvdb.ffw.beans.NotificationType;
import de.edvdb.ffw.db.Database;
import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.system.ServerConfig;
import de.edvdb.ffw.util.Utils;
import de.edvdb.ffw.web.util.HTTPAccess;

public class SMS extends Thread {
	private static Logger log = Logger.getLogger(SMS.class);
	public static final String NL = System.getProperty("line.separator");
	public static final String BLANK = "";
	private Alarmfax fax;

	public SMS(Alarmfax fax) {
		this.fax = fax;
	}

	@SuppressWarnings("unchecked")
	public void generateSMS() {
		String SMSText = "";
		SMSText += "Alarm für FFW " + Config.FFWNAME + NL;

		SMSText += "Sendezeit: " + fax.getReadableTimestamp() + NL;

		// Einsatzort
		SMSText += "Einsatzort: " + fax.getAdresse() + NL;

		// Schlagwort aufbereiten
		String schlagwort = BLANK;
		for (String s : fax.getSchlagwort()) {
			schlagwort += s + " ";
		}
		schlagwort = schlagwort.trim();
		if (!schlagwort.equalsIgnoreCase(BLANK)) {
			SMSText += "Schlagwort: " + schlagwort + NL;
		}
		if (SMSText.length() > ServerConfig.SMSMSGLENGTH) {
			SMSText = SMSText.substring(0, ServerConfig.SMSMSGLENGTH);
			log.info("Truncated SMSText to " + ServerConfig.SMSMSGLENGTH + " chars.");
		}
		String fullURL = ServerConfig.SMSURL;
		fullURL = fullURL.replace("%MESSAGE%", Utils.encodeURL(SMSText));
		List<Empfaenger> alleEmpfaenger = Database.getSession().createCriteria(Empfaenger.class).add(Restrictions.eq("smsEnable", true)).list();
		for(Empfaenger e : alleEmpfaenger) {
			String accessURL = fullURL.replace("%RECIPIENT%", e.getHandynummer());
			if (log.isDebugEnabled() || log.isTraceEnabled()) {
				accessURL += "&debug=1";
				log.debug(accessURL);
			}
			String rc = HTTPAccess.accessURL(accessURL);
			Database.getSession().save(new Notification(e, fax, NotificationType.SMS, rc, new Date()));
			Database.getSession().flush();
			log.info("SMS sent to '" + e + "' [" + e.getHandynummer() + "]");
		}
	}

	@Override
	public void run() {
		generateSMS();
	}
}
