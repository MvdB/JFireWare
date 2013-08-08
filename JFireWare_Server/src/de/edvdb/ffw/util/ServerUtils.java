package de.edvdb.ffw.util;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import de.edvdb.ffw.beans.Adresse;
import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.db.Database;
import de.edvdb.ffw.system.ServerConfig;

public class ServerUtils {
	private static Logger log = Logger.getLogger(ServerUtils.class);
	
	@SuppressWarnings("unchecked")
	public static boolean isFollowup(Alarmfax hasFollowUp) {
		boolean isFollowup = false;
		Date minDate = Utils.getDateByOffset(ServerConfig.FOLLOWUPHOURS * -1,
				ServerConfig.FOLLOWUPMINUTES * -1);
		Date maxDate = Utils.getDateByOffset(0, 5);
		List<Alarmfax> faxe = Database.getSession()
				.createCriteria(Alarmfax.class)
				.add(Restrictions.between("timestamp", minDate, maxDate))
				.list();
		for (Alarmfax fax : faxe) {
			isFollowup |= hasFollowUp.getAdresse().equals(fax.getAdresse());
			log.debug(fax + "Followup: " + isFollowup);
		}
		return isFollowup;
	}
	
	public static void checkPLZ(Adresse a) {
		// Validate PLZ to correct OCR-Mistakes (e.g. to PLZ-Range 8xxxx)
		Pattern plzPattern = Pattern.compile("[0-9]{5}");
		Matcher matcher = plzPattern.matcher(a.getPlz());
		if(matcher.find()) {
			String plz = matcher.group();
			Pattern p = Pattern.compile(ServerConfig.PLZPATTERN_FROM);
			Matcher m = p.matcher(plz);
			a.setPlz(m.replaceFirst(ServerConfig.PLZPATTERN_TO));
		}
	}
	
	public static String replacePossibleAlphaMistakes(String toCorrect) {
		String tmp = new String(toCorrect);
		for (String[] row : ServerConfig.POSSIBLEMISTAKES) {
			toCorrect = toCorrect.replaceAll(row[ServerConfig.FROM], row[ServerConfig.TO]);
			if (log.isTraceEnabled()) {
				log.trace("Utils / AlphaMistakes '" + tmp + "' -> '"
						+ toCorrect + "'");
			}
		}

		return toCorrect;
	}
	
	/***
	 * Entfernt doppelte Leerzeichen, Tabs, sonstige Whitespaces und
	 * unerwuenschte Zeichen
	 * 
	 * @param toCorrect
	 * @return String ohne unerwuenschte Zeichen
	 */
	public static String replaceWhitespaces(String toCorrect) {
		for (String pattern : ServerConfig.WHITESPACES) {
			toCorrect = toCorrect.replace(pattern, "");
		}
		toCorrect = toCorrect.replace("  ", " ");
		toCorrect = toCorrect.trim();
		String patternStr = "\\s+";
		String replaceStr = " ";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(toCorrect);
		return matcher.replaceAll(replaceStr);
	}
	

	public static void print(File pdfFile) {
		try {
			log.trace("Printing file '" + pdfFile.getAbsolutePath() + "'");
			String pdfPath = pdfFile.getCanonicalPath();
			String params = ServerConfig.PDFPARAM;
			params = params.replace("%IMAGE%", pdfPath);
			Runtime rt = Runtime.getRuntime();
			String command = ServerConfig.PDFPATH + " " + params;
			log.trace("Command [" + command + "]");
			log.debug("Printing Alarmfax..");
			Process pr = rt.exec(command);
			pr.waitFor();
		} catch (Exception e) {
			log.error("Printing failed", e);
		}
	}
}
