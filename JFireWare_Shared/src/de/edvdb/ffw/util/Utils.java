package de.edvdb.ffw.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Utils {
	private static Logger log = Logger.getLogger(Utils.class);
	// Hilfsmittel
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"ddMMyyyy_HHmmss");
	public static final SimpleDateFormat readableFormat = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm:ss");
	public static final SimpleDateFormat sdfDays = new SimpleDateFormat(
			"dd.MM.yyyy");
	public static final SimpleDateFormat sdfMinutes = new SimpleDateFormat(
			"HH:mm:ss");
	private static final long MILLISEC_PER_MINUTE = 1000 * 60;
	private static Pattern numPattern = Pattern.compile(".*[0-9].*");
	public static String NEWLINE = System.getProperty("line.separator");

	public static String encodeURL(String toMark) {
		String s = toMark.replaceAll(" ", "+");
		try {
			s = URLEncoder.encode(toMark, "ISO-8859-1");
		} catch (Exception e) {
			log.warn("Failed to URLencode '" + toMark + "'");
		}
		return s;
	}


	public static String replacePossibleNumericMistakes(String toCorrect) {
		toCorrect = toCorrect.replaceAll("Z", "2");
		toCorrect = toCorrect.replaceAll("B", "8");
		toCorrect = toCorrect.replaceAll("G", "6");
		return toCorrect;
	}



	public static String getCurrentDatetime() {
		Date now = new Date();
		return dateFormat.format(now);
	}


	public static boolean containsNumerics(List<String> strasse) {
		boolean containsNumeric = false;
		for (String s : strasse) {
			containsNumeric |= containsNumerics(s);
		}
		return containsNumeric;
	}

	public static boolean containsNumerics(String toCheck) {
		Matcher matcher = numPattern.matcher(toCheck);
		return matcher.matches();
	}

	public static String clearNumerics(String workingString) {
		Pattern pattern = Pattern.compile("[0-9]");
		Matcher matcher = pattern.matcher(workingString);
		return matcher.replaceAll(" ");
	}
	
	public static String extractNumerics(String workingString) {
		Pattern pattern = Pattern.compile("\\D+");
		Matcher matcher = pattern.matcher(workingString);
		return matcher.replaceAll("");
	}
	

	public static String replaceSpecialChars(String tmp) {
		tmp = tmp.replaceAll("ä", "ae");
		tmp = tmp.replaceAll("Ä", "Ae");
		tmp = tmp.replaceAll("ö", "oe");
		tmp = tmp.replaceAll("Ö", "Oe");
		tmp = tmp.replaceAll("ü", "ue");
		tmp = tmp.replaceAll("Ü", "Ue");
		tmp = tmp.replaceAll("ß", "ss");
		return tmp;
	}

	public static Date getDateByOffset(int hours, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, hours);
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	public static String getDateFromDate(Date toFormat) {
		return sdfDays.format(toFormat);
	}

	public static String getTimeFromDate(Date toFormat) {
		return sdfMinutes.format(toFormat);
	}

	public static String getSetAsMultilineHTMLString(Set<String> toFormat) {
		return getListAsMultilineHTMLString(new ArrayList<String>(toFormat));
	}

	public static String getListAsMultilineHTMLString(List<String> toFormat) {
		if (toFormat == null) {
			return "";
		} else {
			String content = "<html>";
			for (String s : toFormat) {
				content += s + "<br/>";
			}
			content += "</html>";
			return content;
		}
	}

	public static String getTimeDiff(Date timestamp) {
		long minutes = Utils.getTimeDiffAsLong(timestamp);
		if(minutes < 60) {
			return minutes + " min";
		} else {
			return (minutes / 60) + " h / " + (minutes % 60) + " min"; 
		}
	}
	
	public static long getTimeDiffAsLong(Date timestamp) {
		return Math.round(((new Date().getTime() - timestamp.getTime()) / MILLISEC_PER_MINUTE));
	}
}
