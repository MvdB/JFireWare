package de.edvdb.ffw.system;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import de.edvdb.ffw.beans.Adresse;
import de.edvdb.ffw.enums.MarkerColor;

public abstract class Config implements IConfig {
	private static Logger log = Logger.getLogger(Config.class);
	public static final String VERSION = "0.9.6";
	
	private static XMLConfiguration config = new XMLConfiguration();
	private static final String CONFIGFILE = "../conf/config.xml";
	private static Long CONFIGTIME;
	// Hibernate Configuration
	public static String HIBERNATECONF = "../conf/hibernate.cfg.xml";
	// Puffergrösse beim Lesen der HTTP-Requests
	public static Integer BUFFERSIZE = 1024;
	public static Long PROC_WAIT = 10000l;
	// Kartentypen für Detailansicht und Anfahrskarte
	public static String MAPTYPE_DETAIL;
	public static String MAPTYPE_OVERVIEW;
	public static String MAP_PROVIDER = "OSM";
	public static String MAP_SIZE = "800x600";
	// Adresse der lokalen Feuerwehr
	public static Adresse FFWADRESSE;
	public static Integer SLEEPTIME;
	public static String FFWNAME;
	public static String TEMPDIR;

	

	public static Boolean OCRNOEXTENSION = false;

	public boolean checkConfig() {
		File configFile = new File(CONFIGFILE);
		Long lastModified = configFile.lastModified();
		if (CONFIGTIME.compareTo(lastModified) != 0) {
			CONFIGTIME = lastModified;

			initConfig();
			return true;
		} else {
			return false;
		}
	}

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

			HIBERNATECONF = config.getString("system.hibernateconf");
			SLEEPTIME = 1000 * config.getInt("system.sleeptime");
			TEMPDIR = config.getString("system.tempdir");
			MAPTYPE_DETAIL = config.getString("system.maptype[@detail]");
			MAPTYPE_OVERVIEW = config.getString("system.maptype[@overview]");
			MAP_PROVIDER = config.getString("system.mapprovider");
			MAP_SIZE = config.getString("system.mapsize");
			prepareFFWAdresse();
			log.info("Configuration parsed..");

		} catch (ConfigurationException e) {
			log.error("Failed to load Configuration!", e);
			System.exit(1);
		}
	}

	protected void prepareFFWAdresse() {
		String strasse = config.getString("personal.adresse[@strasse]");
		String hausnummer = config.getString("personal.adresse[@hausnummer]");
		String plz = config.getString("personal.adresse[@plz]");
		String ort = config.getString("personal.adresse[@ort]");
		String lat = config.getString("personal.adresse[@lat]");
		String lng = config.getString("personal.adresse[@lng]");
		FFWNAME = config.getString("personal.adresse");
		Adresse ffw = new Adresse(strasse, hausnummer, plz, ort, lat, lng, true);
		ffw.setColor(MarkerColor.green);
		FFWADRESSE = ffw;
	}

	protected void checkDirectories() {
		log.trace("Preparing directories..");
		File dir = new File(TEMPDIR);
		dir.mkdirs();
	}
}
