package de.edvdb.ffw.system;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

public class ClientConfig extends Config {
	private static Logger log = Logger.getLogger(ClientConfig.class);

	private static XMLConfiguration config = new XMLConfiguration();
	private static final String CONFIGFILE = "../conf/client.xml";
	private static Long CONFIGTIME;
	public static Integer MONITORNR, RESOLUTION_X, RESOLUTION_Y, FONTSIZE, TIMEOUTHOURS, TIMEOUTMINUTES;
	public static Boolean SOUNDENABLE, UNDECORATED, FULLSCREEN;
	public static String SOUNDFILE;
	public static Float MAPSCALE;
	public static Boolean HOOKONENABLE, HOOKOFFENABLE;
	public static String HOOKONPATH, HOOKOFFPATH;
	public static String HOOKONPARAM, HOOKOFFPARAM;
	
	public boolean checkConfig() {
		Boolean reloaded = super.checkConfig();
		File configFile = new File(CONFIGFILE);
		Long lastModified = configFile.lastModified();
		if (CONFIGTIME.compareTo(lastModified) != 0) {
			CONFIGTIME = lastModified;
			initConfig();
			reloaded = true;
		} 
		return reloaded;
	}

	public void initConfig() {
		super.initConfig();
		try {

			config = new XMLConfiguration();
			config.setValidating(false);
			config.setFileName(CONFIGFILE);
			config.load();
			if (CONFIGTIME == null) {
				CONFIGTIME = new File(CONFIGFILE).lastModified();
			}

			log.info("Configuration loaded..");
			
			MONITORNR = config.getInt("system.monitor[@nr]");
			RESOLUTION_X = config.getInt("system.monitor[@resolutionX]");
			RESOLUTION_Y = config.getInt("system.monitor[@resolutionY]");
			FULLSCREEN = config.getBoolean("system.monitor[@showFullscreen]");
			UNDECORATED = config.getBoolean("system.monitor[@undecorated]");
			MAPSCALE = config.getFloat("system.map[@scale]");
			FONTSIZE = config.getInt("system.font[@size]");
			SOUNDENABLE = config.getBoolean("system.sound[@enable]");
			SOUNDFILE = config.getString("system.sound[@file]");
			TIMEOUTHOURS = config.getInt("system.displaytimeout[@hours]");
			TIMEOUTMINUTES = config.getInt("system.displaytimeout[@minutes]");
			HOOKONENABLE = config.getBoolean("system.hookon[@enable]");
			HOOKONPATH = config.getString("system.hookon[@path]");
			HOOKONPARAM = config.getString("system.hookon[@param]");
			HOOKOFFENABLE = config.getBoolean("system.hookoff[@enable]");
			HOOKOFFPATH = config.getString("system.hookoff[@path]");
			HOOKOFFPARAM = config.getString("system.hookoff[@param]");

			super.checkDirectories();
			log.info("Configuration parsed..");
		} catch (ConfigurationException e) {
			log.error("Failed to load Configuration!", e);
			System.exit(1);
		}
	}
}
