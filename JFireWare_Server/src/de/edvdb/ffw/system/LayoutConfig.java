package de.edvdb.ffw.system;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import de.edvdb.ffw.beans.Field;

public class LayoutConfig {
	private static Logger log = Logger.getLogger(LayoutConfig.class);

	private static XMLConfiguration config = new XMLConfiguration();
	private static final String CONFIGFILE = "../conf/layout.xml";
	private static Long CONFIGTIME = 0L;
	// Alle verfügbaren Felder
	public static Field STRASSE;
	public static Field HAUSNUMMER;
	public static Field PLZ;
	public static Field ORT;
	public static Field FAHRZEUG;
	public static Field EINSATZMITTEL;
	public static Field SCHLAGWORT;
	public static Field BEMERKUNG;
	public static Field ZUSTAENDIGKEIT;
	
	public static final int FROM = 0;
	public static final int TO = 1;

	
	public boolean checkConfig() {
		boolean reloaded = false;
		File configFile = new File(CONFIGFILE);
		Long lastModified = configFile.lastModified();
		if (CONFIGTIME.compareTo(lastModified) != 0) {
			CONFIGTIME = lastModified;
			initConfig();
			reloaded = true;
		}
		return reloaded;
	}
	
	@SuppressWarnings("unchecked")
	public void initConfig() {
		try {

			config = new XMLConfiguration();
			config.setValidating(false);
			config.setFileName(CONFIGFILE);
			config.load();
			if (CONFIGTIME == null) {
				CONFIGTIME = new File(CONFIGFILE).lastModified();
			}

			log.info("Configuration loaded..");

			List<String> ids = config.getList("layout.field[@id]");
			List<String> findPattern = config.getList("layout.field[@findPattern]");
			List<String> index = config.getList("layout.field[@index]");
			List<String> startPattern = config
					.getList("layout.field[@startPattern]");
			List<String> endPattern = config.getList("layout.field[@endPattern]");
			List<String> extendRight = config.getList("layout.field[@extendRight]");
			List<String> extendDown = config.getList("layout.field[@extendDown]");
			List<String> lineOffset = config.getList("layout.field[@lineOffset]");
			List<String> unique = config.getList("layout.field[@unique]");
			List<String> isAlphaOnly = config.getList("layout.field[@isAlphaOnly]");
			List<String> excludeStartpattern = config.getList("layout.field[@excludeStartpattern]");


			for (int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				if (id.equalsIgnoreCase("Strasse"))
					STRASSE = new Field(ids.get(i), findPattern.get(i),
							index.get(i), extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
				if (id.equalsIgnoreCase("Hausnummer"))
					HAUSNUMMER = new Field(ids.get(i), findPattern.get(i),
							index.get(i), extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
				if (id.equalsIgnoreCase("PLZ"))
					PLZ = new Field(ids.get(i), findPattern.get(i), index.get(i),
							extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
				if (id.equalsIgnoreCase("Ort"))
					ORT = new Field(ids.get(i), findPattern.get(i), index.get(i),
							extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
				if (id.equalsIgnoreCase("Fahrzeug"))
					FAHRZEUG = new Field(ids.get(i), findPattern.get(i),
							index.get(i), extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
				if (id.equalsIgnoreCase("Gerät"))
					EINSATZMITTEL = new Field(ids.get(i), findPattern.get(i),
							index.get(i), extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
				if (id.equalsIgnoreCase("Schlagwort"))
					SCHLAGWORT = new Field(ids.get(i), findPattern.get(i),
							index.get(i), extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
				if (id.equalsIgnoreCase("Bemerkung"))
					BEMERKUNG = new Field(ids.get(i), findPattern.get(i),
							index.get(i), extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
				if (id.equalsIgnoreCase("Zustaendigkeit"))
					ZUSTAENDIGKEIT = new Field(ids.get(i), findPattern.get(i),
							index.get(i), extendRight.get(i), extendDown.get(i),
							startPattern.get(i), endPattern.get(i),
							lineOffset.get(i), unique.get(i), isAlphaOnly.get(i), excludeStartpattern.get(i));
			}
			
			log.info("Configuration parsed..");

		} catch (ConfigurationException e) {
			log.error("Failed to load Configuration!", e);
			System.exit(1);
		}
	}
}
