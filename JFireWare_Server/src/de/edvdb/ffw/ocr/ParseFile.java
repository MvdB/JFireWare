package de.edvdb.ffw.ocr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.edvdb.ffw.beans.Adresse;
import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.beans.Field;
import de.edvdb.ffw.system.LayoutConfig;
import de.edvdb.ffw.system.ServerConfig;
import de.edvdb.ffw.util.ServerUtils;
import de.edvdb.ffw.util.Utils;

/**
 * 
 * @author mvdb
 */
public class ParseFile {
	private static Logger log = Logger.getLogger(ParseFile.class);

	private static String SPACE = " ";

	public static Alarmfax analyzeFile(File txtFile) {
		Alarmfax fax = new Alarmfax();
		if (txtFile == null || !txtFile.exists()) {
			return null;
		}
		log.trace("Parsing file '" + txtFile.getAbsolutePath() + "'");
		try {
			ArrayList<String> lines = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(txtFile));
			Adresse adress = new Adresse();
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				if (!currentLine.contains("----")) {
					lines.add(ServerUtils.replaceWhitespaces(ServerUtils.replacePossibleAlphaMistakes(currentLine)));
				}
			}
			getAdressData(lines, adress);
			Set<String> tmpSet = new HashSet<String>(checkFieldInfo(lines,
					LayoutConfig.FAHRZEUG));
			fax.setFahrzeuge(tmpSet);
			tmpSet = new HashSet<String>(checkFieldInfo(lines,
					LayoutConfig.EINSATZMITTEL));
			fax.setEinsatzmittel(tmpSet);
			tmpSet = new HashSet<String>(checkFieldInfo(lines,
					LayoutConfig.SCHLAGWORT));
			fax.setSchlagwort(tmpSet);
			tmpSet = new HashSet<String>(checkFieldInfo(lines,
					LayoutConfig.ZUSTAENDIGKEIT));
			fax.setZustaendigkeit(tmpSet);
			fax.setBemerkung(checkFieldInfo(lines, LayoutConfig.BEMERKUNG));
			adress.validate(); // Validate before save
			ServerUtils.checkPLZ(adress);
			fax.setAdresse(adress);
			br.close();
		} catch (Exception e) {
			log.error("Parsing failed..", e);
		}
		return fax;
	}

	private static void getAdressData(ArrayList<String> lines, Adresse adress) {
		adress.setStrasse(checkSingleFieldInfo(lines, LayoutConfig.STRASSE));
		if (!Utils.containsNumerics(adress.getStrasse())) {
			adress.setHausnummer(checkSingleFieldInfo(lines, LayoutConfig.HAUSNUMMER));
		} else {
			// Falls die Strasse numerische Inhalte hat, handelt es sich meist
			// um eine Autobahn oder Bundesstrasse, also ohne Hausnummer
		}
		adress.setPlz(checkSingleFieldInfo(lines, LayoutConfig.PLZ));
		adress.setOrt(checkSingleFieldInfo(lines, LayoutConfig.ORT));
	}

	private static String checkSingleFieldInfo(ArrayList<String> lines,
			Field field) {
		String toReturn = "";
		List<String> list = checkFieldInfo(lines, field);
		if (list != null && list.size() > 0) {
			toReturn = list.get(0);
		}
		return toReturn;
	}

	private static List<String> checkFieldInfo(ArrayList<String> lines,
			Field field) {
		List<String> results = new ArrayList<String>();
		if (field != null) {
			String[] findPatterns = field.getFindPattern().split("\\+");
			log.trace("Field  : " + field.getId());
			for(String findPattern : findPatterns) {
				log.trace("Pattern: " + findPattern);
				// Iteration über alle Zeilen des Dokuments
				for (int linenr = 0; linenr < lines.size(); linenr++) {
					String baseString = lines.get(linenr);
					// Feld-Muster enthalten
					if (ServerUtils.replacePossibleAlphaMistakes(baseString).contains(findPattern)) {
						// Relevante Zeile (inkl Offset) holen
						String workingString = baseString;
						int offsetNr = linenr + field.getLineOffset();
						if (offsetNr > 0 && offsetNr < lines.size()) {
							workingString = lines.get(offsetNr);
						} else if (offsetNr == 0) {
							// Nachverarbeitung falls gleiche Zeile ?
						} else {
							log.warn("Offset von " + offsetNr
									+ " Zeilen war ungültig für Feld '"
									+ field.getId() + "'");
						}
	
						// Reine 'alpha'-Strings verarbeiten
						if (field.isAlphaOnly()) {
							workingString = Utils.clearNumerics(workingString);
						}
	
						// Bereich zwischen Start- und End-Pattern extrahieren
						// (sofern vorhanden)
						int startIndex = (field.getStartPattern().equalsIgnoreCase(
								"") ? 0 : workingString.indexOf(field
								.getStartPattern()));
						if((startIndex != 0) && field.isExcludeStartpattern()) {
							startIndex += field.getStartPattern().length();
						}
						int endIndex = (field.getEndPattern().equalsIgnoreCase("") ? workingString
								.length() : workingString.indexOf(field
								.getEndPattern()));
						if (endIndex == -1) {
							log.info("Endmuster '" + field.getEndPattern()
									+ "' nicht gefunden für Feld '" + field.getId()
									+ "'.");
							endIndex = workingString.length();
						}
						workingString = workingString.substring(startIndex,
								endIndex);
	
						// verbliebenen String an den Whitespaces aufteilen
						String[] segments = ServerUtils.replaceWhitespaces(workingString)
								.split(SPACE);
	
						// Index aufbereiten
						int index = field.getIndex();
						if (index < 0) {
							index = segments.length + index;
						} else if (index > 0) {
							index--;
						}
	
						// Relevante(s) Element(e) extrahieren
						// Es werden nur Elemente übernommen, die nicht vollständig
						// dem Start- oder Suchmuster entsprechen
						String tmp = "";
						if (!field.isExtendRight()
								&& (index < segments.length)
								&& (!segments[index].equalsIgnoreCase(field
										.getStartPattern()))
								&& (!segments[index].equalsIgnoreCase(field
										.getFindPattern()))) {
							tmp = segments[index];
						} else {
							if ((index < segments.length)
									&& ((segments[index].equalsIgnoreCase(field
											.getStartPattern())) || (segments[index]
											.equalsIgnoreCase(field
													.getFindPattern())))) {
								index++;
							}
							for (int j = index; j < segments.length; j++) {
								tmp += segments[j] + " ";
							}
						}
	
						// Überflüssige Leerzeichen und Whitespaces entfernen
						tmp = ServerUtils.replaceWhitespaces(tmp).trim();
	
						// Nur 'echte' Inhalte übernehmen
						if (!tmp.equalsIgnoreCase("")) {
							results.add(tmp);
						}
	
						// Ende, falls das Feld nur einfach belegbar ist
						if (field.isUnique()) {
							break;
						} else if (field.isExtendDown()) {
							// Falls 'extendDown' aktiv ist werden ALLE folgenden
							// Zeilen bis zum Endmuster ebenfalls angefügt
							for (int i = ++offsetNr; i < lines.size(); i++) {
								if (lines.get(i).equalsIgnoreCase("")
										|| (field.getEndPattern().equalsIgnoreCase(
												"") ? false : lines.get(i)
												.contains(field.getEndPattern()))) {
									break;
								} else {
									results.add(lines.get(i));
								}
							}
						}
					}
				}
			}
		}
		log.debug(results);
		return results;
	}
}